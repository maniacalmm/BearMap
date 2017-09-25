import java.util.*;

/**
 * Tries for the autoCompeletion part
 * Tang Dexian
 */
public class MapTries {

    private class Node {
        Map<Character, Node> links;
        boolean exists;

        public Node() {
            exists = false;
            links = new HashMap<Character, Node>(30);
        }
    }

    private Node root = new Node();

    public void put(String key) {
        put(root, key, 0);
    }

    private Node put(Node x, String key, int d) {
        if (x == null)
            x = new Node();

        if (d == key.length()) {
            x.exists = true;
            return x;
        }

        char c = key.charAt(d);
        x.links.put(c, put(x.links.get(c), key, d + 1));
        return x;
    }

    public boolean get(String key) {
        Node x = get(root, key, 0);
        if (x == null) return false;
        return x.exists;

    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);

        return get(x.links.get(c), key, d + 1);
    }

    public List<String> keyWithPrefix(String prefix) {
        List<String> Q = new LinkedList<>();
        collect(get(root, prefix, 0), prefix, Q); // get() --> first get the node to start
        return Q;
    }

    private void collect(Node x, String prefix, List<String> Q) {
        if (x == null) return;
        if (x.exists) Q.add(prefix);

        for (Character key : x.links.keySet()) {
            collect(x.links.get(key), prefix + key, Q);
        }
    }

    public static void main(String[] args) {
        List<String> s = Arrays.asList("she", "sells", "sea", "shells", "by", "the", "sea", "shore");
        MapTries MT = new MapTries();
        for (String ss : s)
            MT.put(ss);

        System.out.println(MT.keyWithPrefix("se"));


    }

}
