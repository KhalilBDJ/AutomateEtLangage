package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Network {
    private List<Route> routes;
    private List<Line> lines;
    private List<Station> stations;

    public Network() {
        this.routes = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.stations = new ArrayList<>();
    }

    public boolean addRoutes(List<Route> routesList){
        return this.routes.addAll(routesList);
    }

    public boolean addLine(Line line){
        return this.lines.add(line);
    }

    public boolean addLines(List<Line> lines){
        return this.lines.addAll(lines);
    }

    public boolean addStations(List<Station> stations){
        return this.stations.addAll(stations);
    }

    public boolean addStation(Station station){
        return this.stations.add(station);
    }

    public Station getStationWithName(String name){
        Station station1 = stations.stream().filter(station -> station.getNom().equals(name)).findFirst().orElse(null);
        if (station1 == null){
            return null;
        }
        return new Station(station1.getNom(), station1.isTerminus());
    }
}
