import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {

    MapGraph mapGraph;

    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        // graph intialization
        mapGraph = new MapGraph();
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        // TODO: Your code here.
        // iterating while changing, this is the where iterator is really useful;
        Iterator<Long> iterator = mapGraph.AdjacentList.keySet().iterator();
        while(iterator.hasNext()) {
            long id = iterator.next();
            if (mapGraph.noEdges(id))
                iterator.remove();
        }
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        return mapGraph.AdjacentList.keySet();
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        List<Long> adjtmp = new ArrayList<>();
        for (MapEdge n : mapGraph.adj(v)) {
            if (n.getXid() != v) adjtmp.add(n.getXid());
            else adjtmp.add(n.getYid());
        }

        return adjtmp;
    }

    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
    double distance(long v, long w) {
        return mapGraph.getNode(v).distanceTo(mapGraph.getNode(w));
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        // linear search version, quadTree later, probably
        // creating a dummy node just for comparison
        return mapGraph.closestNode(new MapNode((long)-1, lon, lat)).id;
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        return mapGraph.getLon(v);
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        return mapGraph.getLat(v);
    }

    // adding name to a node
    void addingNodeName(long id, String name) {
        mapGraph.addingName(id, name);
    }

    // return a node
    MapNode getNode(long id) {
        return mapGraph.getNode(id);
    }

    // adding a node
    void addNode(long id, double lon, double lat){
        mapGraph.addNode(new MapNode(id, lon, lat));
    }

    // adding an edge
    void addEdge(long id, MapEdge edge) {
        mapGraph.addEdge(id, edge);
    }

}
