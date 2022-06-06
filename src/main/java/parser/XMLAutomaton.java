package parser;

import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XMLAutomaton extends Automaton<NodeList>{
    Document xmlDocument;
    List<Line> lines;

    public XMLAutomaton() throws ParserConfigurationException, IOException, SAXException {
        file = Objects.requireNonNull(getClass().getResourceAsStream("/tram.xml"));
        line = new Line(Transport.CITY_BUS);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        xmlDocument = documentBuilder.parse(file);
        lines = new ArrayList<>();

    }

    @Override
    public String getLineName(NodeList reader) {
        return null;
    }

    @Override
    public List<Station> getLineStations(NodeList reader) {
        return null;
    }

    public void decodeXML(){
        getLineStations();
    }

    public void defineLines() {
        NodeList xmlLines = xmlDocument.getElementsByTagName("ligne");
        for (int linesIndex = 0; linesIndex < xmlLines.getLength(); linesIndex++) {
            Node xmlLine = xmlLines.item(linesIndex);
            if (xmlLine.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println(xmlLine.getNodeName());
//                System.out.println(xmlLine.getNodeValue());
                NodeList nextChilds = xmlLine.getChildNodes();
                for (int j = 0; j < xmlLine.getChildNodes().getLength(); j++) {
                    Node item = nextChilds.item(j);
                    if (item.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println(item.getNodeName());
                        System.out.println(item.getNodeValue());
                    }
                }
                //TODO : check pk le getValue ne marche pas
            }
        }

        //System.out.println(xmlLines.item(0).getChildNodes().item(1).getNodeName());
    }

    public List<Station> getLineStations() {
        Node stations = xmlDocument.getElementsByTagName("stations").item(0);
        System.out.println(stations.getNodeName());
        System.out.println(stations.getTextContent());
        return null;
    }

    @Override
    public List<Direction> getDirections(NodeList reader) {
        return null;
    }

    @Override
    public List<Route> createRoute() {
        return null;
    }

    @Override
    public void addToNetwork(Network network) {

    }
}
