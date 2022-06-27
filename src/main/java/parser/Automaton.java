package parser;

import exception.BadValueException;
import model.Network;
import model.Route;

import java.io.InputStream;
import java.time.LocalTime;
import java.util.List;

public abstract class Automaton {

    protected InputStream file;

    public abstract void addNetworkGlobalStations();

    public abstract void defineLines() throws BadValueException;

    public abstract List<Route> createRoute() throws BadValueException;

    public abstract void addToNetwork(Network network) throws BadValueException;

    protected LocalTime parseDuration(String duration) throws BadValueException {
        if(!duration.matches("\\d{4}")){
            throw new BadValueException("wrong time format");
        }
        return LocalTime.of(Integer.parseInt(duration.substring(0,2)), Integer.parseInt(duration.substring(2)));
    }

}
