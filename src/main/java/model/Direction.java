package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Direction {

    private Set<Passage> passages;
    private Station terminus;
    private Station currentStation;

    public Direction() {
        this.passages = new HashSet<>();
    }
}
