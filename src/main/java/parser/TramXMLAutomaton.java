package parser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import exception.BadValueException;
import lombok.Getter;
import model.*;
import model.xml.TramPojo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class TramXMLAutomaton extends Automaton{
    List<Line> lines;
    InputStream file;
    TramPojo.Reseau reseau;
    Network network;

    public TramXMLAutomaton(Network network) throws IOException {
        this.network = network;
        file = Objects.requireNonNull(getClass().getResourceAsStream("/tram.xml"));
        lines = new ArrayList<>();
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);
        try {
            reseau = xmlMapper.readValue(file, TramPojo.Reseau.class);
        }catch (JsonMappingException e){
            System.err.println("Une erreur a été détecté dans la lacture du fichier XML : ");
            throw e;
        }
    }

    public TramXMLAutomaton(Network network, String filename) throws IOException {
        this.network = network;
        file = Objects.requireNonNull(getClass().getResourceAsStream(filename));
        lines = new ArrayList<>();
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);
        try {
            reseau = xmlMapper.readValue(file, TramPojo.Reseau.class);
        }catch (JsonMappingException e){
            System.err.println("Une erreur a été détecté dans la lacture du fichier XML : ");
            throw e;
        }
    }

    public void addNetworkGlobalStations(){
        String[] stringStations = reseau.getStations().getValue().split(" ");
        for (String stationName : stringStations){
            Station startStation = network.getStationWithName(stationName);
            if (startStation == null) {
                network.addStation(new Station(stationName, false));
            }
        }
    }

    public void defineLines() throws BadValueException {
        addNetworkGlobalStations();

        for (TramPojo.Ligne ligne : reseau.getLignes().getLigne()){
            Line line = new Line(Transport.TRAM);
            line.setName(ligne.getNom().replace("\n", "").replace(" ", ""));

            List<Station> stations = new ArrayList<>();
            List<Direction> directions = new ArrayList<>();
            String[] stringStations = ligne.getStations().getValue().split(" ");

            int stationIndex = 0;
            for (String stationValue : stringStations){
                Station station = network.getStationWithName(stationValue);
                station.setOrder(stationIndex);
                station.setTerminus(stationIndex == 0 || stationIndex == stringStations.length-1);
                stations.add(station);
                stationIndex++;
            }
            line.setStations(stations);

            stationIndex = 0;
            for (Station station : line.getStations()){
                Direction direction = new Direction();
                List<Passage> passages = new ArrayList<>();

                for (int i = 0; i < ligne.getHorairePassages().size(); i++) {
                    String[] stringPassages = ligne.getHorairePassages().get(i).getValue().split(" ");
                    Passage passage = new Passage(parseDuration(stringPassages[stationIndex]));
                    passages.add(passage);
                }

                direction.setPassages(passages);
                direction.setTerminus(line.getStations().get(line.getStations().size()-1));
                direction.setCurrentStation(station);
                directions.add(direction);
                stationIndex++;
            }
            line.setDirections(directions);
            lines.add(line);
        }
    }

    public List<Route> createRoute() throws BadValueException {
        defineLines();
        List<Route> routes = new ArrayList<>();
        for (Line line : lines){
            for (Direction direction : line.getDirections()){
                long routeDuration = line.durationBetweenNextStation(direction);
                System.out.println(direction);
                System.out.println(routeDuration);
                if (routeDuration != 0){
                    Route route = new Route(routeDuration, direction, Transport.TRAM);
                    routes.add(route);
                }
            }
        }
        return routes;
    }

    public void addToNetwork(Network network) throws BadValueException {
        network.addRoutes(createRoute());
        network.addLines(lines);
    }

    //TODO : add validation to verify if same number of station and schedule
}
