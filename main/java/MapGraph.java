import jdk.nashorn.api.scripting.AbstractJSObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapGraph {

    Map<Long, MapNodePack> AdjacentList;
    //long vertices, edges;

    public MapGraph() {
        this.AdjacentList = new HashMap<Long, MapNodePack>();
    }

    public void addNode(MapNode mn) {
       if (!AdjacentList.containsKey(mn.id)) {
           AdjacentList.put(mn.id, new MapNodePack(mn));
       } else {
           throw new IllegalArgumentException("node with this ID already exists");
       }
    }

    //
    void removeNode(Long id) {
        if(AdjacentList.containsKey(id)) {
            AdjacentList.remove(id);
        }
    }

    // this node has no connection
    boolean noEdges(Long id) {
        return AdjacentList.get(id).emptyEdge();
    }

    // check if this node exist
    public boolean nodeExists(Long Nodeid) {
        return AdjacentList.containsKey(Nodeid);
    }

    // adding edge
    public void addEdge (Long id, MapEdge me) {
        if (nodeExists(id)) {
            AdjacentList.get(id).putEdge(me);
        }
    }

    MapNode getNode(long id) {
        return AdjacentList.get(id).mapNode;
    }

    MapNode closestNode(MapNode o) {
        double distance = Double.POSITIVE_INFINITY;
        MapNode target = null;
        for (MapNodePack mnp : AdjacentList.values()) {
            double distmp = mnp.mapNode.distanceTo(o);
            if (distmp < distance && !mnp.emptyEdge()) {
                distance = distmp;
                target = mnp.mapNode;
            }
        }
        return target;
    }

    // add name to the node latter
    void addingName(long id, String name) {
        if (nodeExists(id)) {
            getNode(id).addName(name);
        }
    }

    // get all the edges from and node with id
    public Iterable<MapEdge> adj(Long id) {
        return AdjacentList.get(id).getEdges();
    }

    // return the longitude of a given node
    public double getLon(long id) {
        return AdjacentList.get(id).nodeLon();
    }

    // return the latitude of a given node
    public double getLat(long id) {
        return AdjacentList.get(id).nodeLat();
    }


}
