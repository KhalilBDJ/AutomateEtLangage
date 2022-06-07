package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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
}
