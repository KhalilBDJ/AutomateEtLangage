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

    public Direction() {
        this.passages = new ArrayList<>();
    }
}
