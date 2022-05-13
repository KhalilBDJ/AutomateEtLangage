package parser;

import com.fasterxml.jackson.databind.JsonNode;
import model.*;

import java.io.InputStream;
import java.time.LocalTime;
import java.util.List;

public abstract class Automaton<T> {

    protected InputStream file;
    protected Line line;

    public abstract String getLineName(T reader);
    public abstract List<Station> getLineStations(T reader);
    public abstract List<Direction> getDirections(T reader);
    public abstract List<Route> createRoute();
    public abstract void addToNetwork(Network network);

    protected LocalTime parseDuration(String duration){
        return LocalTime.of(Integer.parseInt(duration.substring(0,2)), Integer.parseInt(duration.substring(2)));
    }

}
