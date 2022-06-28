package parser;

import exception.BadValueException;
import model.*;
import model.text.InterCite;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class InterCiteTextAutomaton extends Automaton{

    // [{Sdépart}, {Sarriv}, {durée}]

    public List<InterCite.Duration> durations;

    //[{Sdépart}, {Sarriv}, {heure départ}]

    public List<InterCite.Scheduled> scheduleds;

    protected Line line;
    public Station start;
    public Station end;

    public InputStream file;

    private Network network;

    public InterCiteTextAutomaton(Network network, String filename) throws IOException, BadValueException {
        this.network = network;
        file = Objects.requireNonNull(getClass().getResourceAsStream(filename));
        line = new Line(Transport.CITY_BUS);
        GetLiaisons();
    }

    public InterCiteTextAutomaton(Network network) throws IOException, BadValueException {
        this(network, "/InterCites.txt");
    }

    // récup deux tableau :
    //  - durée entre chaque station
    //  - tout les passages

    public void GetLiaisons() throws IOException, BadValueException {
        Scanner scanner = new Scanner(file);
        String datas = "";
        String verif = "";

        // On recup l'ensemble du fichier
        while(scanner.hasNextLine()){
            String strline = scanner.nextLine();
            if(!strline.contains("%") && !strline.matches("//")){
                verif += strline + "\n";
            }else if(strline.matches("//")){
                verif += strline;
            }

            if(strline.matches("(\\w+)(\\s+)(\\w+)\\s+\\d{1,2}(?!\\d)")){
                datas = datas + strline + "\n";
            } else if (strline.matches("(\\w+)\\s+(\\w+)\\s+\\d{4}")) {
                datas = datas + strline + "\n";
            } else if (strline.matches("//")) {
                datas = datas + strline;
            }
        }

        if (verif.length() != datas.length()){
            throw new IOException();
        }

        List<String> texts = List.of(datas.split("//")); //On sépare les deux paragraphes en deux tableaux

        List<String> tempDurations = List.of(texts.get(0).split("\n"));// On récupère chaque ligne du premier paragraphe
        List<String> tempScheduleds = List.of(texts.get(1).split("\n")); //On récupère chaque ligne du deuxième paragraphe et on les stocke dans des tableaux

        //On crée une matrice contenant la station de départ, d'arrivée et l'heure du départ

        List<InterCite.Duration> stationToStationDuration = new ArrayList<>(); //c'est le text du haut qui contient la station de d"part arrivée et la durée du trajet
        List<InterCite.Scheduled> stationToStationScheduled = new ArrayList<>();//le paragraphe du bas qui contient la station de départ et d'arrivée avec l'heure de départ

        for(String tempDuration : tempDurations){
            List<String> tempSplitDuration = List.of(tempDuration.split("\\s+"));
            stationToStationDuration.add(new InterCite.Duration(tempSplitDuration.get(0), tempSplitDuration.get(1), Long.parseLong(tempSplitDuration.get(2))));
        }
        for (String tempScheduled : tempScheduleds) {
            List<String> tempSplitDuration = List.of(tempScheduled.split("\\s+"));
            stationToStationScheduled.add(new InterCite.Scheduled(tempSplitDuration.get(0), tempSplitDuration.get(1), parseDuration(tempSplitDuration.get(2))));
        }

        durations = stationToStationDuration;
        scheduleds = stationToStationScheduled;

        start = new Station(durations.get(0).getStationDepart(), true);
        end = new Station(durations.get(durations.size()-1).getStationArrivee(), true);
    }

    public List<Station> getStationsFromLine(){
        List<Station> stations = new ArrayList<>();
        stations.add(start);
        for (int i = 1; i < durations.size(); i++) {
            stations.add(new Station(durations.get(i).getStationDepart(), false));
        }
        stations.add(end);
        return stations;
    }

    @Override
    public void addNetworkGlobalStations() {
        for (InterCite.Duration duration : durations) {
            Station startStation = network.getStationWithName(duration.getStationArrivee());
            if (startStation == null) {
                network.addStation(new Station(duration.getStationDepart(), false));
            }
            Station arrivalStation = network.getStationWithName(duration.getStationArrivee());
            if (arrivalStation == null) {
                network.addStation(new Station(duration.getStationArrivee(), false));
            }
        }
    }

    @Override
    public void defineLines() throws BadValueException {
        addNetworkGlobalStations();

        List<Direction> directions = new ArrayList<>();
        line.setStations(getStationsFromLine());

        Map<String, List<String>> visited = new HashMap<>();

        for (Station station : line.getStations()){
            visited.put(station.getNom(), new ArrayList<>());
        }

        for (InterCite.Scheduled scheduled : scheduleds){
            if (!visited.get(scheduled.getStationDepart()).contains(scheduled.getStationArrivee())){
                Station start = line.getStationWithName(scheduled.getStationDepart());
                Station end = line.getStationWithName(scheduled.getStationArrivee());
                List<Passage> passages = getAllPassages(start.getNom(), end.getNom());
                Station terminus = line.determineTerminus(start, end);
                Direction direction = new Direction(passages, terminus, start);
                directions.add(direction);
                visited.get(start.getNom()).add(end.getNom());
            }
        }
        line.setDirections(directions);
    }

    private List<Passage> getAllPassages(String start, String end){
        List<Passage> passages = new ArrayList<>();

        List<InterCite.Scheduled> allScheduledsForStartAndEnd = scheduleds.stream().filter(scheduled -> scheduled.getStationDepart().equals(start) && scheduled.getStationArrivee().equals(end)).toList();
        for (InterCite.Scheduled scheduled : allScheduledsForStartAndEnd){
            passages.add(new Passage(scheduled.getScheduled()));
        }
        return passages;
    }

/*    public List<Direction> setDirections() throws IOException {
        List<Station> stations = setStations();
        List<Direction> directions = new ArrayList<>();
        for (Station station:stations) {
            List<Passage> towardsStart = new ArrayList<>();
            List<Passage> towardsEnd= new ArrayList<>();
            for (List<String> passage: scheduled) {
                if (passage.get(0).equals(station.getNom()) && getTerminus(station, passage.get(2)).equals(start)){
                    towardsStart.add(new Passage(parseDuration(passage.get(2))));
                }
                if (passage.get(0).equals(station.getNom()) && getTerminus(station, passage.get(2)).equals(end)){
                    towardsEnd.add(new Passage(parseDuration(passage.get(2))));
                }
            }
            directions.add(new Direction(towardsStart, start, station));
            directions.add(new Direction(towardsEnd, end, station));
        }
        System.out.println(directions);
        return directions;
    }*/

/*    public Station getTerminus(Station currentStation, String startTime) throws IOException {
        Station terminus = null;
        if (currentStation.isTerminus() && currentStation.equals(start)){
            return end;
        }
        if (currentStation.isTerminus() && currentStation.equals(end)){
            return start;
        }
        int counter = 0;
        for (List<String> station: scheduled) {
            if (station.get(0).equals(currentStation.getNom()) && station.get(2).equals(startTime)){
                break;
            }
            else {
                counter++;
            }
        }
        while (counter<scheduled.size()){
            if (scheduled.get(counter).get(1).equals(start.getNom()) || scheduled.get(counter).get(1).equals(end.getNom())){
                terminus = new Station(scheduled.get(counter).get(1), true);
                break;
            }
            counter++;

        }
        //System.out.println(terminus);
        return terminus;
    }*/

    public List<Route> createRoute() throws BadValueException {
        defineLines();
        List<Route> routes = new ArrayList<>();
        for (Direction direction : line.getDirections()){
            long routeDuration = findDurationWithStationName(direction.getCurrentStation().getNom(), direction.getTerminus());
            if (routeDuration != 0){
                Route route = new Route(routeDuration, direction, Transport.CITY_BUS);
                routes.add(route);
            }
        }

        return routes;
    }

    private long findDurationWithStationName(String stationName, Station terminus){
        boolean normalSens = line.isNormalSens(terminus);

        if (stationName.equals(terminus.getNom())){
            return 0;
        }
        if (normalSens){
            return durations.stream().filter(duration -> duration.getStationDepart().equals(stationName)).findFirst().get().getDuree();
        }else {
            return durations.stream().filter(duration -> duration.getStationArrivee().equals(stationName)).findFirst().get().getDuree();
        }

        //return durations.stream().filter(duration -> duration.getStationDepart().equals(stationName) || duration.getStationArrivee().equals(st)).findFirst().get().getDuree();
    }

    public void addToNetwork(Network network) throws BadValueException {
        network.addRoutes(createRoute());
        network.addLine(line);
    }


}
