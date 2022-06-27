package Metro;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import model.Station;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Metro {



    @ToString
    public static class Liaison{
        Station start;
        Station finish;
        int duree;

        public Liaison(Station start, Station finish, int duree) {
            this.start = start;
            this.finish = finish;
            this.duree = duree;
        }

        public Station getStart() {
            return start;
        }

        public void setStart(Station start) {
            this.start = start;
        }

        public Station getFinish() {
            return finish;
        }

        public void setFinish(Station finish) {
            this.finish = finish;
        }

        public int getDuree() {
            return duree;
        }

        public void setDuree(int duree) {
            this.duree = duree;
        }
    }

    public static class Rule{
        LocalTime debut;
        LocalTime fin;

        int intervalle;

        public Rule(LocalTime debut, LocalTime fin, int intervalle) {
            this.debut = debut;
            this.fin = fin;
            this.intervalle = intervalle;
        }

        public LocalTime getDebut() {
            return debut;
        }

        public void setDebut(LocalTime debut) {
            this.debut = debut;
        }

        public LocalTime getFin() {
            return fin;
        }

        public void setFin(LocalTime fin) {
            this.fin = fin;
        }

        public int getIntervalle() {
            return intervalle;
        }

        public void setIntervalle(int intervalle) {
            this.intervalle = intervalle;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Rule)) return false;
            Rule rule = (Rule) o;
            return intervalle == rule.intervalle && Objects.equals(debut, rule.debut) && Objects.equals(fin, rule.fin);
        }

        @Override
        public int hashCode() {
            return Objects.hash(debut, fin, intervalle);
        }
    }

    @Getter
    public static class AR {
        List<Lnode> lnodes;
        public AR(){
            lnodes = new ArrayList<>();
        }
    }

    @Getter
    public static class Circuit{
        List<Lnode> lnodes;
        public Circuit(){
            lnodes = new ArrayList<>();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Lnode{
        private String start;
        private String end;
        private long duration;
    }
}
