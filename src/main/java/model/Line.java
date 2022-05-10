package model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Line {

    private Set<Station> stations;
    private String name;

    public Station getStationWithName(String name){
        return stations.stream().filter(station -> station.getNom().equals(name)).findFirst().get();
    }

    public Line() {
        this.stations = new HashSet<>();
    }
}
