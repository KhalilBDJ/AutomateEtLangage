package Metro;

import lombok.ToString;
import model.Station;

public class Metro {

    public static class TempsArret{
        int a;
        public TempsArret(int a){
            this.a = a;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }

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
}
