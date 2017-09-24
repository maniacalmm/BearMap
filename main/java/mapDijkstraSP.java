import java.io.IOException;
import java.util.*;

/*
 * Lazy version of shortest path algo, without indexed priority queue
 */
public class MapDijkstraSP {
    private Map<Long, Double> distTo = new HashMap<>();
    private Map<Long, Long> wayTo = new HashMap<>(); // this is to construct shortest path tre，sort of
    private PriorityQueue<VerticePair> pq;
    private MapNode destination;

    private class VerticePair implements Comparable<VerticePair> {
        Double weight; // include both current length and the heuristic distance
        long verticeID;

        public VerticePair(long vertice, Double weight) {
            this.weight = weight;
            this.verticeID = vertice;
        }

        public int compareTo(VerticePair other) {
            return this.weight.compareTo(other.weight);
        }
    }

    public MapDijkstraSP (GraphDB G, long start, long end) {
        destination = G.getNode(end);

        distTo.put(start, 0.0);
        pq = new PriorityQueue<VerticePair>();
        pq.add(new VerticePair(start, distTo.get(start)));

        while (!pq.isEmpty()) {
            VerticePair VP = pq.remove();
            for (long id : G.adjacent(VP.verticeID)) {
                // we have reached our destination, break out!!
                if (wayTo.containsKey(destination))
                    break;

                double edgeLen = G.getNode(id).distanceTo(G.getNode(VP.verticeID));
                double heuristicLen = G.getNode(id).distanceTo(G.getNode(destination.id));
                relax(id, VP.verticeID, edgeLen, heuristicLen);
            }
        }

    }

    private void relax(long relaxTo, long relaxFrom, double edgeLen, double heuristicLen) {
        if (!distTo.containsKey(relaxTo))
            distTo.put(relaxTo, Double.POSITIVE_INFINITY);
        /*
        System.out.println("relaxing: " + relaxTo + " from " + relaxFrom
                            + " edgeLen: " + edgeLen + " heur " + heuristicLen
                            + " number of nodes: " + pq.size());
                            */
        if (distTo.get(relaxTo) > distTo.get(relaxFrom) + edgeLen) {
            distTo.put(relaxTo, distTo.get(relaxFrom) + edgeLen);
            wayTo.put(relaxTo, relaxFrom);
            // this is the A* step
            pq.add(new VerticePair(relaxTo, distTo.get(relaxTo) + heuristicLen));
            //TS.add(new VerticePair(w, distTo[w]));
        }
    }

    public double distTo(long v) {
        return distTo.get(v);
    }

    public boolean hasPathTo(long v) {
        // if the traversal reached this point, distance weight will definitely be changed
        return distTo.containsKey(v);
    }

    public LinkedList<Long> pathTo(long v) {
        if (!hasPathTo(v)) return null;
        LinkedList<Long> path = new LinkedList<>();
        path.addFirst(v);
        for (Long from = wayTo.get(v); from != null; from = wayTo.get(from))
            path.addFirst(from);
        return path;
    }


}
