package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Direction;
import model.Line;
import model.Passage;
import model.Station;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class JsonAutomaton extends Automaton {
    private final InputStream file;
    private Line line;

    public JsonAutomaton(){
        file = Objects.requireNonNull(getClass().getResourceAsStream("/bus.json"));
        line = new Line();
    }

    public void decodeJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode bus = mapper.readTree(file);

        System.out.println(getLineName(bus));
        System.out.println(getLineStations(bus));
        line.setStations(getLineStations(bus));

    }

    public String getLineName(JsonNode jsonNode){
        return jsonNode.get("ligne").asText();
    }

    public Set<Station> getLineStations(JsonNode jsonNode){
        Set<Station> stations = new HashSet<Station>();
        JsonNode jsonStations = jsonNode.get("horaires").get(0).get("stations");
        for (JsonNode station : jsonStations){
            stations.add(new Station(station.get("station").asText()));
        }
        return stations;
    }

    public Set<Direction> getDirections(JsonNode jsonNode){
        Set<Direction> directions = new HashSet<>();
        JsonNode jsonDirections = jsonNode.get("horaires");
        for (JsonNode direction : jsonDirections){
            Station terminusStation = line.getStationWithName(direction.get("direction").asText());
            JsonNode passages = direction.get("passages");
            int stationNbr = 0;
            for (Station station : line.getStations()){
                Direction dir = new Direction();
                Set<Passage> passages1 = new HashSet<>();
                for (int i = 0; i < line.getStations().size(); i++) {
                    Passage passage = new Passage(passages.get(i).get(stationNbr).asText());
                    passages1.add(passage);
                }
                dir.setCurrentStation(station);
                dir.setTerminus(terminusStation);
                dir.setPassages(passages1);
                stationNbr++;
            }
        }
        return directions;
    }


}
