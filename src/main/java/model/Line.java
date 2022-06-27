package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
public class Line {

    private List<Station> stations;
    private List<Direction> directions;
    private String name;
    private Transport transportType;

    public Line(Transport transportType) {
        this.stations = new ArrayList<>();
        this.directions = new ArrayList<>();
        this.transportType = transportType;
    }

    public Station getStationWithName(String name){
        return stations.stream().filter(station -> station.getNom().equals(name)).findFirst().orElse(null);
    }

    private List<Station> sortListWithDirection(Direction direction){
        return direction.getTerminus().getNom().equals(stations.get(stations.size()-1).getNom()) ? stations : getReverseList();
    }
    public Station nextStation(Direction direction){
        List<Station> sortList = sortListWithDirection(direction);
        return nextStation(direction.getCurrentStation(), sortList);
    }

    public Station nextStation(Station station, List<Station> stations){
        int currentIndex = stations.indexOf(station);
        if (currentIndex == stations.size()-1){
            return null;
        }
        return stations.get(currentIndex+1);
    }

    public Station nextStation(Station station){
        return nextStation(station, this.stations);
    }

    //TODO : rework name
    public Direction findDirectionWithStationAndDir(Direction direction){
        Station next = nextStation(direction);
        if (next == null){
            return null;
        }
        return directions.stream().filter(tempDirection -> tempDirection.getCurrentStation().equals(next) && tempDirection.getTerminus().equals(direction.getTerminus()) && tempDirection.getPassages().get(0).getSchedule().isAfter(direction.getPassages().get(0).getSchedule())).findFirst().orElse(null);
    }

    private List<Station> getReverseList(){
        List<Station> reverseList = new ArrayList<>(stations);
        Collections.reverse(reverseList);
        return reverseList;
    }

    public long durationBetweenNextStation(Direction direction){
        Direction nextDirection = findDirectionWithStationAndDir(direction);
        if (nextDirection == null){
            return 0;
        }
        return direction.getPassages().get(0).getArrival() != null ? ChronoUnit.MINUTES.between(direction.getPassages().get(0).getSchedule(), direction.getPassages().get(0).getArrival()) : ChronoUnit.MINUTES.between(direction.getPassages().get(0).getSchedule(), nextDirection.getPassages().get(0).getSchedule());
    }

    public boolean isNormalSens(Station terminus){
        return !stations.get(0).equals(terminus);
    }

    public Station determineTerminus(Station current, Station next){
        Station confirmNext = nextStation(current);
        if (confirmNext == null){
            confirmNext = nextStation(current, getReverseList());
            if (confirmNext == null){
                System.err.println("ERREUR !");
                return null;
            }
            return confirmNext.equals(next) ? stations.get(0) : stations.get(stations.size()-1);
        }
        return confirmNext.equals(next) ? stations.get(stations.size()-1) : stations.get(0);
    }
}
