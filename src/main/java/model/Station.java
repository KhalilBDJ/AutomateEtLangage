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

    public Station(String nom, boolean isTerminus) {
        this.nom = nom;
        this.isTerminus = isTerminus;
        this.order = 0;
    }

    private String nom;
    private int order;
    private boolean isTerminus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return order == station.order && isTerminus == station.isTerminus && Objects.equals(nom, station.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, order, isTerminus);
    }

    public boolean isTerminus() {
        return isTerminus;
    }
}
