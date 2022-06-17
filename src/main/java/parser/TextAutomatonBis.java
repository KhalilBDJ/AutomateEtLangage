package parser;

import Metro.Metro;
import model.*;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TextAutomatonBis{

    protected File file;
    protected Line line;

    public Metro.TempsArret arret;
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

    public List<List<String>> setRules() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<List<String>> rules = new ArrayList<>();
        while (scanner.hasNextLine()){

            String strline = scanner.nextLine();
            if (Pattern.matches("% arrêt de .*",strline)){
                arret = new Metro.TempsArret(Integer.parseInt(strline.replaceAll("[A-Za-zâê% ]", "").replaceAll(":", "")));

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
        System.out.println(arret.getA());
        System.out.println(tempsDepart);
        return rules;
    }




}
