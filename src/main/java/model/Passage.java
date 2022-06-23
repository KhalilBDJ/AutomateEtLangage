package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Passage {

    private LocalTime schedule;
    // Can be null
    private LocalTime arrival;

    public Passage(LocalTime schedule) {
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passage passage = (Passage) o;
        return Objects.equals(schedule, passage.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schedule);
    }
}
