public class MapNode {
    String id;
    double lon;
    double lat;
    String name = "no where";

    public MapNode(String id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    public MapNode(String id, double lon, double lat, String name) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.name = name;
    }

    public double distanceTo(MapNode o) {
        double lonErr = o.lon - this.lon;
        double latErr = o.lat - this.lat;
        return Math.sqrt(lonErr * lonErr + latErr * latErr);
    }

    public String toString() {
        return "Node: " + id + " lon: " + lon + " lat: " + lat + " name: " + name;
    }
}
