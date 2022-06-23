package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
public class Direction {

    private List<Passage> passages;
    private Station terminus;
    private Station currentStation;

    public Direction(List<Passage> passages, Station terminus, Station currentStation) {
        this.passages = passages;
        this.terminus = terminus;
        this.currentStation = currentStation;
    }

    public Direction() {
        this.passages = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direction direction = (Direction) o;
        return Objects.equals(passages, direction.passages) && Objects.equals(terminus, direction.terminus) && Objects.equals(currentStation, direction.currentStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passages, terminus, currentStation);
    }
}
