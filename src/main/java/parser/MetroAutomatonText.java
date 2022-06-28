package parser;

import Metro.Metro;
import exception.BadValueException;
import model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetroAutomatonText extends Automaton{
    Network network;
    List<Line> lines;

    private List<Station> stations;
    private List<Metro.Rule> rules;
    private List<Metro.AR> ars;
    private List<Metro.Circuit> circuits;

    private int tempsArret;

    public MetroAutomatonText(Network network) throws BadValueException, FileNotFoundException {
        this.file = Objects.requireNonNull(getClass().getResourceAsStream("/metro.txt"));;
        this.network = network;
        lines = new ArrayList<>();
        circuits = new ArrayList<>();
        ars = new ArrayList<>();
        stations = new ArrayList<>();
        rules = new ArrayList<>();
        Init();
    }
    public void Init() throws FileNotFoundException, BadValueException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()){
            String strline = scanner.nextLine();
            if (Pattern.matches("% stations", strline)){
                String nextLine = scanner.nextLine();
                if (Pattern.matches("(\\w+\\s*)*", nextLine)){
                    for (String station: nextLine.split(" ")) {
                        stations.add(new Station(station, false));
                    }
                }
            }
            if (Pattern.matches("% liaisons A/R", strline)){
                Metro.AR ar = new Metro.AR();
                scanner.nextLine();
                String nextLine = scanner.nextLine();
                while (Pattern.matches("(\\w+)(\\s+)(\\w+)\\s+\\d{1,2}(?!\\d)", nextLine)){
                    String[] split = nextLine.split(" ");
                    Metro.Lnode lnode = new Metro.Lnode(split[0],split[1],Long.parseLong(split[2]));
                    ar.getLnodes().add(lnode);
                    nextLine = scanner.nextLine();
                }
                ars.add(ar);
            }
            if (Pattern.matches("% liaisons circuit", strline)){
                Metro.Circuit circuit = new Metro.Circuit();
                scanner.nextLine();
                String nextLine = scanner.nextLine();
                while (Pattern.matches("(\\w+)(\\s+)(\\w+)\\s+\\d{1,2}(?!\\d)", nextLine)){
                    String[] split = nextLine.split(" ");
                    Metro.Lnode lnode = new Metro.Lnode(split[0],split[1],Long.parseLong(split[2]));
                    circuit.getLnodes().add(lnode);
                    nextLine = scanner.nextLine();
                }
                circuits.add(circuit);
            }
            if (Pattern.matches("% arrêt de \\d minutes en station", strline)){
                Pattern p = Pattern.compile("(?<!\\d)[0-9](?!\\d)|[1-5][0-9]|60]");
                Matcher m = p.matcher(strline);
                if (m.find()){
                    tempsArret = Integer.parseInt(m.group(0));
                }
            }
            if (Pattern.matches("%.*(0?[0-9]|[1-5][0-9]) minutes de (0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9]) à (0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9]) et de (0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])à (0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])", strline)){
                List<String> rule = new ArrayList<>(List.of(strline.replaceAll(":", "").replaceAll("[^0-9]", " ").split("\\s+")));
                rule.remove(0);
                int j = 1;
                while (j<rule.size()){
                    rules.add(new Metro.Rule(parseDuration(rule.get(j)), parseDuration(rule.get(j+1)), Integer.parseInt(rule.get(0))));
                    j+=2;
                }
                String nextRule=scanner.nextLine();
                if (nextRule.matches(".*(0?[0-9]|[1-5][0-9]) minutes sinon")){
                    rule = new ArrayList<>(List.of(nextRule.replaceAll(":", "").replaceAll("[^0-9]", " ").split("\\s+")));
                    rule.remove(0);
                    Metro.Rule rule1;
                    Metro.Rule rule2;
                    int intervalle1 = Integer.parseInt(rule.get(0));
                    rule1 = new Metro.Rule(rules.get(0).getFin(), rules.get(1).getDebut(), intervalle1);
                    rules.add(rule1);
                    String finalStart = scanner.nextLine();
                    if (finalStart.matches(".*(0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])")){
                        rule = new ArrayList<>(List.of(finalStart.replaceAll(":", "").replaceAll("[^0-9]", " ").split("\\s+")));
                        rule.remove(0);
                        String lastStart = rule.get(0);
                        rule2 = new Metro.Rule(rules.get(1).getFin(), parseDuration(lastStart), intervalle1);
                        rules.add(rule2);
                    }
                }
            }
        }
    }
    @Override
    public void addNetworkGlobalStations() {
        for (Station station: stations) {
            Station startStation = network.getStationWithName(station.getNom());
            if (startStation == null){
                network.addStation(new Station(station.getNom(), false));
            }
        }
    }

    @Override
    public void defineLines() throws BadValueException {
        addNetworkGlobalStations();

        int numberLine = 0;
        for (Metro.AR liaisonAR: ars) {
            Line line = new Line(Transport.METRO);
            line.setName("" + (++numberLine));
            line.setStations(liaisonAR.getAllStations());

            List<Direction> directions = new ArrayList<>();

            Direction direction = new Direction();

            List<Passage> passages = initPassages();

            Station current = line.getStations().get(0);
            Station next = line.getStations().get(1);

            direction.setTerminus(line.determineTerminus(current, next));
            direction.setPassages(passages);
            direction.setCurrentStation(current);
            directions.add(direction);

            for (int i = 1; i < line.getStations().size()-1; i++) {
                direction = new Direction();
                current = line.getStations().get(i);
                next = line.getStations().get(i+1);

                Station localStation = current;

                long duration = liaisonAR.getLnodes().stream().filter(lnode -> lnode.getEnd().equals(localStation.getNom())).findFirst().get().getDuration();
                passages = nextPassage(passages, (int) duration);
                direction.setTerminus(line.determineTerminus(current, next));
                direction.setPassages(passages);
                direction.setCurrentStation(current);
                directions.add(direction);
            }
            line.setDirections(directions);
            lines.add(line);
        }

        for(Metro.Circuit liaisonCircuit: circuits){
            Line line = new Line(Transport.METRO);
            line.setName("" + (++numberLine));
            line.setStations(liaisonCircuit.getAllStations());

            List<Direction> directions = new ArrayList<>();

            Direction direction = new Direction();

            List<Passage> passages = initPassages();

            Station current = line.getStations().get(0);
            Station next = line.getStations().get(1);

            direction.setTerminus(line.determineTerminus(current, next));
            direction.setPassages(passages);
            direction.setCurrentStation(current);
            directions.add(direction);

            for (int i = 1; i < line.getStations().size()-1; i++) {
                direction = new Direction();
                current = line.getStations().get(i);
                next = line.getStations().get(i+1);

                Station localStation = current;

                long duration = liaisonCircuit.getLnodes().stream().filter(lnode -> lnode.getEnd().equals(localStation.getNom())).findFirst().get().getDuration();
                passages = nextPassage(passages, (int) duration);

                direction.setTerminus(line.determineTerminus(current, next));
                direction.setPassages(passages);
                direction.setCurrentStation(current);
                directions.add(direction);
            }
            line.setDirections(directions);
            lines.add(line);
        }
    }

    public List<Passage> initPassages(){
        int j=7;
        int i=0;

        LocalTime matinDebut = rules.get(0).getDebut();
        LocalTime matinFin = rules.get(0).getFin();
        LocalTime apremDebut = rules.get(1).getDebut();
        LocalTime apremFin = rules.get(1).getFin();
        LocalTime end = rules.get(3).getFin();

        List<Passage> passages = new ArrayList<>();
        LocalTime heure;
        while (j<24){
            heure=LocalTime.of(j,i);
            if ((heure.isAfter(matinDebut)&& heure.isBefore(matinFin) || heure.equals(matinDebut)) || (heure.isAfter(apremDebut) && heure.isBefore(apremFin)) || heure.equals(apremDebut)){
                Passage passage = new Passage(heure);
                passages.add(passage);
                i = i+ rules.get(0).getIntervalle();
            } else if (heure.isAfter(end)) {
                break;
            } else{
                Passage passage = new Passage(heure);
                passages.add(passage);
                i = i+ rules.get(1).getIntervalle();
            }
            if (i == 60){
                i = 0;
                j++;
            }
        }
        return passages;
    }

    private List<Passage> nextPassage(List<Passage> previousPassages, int dureePassage){
        List<Passage> newPassages = new ArrayList<>();
        for (Passage passage:previousPassages) {
            newPassages.add(new Passage(passage.getSchedule().plusMinutes(dureePassage+tempsArret)));
        }
        return newPassages;
    }

    @Override
    public List<Route> createRoute() throws BadValueException {
        defineLines();

        List<Route> routes = new ArrayList<>();
        for (Line line : lines){
            for (Direction direction : line.getDirections()){
                long routeDuration = line.durationBetweenNextStation(direction)-2;
                if (routeDuration != 0){
                    if (routeDuration < 0){
                        routeDuration = getGoodDuration(line, direction);
                    }
                    Route route = new Route(routeDuration, direction, Transport.METRO);
                    routes.add(route);
                }
            }
        }
        return routes;
    }

    private long getGoodDuration(Line line, Direction direction){
        Station current = direction.getCurrentStation();
        // TODO : numéroté les AR et circuit avec même numéro que ligne et rechercher dedans
        return 0;
    }

    @Override
    public void addToNetwork(Network network) throws BadValueException {
        network.addRoutes(createRoute());
        network.addLines(lines);
    }


}
