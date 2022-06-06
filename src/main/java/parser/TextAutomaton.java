package parser;

import model.Station;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TextAutomaton {

    public String[][] duration;
    public String[][] scheduled;
    public void GetLiaisons() throws FileNotFoundException {
        File file = new File("/Users/khalilbedjaoui/IdeaProjects/TP_AutomateEtLangage/src/main/resources/InterCites.txt");
        Scanner scanner = new Scanner(file);
        String datas = "";
        while(scanner.hasNextLine()){
            String strline = scanner.nextLine();
            if (!strline.contains("%")){
                datas = datas + strline + "\n";
            }
        }
        String[] texts = datas.split("//"); //On sépare les deux paragraphes en deux tableaux
        //System.out.println(texts[0] + "" + texts[1]);

        String[] miniRoutes = texts[0].split("\n");// On récupère chaque ligne du premier paragraphe
        String[] routes = texts[1].split("\n"); //On récupère chaque ligne du deuxième paragraphe et on les stocke dans des tableaux
        //On crée une matrice contenant la station de départ, d'arrivée et l'heure du départ
        int j = 0;
        int i = 0;
        String[][] stationToStationDuration = new String[miniRoutes.length][]; //c'est le text du haut qui contient la station de d"part arrivée et la durée du trajet
        String[][] stationToStationScheduled = new String[routes.length][];//le paragraphe du bas qui contient la station de départ et d'arrivée avec l'heure de départ
        for(String miniRoute : miniRoutes){
            stationToStationDuration[j] = miniRoute.split("\\s+");
            j++;
        }
        for (String route : routes) {
            stationToStationScheduled[i] = route.split("\\s+");
            i++;
        }
        duration = stationToStationDuration;
        scheduled = stationToStationScheduled;
    }

    public Station getTerminus(Station currentStation, String startTime){
        for (String[] station: scheduled) {
            List<String> stationArray = new ArrayList<>(List.of(station));
            int place = stationArray.indexOf(currentStation.getNom());
        }

        return null;
    }
}
