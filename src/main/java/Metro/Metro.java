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

        public List<Station> getAllStations(){
            List<Station> stations = new ArrayList<>();
            for (Lnode node : lnodes){
                stations.add(new Station(node.start, false));
            }
            stations.get(0).setTerminus(true);
            stations.add(new Station(lnodes.get(lnodes.size()-1).end, true));
            return stations;
        }
    }

    @Getter
    public static class Circuit{
        List<Lnode> lnodes;
        public Circuit(){
            lnodes = new ArrayList<>();
        }

        public List<Station> getAllStations(){
            List<Station> stations = new ArrayList<>();
            int order = 0;
            for (Lnode node : lnodes){
                Station station = new Station(node.start, false);
                station.setOrder(++order);
                stations.add(station);
            }
            stations.get(0).setTerminus(true);
            Station last = new Station(lnodes.get(lnodes.size() - 1).end, true);
            last.setOrder(++order);
            stations.add(last);
            return stations;
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
