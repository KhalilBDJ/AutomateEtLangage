package parser;

import model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TextAutomatonBis{

    protected File file;
    protected Line line;
    public TextAutomatonBis(){
       file = new File("./src/main/resources/metro.txt");
       line = new Line(Transport.METRO);
    }

    public List<Station> setStation() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<Station> stations = new ArrayList<>();
        while (scanner.hasNextLine()){
            String strline = scanner.nextLine();
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
        System.out.println(stations);
        return stations;
    }




}
