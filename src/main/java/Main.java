import model.Station;
import org.xml.sax.SAXException;
import parser.JsonAutomaton;
import parser.TextAutomatonBis;
import parser.XMLAutomaton;
import parser.TextAutomaton;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

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
        automatonBis.setStation();
    }
}
