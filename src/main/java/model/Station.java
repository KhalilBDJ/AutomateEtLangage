package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Station {

    private String nom;
    private boolean isTerminus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return isTerminus == station.isTerminus && Objects.equals(nom, station.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, isTerminus);
    }

    public boolean isTerminus() {
        return isTerminus;
    }
}
