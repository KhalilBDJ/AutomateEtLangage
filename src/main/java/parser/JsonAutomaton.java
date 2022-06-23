package parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonAutomaton extends Automaton<JsonNode> {
    public JsonAutomaton(String filename){
        file = Objects.requireNonNull(getClass().getResourceAsStream(filename));
        line = new Line(Transport.CITY_BUS);
    }

    public JsonAutomaton(){
        file = Objects.requireNonNull(getClass().getResourceAsStream("/bus.json"));
        line = new Line(Transport.CITY_BUS);
    }


    //TODO: rework plus besoin de passer un JsonNode dans les méthodes !!
    public void decodeJson() throws IOException {
        //TODO : mettre ça en attribut
        ObjectMapper mapper = new ObjectMapper();
        JsonNode bus;
        try{
             bus = mapper.readTree(file);
        }catch (JsonParseException e){
            System.err.println("Une erreur a été détecté lors de la lecture du fichier json : ");
            throw e;
        }

        line.setName(getLineName(bus));
        line.setStations(getLineStations(bus));
        line.setDirections(getDirections(bus));
    }

    public String getLineName(JsonNode jsonNode){
        return jsonNode.get("ligne").asText();
    }

    public List<Station> getLineStations(JsonNode jsonNode){
        List<Station> stations = new ArrayList<>();
        JsonNode jsonStations = jsonNode.get("horaires").get(0).get("stations");
        int stationIndex = 0;
        for (JsonNode station : jsonStations){
            boolean isTerminus = stationIndex == 0 || stationIndex == jsonStations.size()-1;
            stations.add(new Station(station.get("station").asText(), isTerminus));
            stationIndex++;
        }
        return stations;
    }

    public List<Direction> getDirections(JsonNode jsonNode){
        List<Direction> directions = new ArrayList<>();
        JsonNode jsonDirections = jsonNode.get("horaires");
        for (JsonNode direction : jsonDirections){
            Station terminusStation = line.getStationWithName(direction.get("direction").asText());
            JsonNode passages = direction.get("passages");
            JsonNode jsonStations = direction.get("stations");
            int stationIndex = 0;
            for (JsonNode station : jsonStations){
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
        System.out.println(directions);
        return directions;
    }

    public List<Route> createRoute(){
        List<Route> routes = new ArrayList<>();
        for (Direction direction : line.getDirections()){
            long routeDuration = line.durationBetweenNextStation(direction);
            if (routeDuration != 0){
                Route route = new Route(routeDuration, direction, Transport.BUS);
                routes.add(route);
            }
        }
        return routes;
    }

    public void addToNetwork(Network network){
        network.addRoutes(createRoute());
        network.addLine(line);
    }
}
