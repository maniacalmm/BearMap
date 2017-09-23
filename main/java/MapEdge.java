public class MapEdge {
    // since we assume double direction, then no point in distinguish between directions
    private MapNode x;
    private MapNode y;
    String name = "no where";
    double length;

    public MapEdge(MapNode x, MapNode y) {
       this.x = x;
       this.y = y;
       length = x.distanceTo(y);
    }

    // compare to the node constructor, this one is more useful
    public MapEdge(MapNode x, MapNode y, String name) {
       this.x = x;
       this.y = y;
       this.name = name;
       length = x.distanceTo(y);
    }

    long getXid() {
        return x.id;
    }

    long getYid() {
        return y.id;
    }

    public double length() {
        return length;
    }

    public String toString() {
        return x.id + " to " + y.id + " " + " len: " + length;
    }


}
