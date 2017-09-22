import java.util.*;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    private QuadTree QT;
    String imgRoot;
    private double LonDPP;
    private String[][] renderedGrid;
    private double raster_ul_lon;
    private double raster_ul_lat;
    private double raster_lr_lon;
    private double raster_lr_lat;
    private boolean querySuccess;
    private int depth;   // can be determined by the length of fileName in renderedGrid
    Map<String, Object> rasteredResult;


    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        // YOUR CODE HERE
        // for QuadTree, quantity here means the amount of picture we have except for root file
        this.imgRoot = imgRoot;
        QT = new QuadTree(21844);
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     *
     *
     *  private static final String[] REQUIRED_RASTER_RESULT_PARAMS = {"render_grid", "raster_ul_lon",
        "raster_ul_lat", "raster_lr_lon", "raster_lr_lat", "depth", "query_success"};
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {

        System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        double ullon = params.get("ullon");
        double ullat = params.get("ullat");
        double lrlon = params.get("lrlon");
        double lrlat = params.get("lrlat");
        double w = params.get("w");
        double stepLon, stepLat;

        if (ullon > MapServer.ROOT_LRLON
                || ullat < MapServer.ROOT_LRLAT
                || lrlat > MapServer.ROOT_ULLAT
                || lrlon < MapServer.ROOT_ULLON) {
            /*
            if (ullon < MapServer.ROOT_ULLON) System.out.println("error 1");
            if (ullat > MapServer.ROOT_ULLAT) System.out.println("error 2");
            if (lrlat < MapServer.ROOT_LRLAT) System.out.println("error 3");
            if (lrlon > MapServer.ROOT_LRLON) System.out.println("error 4");
            */
                //System.out.println("query Success is false");
                querySuccess = false;
        } else {
                querySuccess = true;
        }

            List<Double> lon = new ArrayList<>();
            List<Double> lat = new ArrayList<>();

            LonDPP = LonDPPCal(ullon, lrlon, w);
            stepLon = QT.searchFileName(ullon, ullat, LonDPP).lonStep(); //determine the scaled LonDPP
            stepLat = QT.searchFileName(ullon, ullat, LonDPP).latiStep(); //determine the scaled LonDPP

            //System.out.println("ullon: " + ullon + " lrlon: " + lrlon + " step: " + stepLon);
            //System.out.println("ullat: " + ullat + " lrlat: " + lrlat + " step: " + stepLat);

            for (double i = ullon; i < lrlon + stepLon; i += stepLon) lon.add(i);
            for (double j = ullat; j > lrlat - stepLat; j = j - stepLat) lat.add(j);
            //lon.add(lrlon); // get the searching grid longitude
            //lat.add(lrlat); // get the searching grid latitude
            // actually not necessary, since the gap might be too small such that it creates replica image

            //System.out.println("lon : " + lon);
            //System.out.println("lat size: " + lat);

            renderedGrid = gridRendering(lon, lat, LonDPP);

            // for raster geo info
            QuadTree.Node ULNode = QT.searchFileName(ullon, ullat, LonDPP);
            QuadTree.Node LRNode = QT.searchFileName(lon.get(lon.size() - 1), lat.get(lat.size() - 1), LonDPP);
            raster_ul_lat = ULNode.ULLAT;
            raster_ul_lon = ULNode.ULLON;
            raster_lr_lat = LRNode.LRLAT;
            raster_lr_lon = LRNode.LRLON;

            //System.out.println("LonDPP needed: " + LonDPP);
            //System.out.println("LonDPP returned: " + ULNode.LonDPPcal());

            // depth calculation
            depth = ULNode.fileName.length();
            //querySuccess = true;
        //}

        results.put("render_grid", renderedGrid);
        results.put("raster_ul_lon", raster_ul_lon);
        results.put("raster_ul_lat", raster_ul_lat);
        results.put("raster_lr_lon", raster_lr_lon);
        results.put("raster_lr_lat", raster_lr_lat);
        results.put("depth", depth);
        results.put("query_success", querySuccess);

        showGrid();
        System.out.println("raster_ul_lon: " + raster_ul_lon
                            + " raster_ul_lat: " + raster_ul_lat
                            + " raster_lr_lon: " + raster_lr_lon
                            + " raster_lr_lat: " + raster_lr_lat
                            + " depth: " + depth
                            + " query_success " + querySuccess);

        return results;
    }

    private void showGrid() {
       for (String[] row : renderedGrid) {
           for (String s : row)
               System.out.print(s + " ");
           System.out.println();
       }
    }

    /**
     *
     * @param x
     * @param y
     * @param LonDPP
     * @return
     *
     * if we construct the raster image with only x, and y, then,
     * it is almost guaranteed that if the query box outside the root a bit, repetition
     * is gonna occur, which is like 11.png 11.png, by introduced a hashset to keep track
     * of all the raster file, to keep the potential replicate out, which is effective for
     * column replicates, and we made a second pass for the potential row replicate
     */
    private String[][] gridRendering(List<Double> x, List<Double> y, double LonDPP) {

        List<List<String>> rasteredNode = new ArrayList<>();
        Set<String> checkingName = new HashSet<>();
        List<List<String>> cleanName = new ArrayList<>();

        // first pass
        for (int i = 0; i < y.size(); i++) {
            rasteredNode.add(new ArrayList<>());
            for (int j = 0; j < x.size(); j++) {
                QuadTree.Node tmp = QT.searchFileName(x.get(j), y.get(i), LonDPP);
                if (!checkingName.contains(tmp.fileName)) {
                        checkingName.add(tmp.fileName);
                        String wholeThing = imgRoot + QT.searchFileName(x.get(j), y.get(i), LonDPP).fileName + ".png";
                        rasteredNode.get(i).add(wholeThing);
                }
            }
        }

        // second pass
        // eliminate row replicate
        int row = 0;
        for (int k = 0; k < rasteredNode.size(); k++) {
            if (rasteredNode.get(k).size() != 0) {
                cleanName.add(new ArrayList<>());

                for (int l = 0; l < rasteredNode.get(0).size(); l++) {
                    cleanName.get(row).add(rasteredNode.get(k).get(l));
                }
                row++;
            }

        }


        /*
        for (List<String> l : cleanName) {
            for (String s : l) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
        System.out.println("--------------");
        */
        //System.out.println("row: " + rasteredNode.size() + " columns: " + rasteredNode.get(0).size());
        String[][] result = new String[cleanName.size()][cleanName.get(0).size()];
        for (int m = 0; m < cleanName.size(); m++) {
            for (int n = 0; n < cleanName.get(0).size(); n++) {
                result[m][n] = cleanName.get(m).get(n);
            }
        }

        return result;
    }

    private double LonDPPCal(double ullon, double lrlon, double w) {
        return Math.abs(ullon - lrlon) / w;
    }


}
