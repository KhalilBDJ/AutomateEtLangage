package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.BadValueException;
import model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonAutomaton extends Automaton {

    Network network;
    private JsonNode bus;
    Line line;

    public JsonAutomaton(Network network, String filename) throws IOException {
        file = Objects.requireNonNull(getClass().getResourceAsStream(filename));
        line = new Line(Transport.BUS);
        this.network = network;
        ObjectMapper mapper = new ObjectMapper();
        try {
            bus = mapper.readTree(file);
        } catch (IOException e) {
            System.err.println("Une erreur a été détecté lors de la lecture du fichier json : ");
            throw e;
        }
        line.setName(getLineName());
    }

    public JsonAutomaton(Network network) throws IOException {
        this(network, "/bus.json");
    }

    @Override
    public void addNetworkGlobalStations() {
        for (JsonNode horaire : bus.get("horaires")) {
            for (JsonNode station : horaire.get("stations")) {
                Station startStation = network.getStationWithName(station.get("station").asText());
                if (startStation == null) {
                    network.addStation(new Station(station.get("station").asText(), false));
                }
            }
        }
    }

    public String getLineName() {
        return bus.get("ligne").asText();
    }

    public List<Station> getStationsFromLine() {
        List<Station> stations = new ArrayList<>();
        JsonNode jsonStations = bus.get("horaires").get(0).get("stations");
        int stationIndex = 0;
        for (JsonNode station : jsonStations) {
            boolean isTerminus = stationIndex == 0 || stationIndex == jsonStations.size() - 1;
            stations.add(new Station(station.get("station").asText(), isTerminus));
            stationIndex++;
        }
        return stations;
    }

    public void defineLines() throws BadValueException {
        addNetworkGlobalStations();

        List<Direction> directions = new ArrayList<>();
        JsonNode jsonDirections = bus.get("horaires");
        line.setStations(getStationsFromLine());
        for (JsonNode direction : jsonDirections) {
            Station terminusStation = line.getStationWithName(direction.get("direction").asText());
            JsonNode passages = direction.get("passages");
            JsonNode jsonStations = direction.get("stations");
            int stationIndex = 0;
            for (JsonNode station : jsonStations) {
                Station currentStation = line.getStationWithName(station.get("station").asText());

                Direction dir = new Direction();
                List<Passage> passages1 = new ArrayList<>();

                for (int i = 0; i < line.getStations().size(); i++) {
                    Passage passage = new Passage(parseDuration(passages.get(i).get(stationIndex).asText()));
                    passages1.add(passage);
                }

                dir.setCurrentStation(currentStation);
                dir.setTerminus(terminusStation);
                dir.setPassages(passages1);
                directions.add(dir);
                stationIndex++;
            }
        }
        line.setDirections(directions);
    }

    public List<Route> createRoute() throws BadValueException {
        defineLines();
        List<Route> routes = new ArrayList<>();
        for (Direction direction : line.getDirections()) {
            long routeDuration = line.durationBetweenNextStation(direction);
            if (routeDuration != 0) {
                Route route = new Route(routeDuration, direction, Transport.BUS);
                routes.add(route);
            }
        }
        return routes;
    }

    public void addToNetwork(Network network) throws BadValueException {
        network.addRoutes(createRoute());
        network.addLine(line);
    }
}
