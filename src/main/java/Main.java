import org.xml.sax.SAXException;
import parser.JsonAutomaton;
import parser.XMLAutomaton;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        JsonAutomaton jsonAutomaton = new JsonAutomaton();
        jsonAutomaton.decodeJson();

        XMLAutomaton xmlAutomaton = new XMLAutomaton();
        xmlAutomaton.defineLines();
    }
}
