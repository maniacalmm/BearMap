import java.util.ArrayList;
import java.util.List;

public class MapNodePack {
    MapNode mapNode;
    List<MapEdge> edges;

    public MapNodePack(MapNode mapNode) {
        this.mapNode = mapNode;
        edges = new ArrayList<>();
    }

    boolean emptyEdge() {
        return edges.isEmpty();
    }

    void appendName(String name) {
        mapNode.addName(name);
    }

    double nodeLon() {
        return mapNode.lon;
    }

    double nodeLat() {
        return mapNode.lat;
    }

    void putEdge (MapEdge me) {
        edges.add(me);
    }

    Iterable<MapEdge> getEdges() {
        return edges;
    }


}
