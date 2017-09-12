import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class QuadTree {
    Node root;

    private class Node {
        String fileName;
        double centerLon, centerLati;
        double ULLAT, ULLON, LRLAT, LRLON;
        Node[] Quad = new Node[4];

        public Node(String fileName, double ullat, double ullon, double lrlat, double lrlon) {
            this.fileName = fileName;
            ULLAT = ullat;
            ULLON = ullon;
            LRLAT = lrlat;
            LRLON = lrlon;
            centerLon = (ULLON + LRLON) / 2;
            centerLati = (ULLAT + LRLAT) / 2;
        }

        public String toString() {
            return fileName;
        }
    }

    public QuadTree(int quantity) {
        root = new Node("root", MapServer.ROOT_ULLAT, MapServer.ROOT_ULLON,
                MapServer.ROOT_LRLAT, MapServer.ROOT_LRLON);
        initialization(quantity);
    }

    private void initialization(int num) {
        for (int i = 1; i <= num; i++) {
            String file = baseFourConvert(i);
            System.out.println(file);
            add(root, file, 0);
        }

    }
/*
    private void BFSinital(int num) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while(!queue.isEmpty() && num > 0) {
            Node tmp = queue.remove();
            for (int i = 0; i < tmp.Quad.length; i++) {
                //tmp.Quad[i] = new Node()
            }
        }
    }
*/
    private void add(Node toAdd, String fileName, int count) {
        //System.out.println("filename: " + fileName + " count: " + count);
        if (fileName.length()-1 == count) {
            int section = fileName.charAt(fileName.length() - 1) - '1';
            double[] posInfo = posCal(toAdd, section);
            toAdd.Quad[section] =
                    new Node(fileName, posInfo[0], posInfo[1], posInfo[2], posInfo[3]);
            //System.out.println("fileName: " + fileName + " added into " + toAdd.toString() + " section: " + section);
        } else {
            int s = fileName.charAt(count) - '1';
            //System.out.println("s: " + s + " --> " + toAdd.Quad[s].toString());
            add(toAdd.Quad[s], fileName, count + 1);
            //add(toAdd.Quad[s], fileName, count++);

        }

    }

    private double[] posCal(Node toAdd, int section) {
        double ullat, ullon, lrlat, lrlon;
        if (section == 0) {
            ullat = toAdd.ULLAT;                     // upper y
            ullon = toAdd.ULLON;                     // upper x
            lrlat = (toAdd.LRLAT + toAdd.ULLAT) / 2; // lower y
            lrlon = (toAdd.LRLON + toAdd.ULLON) / 2; // lower x
        } else if (section == 1) {
            ullat = toAdd.ULLAT;
            ullon = (toAdd.ULLON + toAdd.LRLON) / 2;
            lrlat = (toAdd.LRLAT + toAdd.ULLAT) / 2;
            lrlon = toAdd.LRLON;
        } else if (section == 2) {
            ullat = (toAdd.ULLAT + toAdd.LRLAT) / 2;
            ullon = toAdd.ULLON;
            lrlat = toAdd.LRLAT;
            lrlon = (toAdd.LRLON + toAdd.ULLON) / 2;
        } else {
            ullat = (toAdd.ULLAT + toAdd.LRLAT) / 2;
            ullon = (toAdd.ULLON + toAdd.LRLON) / 2;
            lrlat = toAdd.LRLAT;
            lrlon = toAdd.LRLON;
        }

        double[] result = new double[] {ullat, ullon, lrlat, lrlon};

        return result;
    }

    private static String baseFourConvert(int num) {
        StringBuilder sb = new StringBuilder();
        while(num > 0) {
            int digit = num % 4;
            if (digit == 0) {
                sb.append(4);
                num-=4;
            } else {
                sb.append(digit);
            }
            num /= 4;
        }
        sb.reverse();
        return sb.toString();
    }

    private void BFS() {
        Queue<Node> Q = new LinkedList<>();
        Q.add(root);
        while(!Q.isEmpty()) {
            //System.out.println("Q size: " + Q.size());
            Node n = Q.remove();
            for (Node node : n.Quad) {
                if (node != null) {
                    System.out.print(node.toString() + " ");
                    Q.add(node);
                }
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {

        int quantity = 4;
        QuadTree QT = new QuadTree(20);
        System.out.println("BFS starting");
        QT.BFS();



    }



}
