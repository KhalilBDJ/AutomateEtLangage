import model.Network;
import org.xml.sax.SAXException;
import parser.TrainXMLAutomaton;
import parser.TramXMLAutomaton;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Network network = new Network();
/*        JsonAutomaton jsonAutomaton = new JsonAutomaton();
        jsonAutomaton.decodeJson();*/

/*        TramXMLAutomaton tramXMLAutomaton = new TramXMLAutomaton(network);
        System.out.println(tramXMLAutomaton.createRoute())*/;

        TrainXMLAutomaton trainXMLAutomaton = new TrainXMLAutomaton(network);
        System.out.println(trainXMLAutomaton.createRoute());
    }
}
