package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Network {
    private List<Route> routes;
    private List<Line> lines;

    public Network() {
        this.routes = new ArrayList<>();
        this.lines = new ArrayList<>();
    }

    public boolean addRoutes(List<Route> routesList){
        return this.routes.addAll(routesList);
    }

    public boolean addLine(Line line){
        return this.lines.add(line);
    }
}
