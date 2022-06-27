package parser;

import Metro.Metro;
import model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TextAutomatonBis{

    protected File file;
    protected Line line;

    //public Metro.TempsArret arret;
    public List<String> tempsDepart = new ArrayList<>();
    public TextAutomatonBis(){
       file = new File("./src/main/resources/metro.txt");
       line = new Line(Transport.METRO);
    }

    public List<Station> setStation() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<Station> stations = new ArrayList<>();
        while (scanner.hasNextLine()){
            String strline = scanner.nextLine();
            // Liaison circuit
            if (Pattern.matches("% stations", strline)){
                List<String> stationsString = List.of(scanner.nextLine().split(" "));
                for (String station: stationsString) {
                    stations.add(new Station(station, false));
                }
            }
            if (Pattern.matches("% liaisons A/R", strline) && Pattern.matches("% depart arrivee duree", scanner.nextLine())){
                List<String> stationsString = new ArrayList<>(List.of(scanner.nextLine().split(" ")));
                stationsString.remove(2);
                for (Station station : stations) {
                    for (String stationString: stationsString) {
                        if (station.getNom().equals(stationString)){
                            station.setTerminus(true);
                        }
                    }
                }
            }
        }
        //System.out.println(stations);
        return stations;
    }

    public List<List<String>> setRules() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<List<String>> rules = new ArrayList<>();
        while (scanner.hasNextLine()){

            String strline = scanner.nextLine();
            if (Pattern.matches("% arrêt de .*",strline)){
                //arret = new Metro.TempsArret(Integer.parseInt(strline.replaceAll("[A-Za-zâê% ]", "").replaceAll(":", "")));

            }
            if (Pattern.matches("% départ de .*", strline)){
                int i = 0;
                List<String> rule = new ArrayList<>(List.of(strline.replaceAll("[A-Za-zùàé%]", "").replaceAll(":", "").split("\\s+")));
                rule.remove(0);
                for (String simple : rule) {
                    if (simple.length()<=2){
                        tempsDepart.add(simple);
                        break;
                    }
                    i++;
                }
                if (i<= rule.size()-1){
                    rule.remove(i);
                }
                rules.add(rule);
            }
            if (Pattern.matches("% dernier départ .*",strline)){
                int i =0;
                List<String> rule = new ArrayList<>(List.of(strline.replaceAll("[A-Za-zùàé%]", "").replaceAll(":", "").split("\\s+")));
                rule.remove(0);
                for (String simple : rule) {
                    if (simple.length()<=2){
                        tempsDepart.add(simple);
                        break;
                    }
                    i++;
                }
                if (i<= rule.size()-1){
                    rule.remove(i);
                }

                rules.add(rule);
            }

        }
        System.out.println(rules);
        //System.out.println(arret.getA());
        System.out.println(tempsDepart);
        return rules;
    }

    public List<Direction> setDiretions() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<Station> stations = setStation();
        List<Metro.Liaison> liaisons = new ArrayList<>();
        List<List<String>> rules = setRules();

        List<Direction> directions = new ArrayList<>();

        while(scanner.hasNextLine()){
            String strline = scanner.nextLine();
            if (Pattern.matches("%.*circuit",strline)){

                scanner.nextLine();
                String autreTest= scanner.nextLine();

                while (!Pattern.matches("% .*", autreTest) && Pattern.matches("[a-zA-Z].*[0-9]", autreTest)){
                    List<String> liaisonsString = List.of(autreTest.split(" "));
                    Metro.Liaison liaison = new Metro.Liaison(searchStation(stations, liaisonsString.get(0)),searchStation(stations, liaisonsString.get(1)), Integer.parseInt(liaisonsString.get(2)));

                    liaisons.add(liaison);
                    autreTest = scanner.nextLine();
                }
                List<Passage> firstPassages = initPassages(parseDuration(rules.get(0).get(0)),parseDuration(rules.get(0).get(1)),parseDuration(rules.get(0).get(2)),parseDuration(rules.get(0).get(3)),parseDuration(rules.get(2).get(0)));
                System.out.println(firstPassages);
                Direction dir =new Direction(firstPassages,liaisons.get(liaisons.size()-1).getFinish(),liaisons.get(0).getStart());
                directions.add(dir);
                for (Metro.Liaison liaison: liaisons) {
                    List<Passage> passageInter = nextPassage(firstPassages, liaison.getDuree());
                    firstPassages = passageInter;
                    Direction dir2 = new Direction(passageInter, liaison.getFinish(),liaisons.get(0).getStart());
                    directions.add(dir2);
                }
                //System.out.println(directions);
            }

            if (Pattern.matches("% liaisons A/R",strline)){
                scanner.nextLine();
                String autreTest= scanner.nextLine();
                List<Metro.Liaison> liaisons1 = new ArrayList<>();
                while (!Pattern.matches("% .*", autreTest) && Pattern.matches("[a-zA-Z].*[0-9]", autreTest)){
                    System.out.println(autreTest);
                    List<String> liaisonsString = List.of(autreTest.split(" "));
                    Metro.Liaison liaison = new Metro.Liaison(searchStation(stations, liaisonsString.get(0)),searchStation(stations, liaisonsString.get(1)), Integer.parseInt(liaisonsString.get(2)));
                    liaisons1.add(liaison);
                    autreTest = scanner.nextLine();
                }
                List<Passage> firstPassages = initPassages(parseDuration(rules.get(0).get(0)),parseDuration(rules.get(0).get(1)),parseDuration(rules.get(0).get(2)),parseDuration(rules.get(0).get(3)), parseDuration(rules.get(2).get(0)));
                Direction firstDir = new Direction(firstPassages, liaisons1.get(0).getFinish(), liaisons1.get(0).getStart());
                directions.add(firstDir);
                liaisons1.remove(0);
                for (Metro.Liaison liaison : liaisons1) {
                    List<Passage> passageInter = nextPassage(firstPassages, liaison.getDuree());
                    firstPassages = passageInter;
                    Direction dir2 = new Direction(passageInter, liaison.getFinish(),liaison.getStart());
                    directions.add(dir2);
                }

            }
            //Direction dir =new Direction(initPassages(parseDuration(rules.get(0).get(0)),parseDuration(rules.get(0).get(1)),parseDuration(rules.get(0).get(2)),parseDuration(rules.get(0).get(3))),liaisons.get(liaisons.size()-1).getFinish(),liaisons.get(0).getStart());
            //System.out.println(dir);

        }
        //System.out.println(liaisons);
        System.out.println(directions);
        return directions;
    }

    private Station searchStation(List<Station> stations, String stationName){
        for (Station station:stations) {
            if (station.getNom().equals(stationName)){
                return station;
            }
        }
        return null;
    }

    public List<Passage> initPassages(LocalTime matin, LocalTime matin1, LocalTime aprem, LocalTime aprem2, LocalTime end){
        int j=0;
        int i=0;
        List<Passage> passages = new ArrayList<>();
        LocalTime heure;
        while (j<24){
            heure=LocalTime.of(j,i);
            //System.out.println(heure);
            if ((heure.isAfter(matin)&& heure.isBefore(matin1) || heure.equals(matin)) || (heure.isAfter(aprem) && heure.isBefore(aprem2)) || heure.equals(aprem)){
                Passage passage = new Passage(heure);
                passages.add(passage);
                i = i+ Integer.parseInt(tempsDepart.get(0));
            } else if (heure.isAfter(end)) {
                i = i+ Integer.parseInt(tempsDepart.get(1));
                break;
            } else{
                Passage passage = new Passage(heure);
                passages.add(passage);
                i = i+ Integer.parseInt(tempsDepart.get(1));
            }
            if (i == 60){
                i = 0;
                j++;
            }
        }
        return passages;
    }
    private LocalTime parseDuration(String duration){
        return LocalTime.of(Integer.parseInt(duration.substring(0,2)), Integer.parseInt(duration.substring(2)));
    }

    private List<Passage> nextPassage(List<Passage> previousPassages, int dureePassage){
        List<Passage> newPassages = new ArrayList<>();
        for (Passage passage:previousPassages) {
            newPassages.add(new Passage(passage.getSchedule().plusMinutes(dureePassage  /*arret.getA()*/)));
        }
        System.out.println(newPassages);
        return newPassages;
    }

    public List<Route> createRoute(){
        List<Route> routes = new ArrayList<>();
        for (Direction direction : line.getDirections()){
            long routeDuration = line.durationBetweenNextStation(direction);
            if (routeDuration != 0){
                Route route = new Route(routeDuration, direction, Transport.METRO);
                routes.add(route);
            }
        }
        System.out.println(routes);
        return routes;
    }
    public void decode() throws FileNotFoundException {
        line.setName("metro");
        line.setStations(setStation());
        List<Direction> directions = setDiretions();
        System.out.println(directions);
        line.setDirections(directions);
        System.out.println(createRoute());
    }

}
