import model.Station;
import org.xml.sax.SAXException;
import parser.TextAutomatonBis;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        //JsonAutomaton jsonAutomaton = new JsonAutomaton();
        //jsonAutomaton.decodeJson();
        Station station = new Station("Syen", false);

        //XMLAutomaton xmlAutomaton = new XMLAutomaton();
        //xmlAutomaton.defineLines();

        //TextAutomaton textAutomaton = new TextAutomaton();
        //textAutomaton.getTerminus(station, "0850");
        //textAutomaton.setStations();
        //textAutomaton.setDirections();
        //textAutomaton.createRoute();

        TextAutomatonBis automatonBis = new TextAutomatonBis();
        //System.out.println(LocalTime.of(Integer.parseInt("0020".substring(0,2)), Integer.parseInt("0020".substring(2))));
        //automatonBis.setDiretions();
        automatonBis.decode();
        //automatonBis.setStation();
        //automatonBis.setRules();

        /*String test = "% départ de Gare toutes les 10 minutes de 07:00 à 09:00 et de 16:30à 18:00";
        String test2 = test.replaceAll("[A-Za-zùàé%]", "").replaceAll(":", "");
        System.out.println(test2);
        List<String> list= new ArrayList<>(List.of(test2.split("\\s+")));
        list.remove(0);
        System.out.println(list);*/
    }
}
