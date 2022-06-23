package model.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class TramPojo {
    @JacksonXmlRootElement(localName = "reseau")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reseau{
        Stations stations;
        Lignes lignes;
    }

    @JacksonXmlRootElement(localName = "lignes")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Lignes{
        List<Ligne> ligne;
    }

    @JacksonXmlRootElement(localName = "ligne")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ligne{
        @JacksonXmlText(value = true)
        String nom;
        Stations stations;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "heures-passage")
        List<HorairePassage> horairePassages = new ArrayList<>();
    }

    @JacksonXmlRootElement(localName = "stations")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Stations{
        @JacksonXmlText(value = true)
        String value;
    }

    @JacksonXmlRootElement(localName = "heures-passage")
    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HorairePassage{
        @JacksonXmlText(value = true)
        String value;
    }
}
