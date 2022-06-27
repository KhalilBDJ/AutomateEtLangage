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
    }
    public void Init() throws FileNotFoundException {
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
            if (Pattern.matches("% départ de Gare toutes les (?<!\\d)[0-9](?!\\d)|[1-5][0-9]|60 minutes de [0-1][0-9]|[2][0-3]:[0-5][0-9] à  [0-1][0-9]|[2][0-3]:[0-5][0-9] et de [0-1][0-9]|[2][0-3]:[0-5][0-9]à [0-1][0-9]|[2][0-3]:[0-5][0-9]", strline)){

            }

        }
    }
    @Override
    public void addNetworkGlobalStations() {

    }

    @Override
    public void defineLines() throws BadValueException {

    }

    @Override
    public List<Route> createRoute() throws BadValueException {
        return null;
    }

    @Override
    public void addToNetwork(Network network) throws BadValueException {

    }


}
