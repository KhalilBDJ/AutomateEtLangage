package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class JsonAutomaton extends Automaton {
    private final InputStream file;

    public JsonAutomaton(){
        file = Objects.requireNonNull(getClass().getResourceAsStream("/bus.json"));
    }

    public void decodeJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode bus = mapper.readTree(file);

        System.out.println(line(bus));

    }

    public String line(JsonNode jsonNode){
        return jsonNode.get("ligne").asText();
    }
}
