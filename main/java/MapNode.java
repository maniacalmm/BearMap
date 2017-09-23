/**
 * MapNode is the Node we use to identify and spot on the map
 *
 * it has one longitude, one latitude, and probably a name
 *
 */
public class MapNode {
    Long id;
    double lon;
    double lat;
    String name = "no where";

    public MapNode(Long id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    // this is actually kinda useless
    public MapNode(Long id, double lon, double lat, String name) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.name = name;
    }

    void addName(String name) {
        this.name = name;
    }

    public double distanceTo(MapNode o) {
        double lonErr = o.lon - this.lon;
        double latErr = o.lat - this.lat;
        return Math.sqrt(lonErr * lonErr + latErr * latErr);
    }

    double distanceToSquare(MapNode o) {
        double lonErr = o.lon - this.lon;
        double latErr = o.lat - this.lat;
        return lonErr * lonErr + latErr * latErr;
    }

    public String toString() {
        return "Node: " + id + " lon: " + lon + " lat: " + lat + " name: " + name;
    }
}
