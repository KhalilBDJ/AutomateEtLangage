package model.text;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

public class InterCite {
    @Getter
    @AllArgsConstructor
    public static class Duration{
        String stationDepart;
        String stationArrivee;
        long duree;
    }

    @Getter
    @AllArgsConstructor
    public static class Scheduled{
        String stationDepart;
        String stationArrivee;
        LocalTime scheduled;
    }
}
