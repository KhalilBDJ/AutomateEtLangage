package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Route {

    private long duration;
    private Direction direction;
    private Transport transport;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return duration == route.duration && Objects.equals(direction, route.direction) && transport == route.transport;
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, direction, transport);
    }
}
