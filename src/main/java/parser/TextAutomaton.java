package parser;

import model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextAutomaton{

    public List<List<String>> duration;
    public List<List<String>> scheduled;
    protected Line line;
    public Station start;
    public Station end;

    public TextAutomaton(){
        line = new Line(Transport.CITY_BUS);
    }
    public void GetLiaisons() throws FileNotFoundException {
        File file = new File("./src/main/resources/InterCites.txt");
        Scanner scanner = new Scanner(file);
        String datas = "";
        while(scanner.hasNextLine()){
            String strline = scanner.nextLine();
            if (!strline.contains("%")){
                datas = datas + strline + "\n";
            }
        }
        List<String> texts = List.of(datas.split("//")); //On sépare les deux paragraphes en deux tableaux
        //System.out.println(texts[0] + "" + texts[1]);

        List<String> miniRoutes = List.of(texts.get(0).split("\n"));// On récupère chaque ligne du premier paragraphe
        List<String> routes = List.of(texts.get(1).split("\n")); //On récupère chaque ligne du deuxième paragraphe et on les stocke dans des tableaux
        //On crée une matrice contenant la station de départ, d'arrivée et l'heure du départ
        int j = 0;
        int i = 0;
        List<List<String>> stationToStationDuration = new ArrayList<>(); //c'est le text du haut qui contient la station de d"part arrivée et la durée du trajet
        List<List<String>> stationToStationScheduled = new ArrayList<>();//le paragraphe du bas qui contient la station de départ et d'arrivée avec l'heure de départ
        for(String miniRoute : miniRoutes){
            stationToStationDuration.add(List.of(miniRoute.split("\\s+")));
        }
        for (String route : routes) {
            stationToStationScheduled.add(List.of(route.split("\\s+")));
        }
        stationToStationScheduled.remove(0);
        duration = stationToStationDuration;
        scheduled = stationToStationScheduled;
        start = new Station(duration.get(0).get(0), true);
        end = new Station(duration.get(duration.size()-1).get(1), true);
    }

    public Station getTerminus(Station currentStation, String startTime) throws FileNotFoundException {
        GetLiaisons();
        int stationPosition;
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
    }

    public List<Station> setStations() throws FileNotFoundException {
        List<Station> stations = new ArrayList<>();
        GetLiaisons();
        stations.add(start);
        for (List<String> passage : duration){
            stations.add(new Station(passage.get(1), false));
        }
        stations.remove(stations.size()-1);
        stations.add(end);
        //System.out.println(stations);
        return stations;
    }

    protected LocalTime parseDuration(String duration){
        return LocalTime.of(Integer.parseInt(duration.substring(0,2)), Integer.parseInt(duration.substring(2)));
    }

    public List<Direction> setDirections() throws FileNotFoundException {
        GetLiaisons();
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
    }

    public void decodeTxt1() throws FileNotFoundException {
        line.setName("?");
        line.setStations(setStations());
        line.setDirections(setDirections());
        System.out.println(createRoute());
    }

    public List<Route> createRoute(){
        List<Route> routes = new ArrayList<>();
        for (Direction direction : line.getDirections()){
            long routeDuration = line.durationBetweenNextStation(direction);
            if (routeDuration != 0){
                Route route = new Route(routeDuration, direction, Transport.CITY_BUS);
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
