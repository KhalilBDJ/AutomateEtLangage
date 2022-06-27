package parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import exception.BadValueException;
import model.*;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AutomatonTest {

    @Test
    public void test_pass_with_json_good_file() throws IOException, BadValueException {
        Network network = new Network();
        JsonAutomaton jsonAutomaton = new JsonAutomaton(network, "/json_good_file.json");
        List<Route> route = jsonAutomaton.createRoute();
        assertEquals(route, good_route(Transport.BUS));
    }

    @Test(expected = JsonParseException.class)
    public void test_pass_with_json_bad_file() throws IOException, BadValueException {
        Network network = new Network();
        JsonAutomaton jsonAutomaton = new JsonAutomaton(network, "/json_bad_file.json");
        jsonAutomaton.createRoute();
    }

    @Test(expected = BadValueException.class)
    public void test_pass_with_json_bad_value() throws IOException, BadValueException {
        Network network = new Network();
        JsonAutomaton jsonAutomaton = new JsonAutomaton(network,"/json_bad_value.json");
        jsonAutomaton.createRoute();
    }

    @Test
    public void test_pass_train_xml_good_file() throws IOException, BadValueException {
        Network network = new Network();
        TrainXMLAutomaton trainXMLAutomaton = new TrainXMLAutomaton(network, "/train_xml_good_file.xml");
        List<Route> route = trainXMLAutomaton.createRoute();
        assertEquals(route, good_route(Transport.TRAIN));
    }

    @Test(expected = JsonMappingException.class)
    public void test_pass_train_xml_bad_file() throws IOException, BadValueException {
        Network network = new Network();
        TrainXMLAutomaton trainXMLAutomaton = new TrainXMLAutomaton(network,"/train_xml_bad_file.xml");
        trainXMLAutomaton.defineLines();
    }

    @Test(expected = IOException.class)
    public void test_pass_train_xml_bad_value_file() throws IOException, BadValueException {
        Network network = new Network();
        TrainXMLAutomaton trainXMLAutomaton = new TrainXMLAutomaton(network,"/train_xml_bad_value_file.xml");
        trainXMLAutomaton.defineLines();
    }

    @Test
    public void test_pass_tram_xml_good_file() throws IOException, BadValueException {
        Network network = new Network();
        TramXMLAutomaton tramXMLAutomaton = new TramXMLAutomaton(network, "/tram_xml_good_file.xml");
        List<Route> route = tramXMLAutomaton.createRoute();
        List<Route> routes = good_route(Transport.TRAM);
        for (Route route1 : routes){
            route1.getDirection().getTerminus().setOrder(setGoodOrder(route1.getDirection().getTerminus()));
            route1.getDirection().getCurrentStation().setOrder(setGoodOrder(route1.getDirection().getCurrentStation()));
        }
        assertEquals(route, routes);
    }

    @Test(expected = JsonParseException.class)
    public void test_pass_tram_xml_bad_file() throws IOException, BadValueException {
        Network network = new Network();
        TramXMLAutomaton tramXMLAutomaton = new TramXMLAutomaton(network,"/tram_xml_bad_file.xml");
        tramXMLAutomaton.defineLines();
    }

    @Test(expected = BadValueException.class)
    public void test_pass_tram_xml_bad_value() throws IOException, BadValueException {
        Network network = new Network();
        TramXMLAutomaton tramXMLAutomaton = new TramXMLAutomaton(network,"/tram_xml_bad_value.xml");
        tramXMLAutomaton.defineLines();
    }

    List<Route> good_route(Transport transport){
       List<Route> routes = new ArrayList<>();
       Route route = new Route(9, good_direction().get(0), transport);
       routes.add(route);
       return routes;
    }

    List<Direction> good_direction(){
        Direction direction1 = new Direction();
        direction1.setTerminus(good_stations().get(1));
        direction1.setCurrentStation(good_stations().get(0));
        List<Passage> passages1 = new ArrayList<>();
        passages1.add(new Passage(parseDuration("0720")));
        passages1.add(new Passage(parseDuration("0740")));
        direction1.setPassages(passages1);

        Direction direction2 = new Direction();
        direction2.setTerminus(good_stations().get(1));
        direction2.setCurrentStation(good_stations().get(1));
        List<Passage> passages2 = new ArrayList<>();
        passages2.add(new Passage(parseDuration("0729")));
        passages2.add(new Passage(parseDuration("0749")));
        direction2.setPassages(passages2);

        List<Direction> directions = new ArrayList<>();
        directions.add(direction1);
        directions.add(direction2);
        System.out.println(directions);
        return directions;
    }

    List<Station> good_stations(){
        Station station1 = new Station("Gare", true);
        Station station2 = new Station("Piscine", true);
        List<Station> stations = new ArrayList<>();
        stations.add(station1);
        stations.add(station2);
        return stations;
    }

    LocalTime parseDuration(String duration){
        return LocalTime.of(Integer.parseInt(duration.substring(0,2)), Integer.parseInt(duration.substring(2)));
    }

    int setGoodOrder(Station station){
        if (station.getNom().equals("Gare")){
            return 0;
        }else {
            return 1;
        }
    }
}