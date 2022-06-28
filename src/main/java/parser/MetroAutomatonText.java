package parser;

import Metro.Metro;
import exception.BadValueException;
import model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetroAutomatonText extends Automaton{

    protected File file;
    Network network;
    List<Line> lines;

    private List<Station> stations;
    private List<Metro.Rule> rules;
    private List<Metro.AR> ars;
    private List<Metro.Circuit> circuits;

    private int tempsArret;

    public MetroAutomatonText(File file, Network network) {
        this.file = file;
        this.network = network;
        lines = new ArrayList<>();
        circuits = new ArrayList<>();
        ars = new ArrayList<>();
        stations = new ArrayList<>();
        rules = new ArrayList<>();
    }
    public void Init() throws FileNotFoundException, BadValueException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()){
            String strline = scanner.nextLine();
            if (Pattern.matches("% stations", strline)){
                String nextLine = scanner.nextLine();
                if (Pattern.matches("\\w+", nextLine)){
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
                    ars.add(ar);
                    nextLine = scanner.nextLine();
                }
            }
            if (Pattern.matches("% liaisons circuit", strline)){
                Metro.Circuit circuit = new Metro.Circuit();
                scanner.nextLine();
                String nextLine = scanner.nextLine();
                while (Pattern.matches("(\\w+)(\\s+)(\\w+)\\s+\\d{1,2}(?!\\d)", nextLine)){
                    String[] split = nextLine.split(" ");
                    Metro.Lnode lnode = new Metro.Lnode(split[0],split[1],Long.parseLong(split[2]));
                    circuit.getLnodes().add(lnode);
                    circuits.add(circuit);
                    nextLine = scanner.nextLine();
                }
            }
            if (Pattern.matches("% arrêt de (?<!\\d)[0-9](?!\\d)|[1-5][0-9]|60]", strline)){
                Pattern p = Pattern.compile("(?<!\\d)[0-9](?!\\d)|[1-5][0-9]|60]");
                Matcher m = p.matcher(strline);
                if (m.find()){
                    tempsArret = Integer.parseInt(m.group(0));
                }
            }
            if (Pattern.matches("%.*(0?[0-9]|[1-5][0-9]) minutes de (0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9]) à (0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9]) et de (0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])à (0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])", strline)){
                List<String> rule = new ArrayList<>(List.of(strline.replaceAll(":", "").replaceAll("[^0-9à]", " ").split("\\s+")));
                rule.remove(0);
                int j = 1;
                while (j<rule.size()){
                    rules.add(new Metro.Rule(parseDuration(rule.get(j)), parseDuration(rule.get(j+1)), Integer.parseInt(rule.get(0))));
                    j+=2;
                }
                String nextRule=scanner.nextLine();
                if (nextRule.matches(".*(0?[0-9]|[1-5][0-9]) minutes sinon")){
                    rule = new ArrayList<>(List.of(nextRule.replaceAll(":", "").replaceAll("[^0-9à]", " ").split("\\s+")));
                    rule.remove(0);
                    Metro.Rule rule1;
                    Metro.Rule rule2;
                    int intervalle1 = Integer.parseInt(rule.get(0));
                    rule1 = new Metro.Rule(rules.get(0).getFin(), rules.get(1).getDebut(), intervalle1);
                    rules.add(rule1);
                    String finalStart = scanner.nextLine();
                    if (finalStart.matches(".*(0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])")){
                        rule = new ArrayList<>(List.of(finalStart.replaceAll(":", "").replaceAll("[^0-9à]", " ").split("\\s+")));
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
        int numberLine = 0;
        for (Metro.AR liaisonAR: ars) {
            Line line = new Line(Transport.METRO);
            line.setName("" + (++numberLine));

        }
    }

    @Override
    public List<Route> createRoute() throws BadValueException {
        return null;
    }

    @Override
    public void addToNetwork(Network network) throws BadValueException {

    }


}
