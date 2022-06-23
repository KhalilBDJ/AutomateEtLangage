package model.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @Valid
    public static class Horaires{
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "line")
        @Valid
        @NotEmpty
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
        @NotNull
        @NotEmpty
        String nom;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "junction")
        @NotEmpty
        @Valid
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
        @NotNull
        @Valid
        StartStation startStation;
        @JacksonXmlProperty(localName = "arrival-station")
        @NotNull
        @Valid
        ArrivalStation arrivalStation;
        @JacksonXmlProperty(localName = "start-hour")
        @NotNull
        @Valid
        StartHour startHour;
        @JacksonXmlProperty(localName = "arrival-hour")
        @NotNull
        @Valid
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
        @NotNull
        @NotEmpty
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
        @NotNull
        @NotEmpty
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
        @NotNull
        @NotEmpty
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
        @NotNull
        @NotEmpty
        String value;
    }
}
