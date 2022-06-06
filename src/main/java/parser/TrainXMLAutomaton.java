package parser;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Getter;
import model.*;
import model.xml.TrainPojo;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class TrainXMLAutomaton {
    List<Line> lines;
    InputStream file;
    TrainPojo.Horaires horaires;
    Network network;

    public TrainXMLAutomaton(Network network) throws IOException {
        this.network = network;
        file = Objects.requireNonNull(getClass().getResourceAsStream("/train.xml"));
        lines = new ArrayList<>();
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);
        horaires = xmlMapper.readValue(file, TrainPojo.Horaires.class);
    }

    public TrainXMLAutomaton(Network network, String filename) throws IOException {
        this.network = network;
        file = Objects.requireNonNull(getClass().getResourceAsStream(filename));
        lines = new ArrayList<>();
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);
        horaires = xmlMapper.readValue(file, TrainPojo.Horaires.class);
    }

    private void addNetworkGlobalStations() {
        for (TrainPojo.Line line : horaires.getLines()) {
            for (TrainPojo.Junction junction : line.getJunctions()) {
                Station startStation = network.getStationWithName(junction.getStartStation().getValue());
                if (startStation == null) {
                    network.addStation(new Station(junction.getStartStation().getValue(), false));
                }
                Station arrivalStation = network.getStationWithName(junction.getArrivalStation().getValue());
                if (arrivalStation == null) {
                    network.addStation(new Station(junction.getArrivalStation().getValue(), false));
                }
            }
        }
    }

    public void defineLines() {
        addNetworkGlobalStations();

        for (TrainPojo.Line ligne : horaires.getLines()) {
            Line line = new Line(Transport.TRAIN);
            line.setName(ligne.getNom().replace("\n", "").replace(" ", ""));
            List<Station> stations = getStationsFromLine(ligne);

            List<Station> terminus = new ArrayList<>();
            Station firstTerminus = stations.get(0);
            firstTerminus.setTerminus(true);
            Station secondTerminus = stations.get(stations.size() - 1);
            secondTerminus.setTerminus(true);
            terminus.add(firstTerminus);
            terminus.add(secondTerminus);
            line.setStations(stations);

            System.out.println(stations);

            boolean cycle = isCyclePresent(firstTerminus, ligne);


            List<Direction> directions = new ArrayList<>();

            if (cycle){
                for (int i = 0; i < stations.size()-1; i++) {
                    Direction direction = new Direction();
                    List<Passage> passages = new ArrayList<>();
                    Station current = stations.get(i);
                    Station next = stations.get(i+1);

                    List<TrainPojo.Junction> junctions = ligne.getJunctions().stream().filter(junction -> junction.getStartStation().getValue().equals(current.getNom())
                            && junction.getArrivalStation().getValue().equals(next.getNom())).toList();

                    for (TrainPojo.Junction junction : junctions){
                        Passage passage = new Passage(parseDuration(junction.getStartHour().getValue()), parseDuration(junction.getArrivalHour().getValue()));
                        passages.add(passage);
                    }
                    direction.setCurrentStation(current);
                    direction.setPassages(passages);
                    direction.setTerminus(stations.get(stations.size()-1));
                    directions.add(direction);
                }
                // TODO : ici gérer le cas où dernière direction
                Direction lastdirection = new Direction();
                List<Passage> lastPassages = new ArrayList<>();
                List<TrainPojo.Junction> lastJunctions = ligne.getJunctions().stream().filter(junction -> junction.getArrivalStation().getValue().equals(stations.get(stations.size()-1).getNom())).toList();
                for (TrainPojo.Junction junction : lastJunctions){
                    Passage passage = new Passage(parseDuration(junction.getStartHour().getValue()), parseDuration(junction.getArrivalHour().getValue()));
                    lastPassages.add(passage);
                }
                lastdirection.setCurrentStation(stations.get(stations.size()-1));
                lastdirection.setPassages(lastPassages);
                lastdirection.setTerminus(stations.get(stations.size()-1));
                directions.add(lastdirection);

                //TODO : faire les directions dans l'autres sens
                for (int i = stations.size()-1; i > 0; i--) {
                    Direction direction = new Direction();
                    List<Passage> passages = new ArrayList<>();
                    Station current = stations.get(i);
                    Station next = stations.get(i-1);

                    List<TrainPojo.Junction> junctions = ligne.getJunctions().stream().filter(junction -> junction.getStartStation().getValue().equals(current.getNom())
                            && junction.getArrivalStation().getValue().equals(next.getNom())).toList();

                    for (TrainPojo.Junction junction : junctions){
                        Passage passage = new Passage(parseDuration(junction.getStartHour().getValue()), parseDuration(junction.getArrivalHour().getValue()));
                        passages.add(passage);
                    }
                    direction.setCurrentStation(current);
                    direction.setPassages(passages);
                    direction.setTerminus(stations.get(0));
                    directions.add(direction);
                }

                lastdirection = new Direction();
                lastPassages = new ArrayList<>();
                lastJunctions = ligne.getJunctions().stream().filter(junction -> junction.getArrivalStation().getValue().equals(stations.get(0).getNom())).toList();
                for (TrainPojo.Junction junction : lastJunctions){
                    Passage passage = new Passage(parseDuration(junction.getStartHour().getValue()), parseDuration(junction.getArrivalHour().getValue()));
                    lastPassages.add(passage);
                }
                lastdirection.setCurrentStation(stations.get(0));
                lastdirection.setPassages(lastPassages);
                lastdirection.setTerminus(stations.get(0));
                directions.add(lastdirection);
            }else{
                System.out.println("PAS DE CYCLE");
                for (Station station1 : stations) {
                    for (Station station2 : stations) {
                        //INIT
                        Direction direction = new Direction();
                        List<Passage> passages = new ArrayList<>();

                        List<TrainPojo.Junction> junctions = ligne.getJunctions().stream().filter(junction -> junction.getStartStation().getValue().equals(station1.getNom())
                                && junction.getArrivalStation().getValue().equals(station2.getNom())).toList();

                        List<TrainPojo.Junction>  junctions2 = ligne.getJunctions().stream().filter(junction -> junction.getArrivalStation().getValue().equals(station2.getNom())).toList();
                        if (station1.equals(station2) && station2.isTerminus() && !junctions2.isEmpty()){
                            Direction direction2 = new Direction();
                            List<Passage> passages2 = new ArrayList<>();
                            for (TrainPojo.Junction junction : junctions2){
                                Passage passage = new Passage(parseDuration(junction.getArrivalHour().getValue()));
                                passages2.add(passage);
                            }
                            direction2.setCurrentStation(station1);
                            direction2.setPassages(passages2);
                            direction2.setTerminus(station2);
                            directions.add(direction2);
                        }

                        if (!junctions.isEmpty()){
                            for (TrainPojo.Junction junction : junctions){
                                Passage passage = new Passage(parseDuration(junction.getStartHour().getValue()), parseDuration(junction.getArrivalHour().getValue()));
                                passages.add(passage);
                            }
                            direction.setCurrentStation(station1);
                            direction.setPassages(passages);
                            direction.setTerminus(line.determineTerminus(station1, station2));
                            directions.add(direction);
                        }
                    }
                }
            }

            line.setDirections(directions);
            lines.add(line);
        }
    }

    private List<Station> getStationsFromLine(TrainPojo.Line line) {
        List<Station> stations = new ArrayList<>();
        for (TrainPojo.Junction junction : line.getJunctions()) {
            Station startStation = network.getStationWithName(junction.getStartStation().getValue());
            if (!stations.contains(startStation) && startStation != null) {
                stations.add(startStation);
            }
            Station arrivalStation = network.getStationWithName(junction.getArrivalStation().getValue());
            if (!stations.contains(arrivalStation) && arrivalStation != null) {
                stations.add(arrivalStation);
            }
        }
        return stations;
    }

    public List<Route> createRoute(){
        defineLines();
        List<Route> routes = new ArrayList<>();
        for (Line line : lines){
            for (Direction direction : line.getDirections()){
                System.out.println(direction);
                long routeDuration = line.durationBetweenNextStation(direction);
                if (routeDuration != 0){
                    Route route = new Route(routeDuration, direction, Transport.TRAIN);
                    routes.add(route);
                }
            }
        }
        return routes;
    }

    public void addToNetwork(Network network){
        network.addRoutes(createRoute());
        network.addLines(lines);
    }

    protected LocalTime parseDuration(String duration) {
        return LocalTime.of(Integer.parseInt(duration.substring(0, 2)), Integer.parseInt(duration.substring(2)));
    }

    private boolean isCyclePresent(Station terminus, TrainPojo.Line line) {
        return line.getJunctions().stream().anyMatch(junction -> junction.getStartStation().getValue().equals(terminus.getNom())) && line.getJunctions().stream().anyMatch(junction -> junction.getArrivalStation().getValue().equals(terminus.getNom()));
    }
}
