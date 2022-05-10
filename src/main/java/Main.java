import parser.JsonAutomaton;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JsonAutomaton jsonAutomaton = new JsonAutomaton();
        jsonAutomaton.decodeJson();
    }
}
