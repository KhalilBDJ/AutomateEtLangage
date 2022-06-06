package model.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class TrainPojo {

    @JacksonXmlRootElement(localName = "horaires")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Horaires{
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "line")
        List<Line> lines = new ArrayList<>();
    }

    @JacksonXmlRootElement(localName = "line")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Line{
        @JacksonXmlText()
        String nom;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "junction")
        List<Junction> junctions = new ArrayList<>();
    }

    @JacksonXmlRootElement(localName = "junction")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Junction{
        @JacksonXmlProperty(localName = "start-station")
        StartStation startStation;
        @JacksonXmlProperty(localName = "arrival-station")
        ArrivalStation arrivalStation;
        @JacksonXmlProperty(localName = "start-hour")
        StartHour startHour;
        @JacksonXmlProperty(localName = "arrival-hour")
        ArrivalHour arrivalHour;
    }

    @JacksonXmlRootElement(localName = "start-station")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StartStation{
        @JacksonXmlText()
        String value;
    }

    @JacksonXmlRootElement(localName = "arrival-station")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArrivalStation{
        @JacksonXmlText()
        String value;
    }

    @JacksonXmlRootElement(localName = "start-hour")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StartHour{
        @JacksonXmlText()
        String value;
    }

    @JacksonXmlRootElement(localName = "arrival-hour")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArrivalHour{
        @JacksonXmlText()
        String value;
    }
}
