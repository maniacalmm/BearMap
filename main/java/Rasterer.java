import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (ullon < MapServer.ROOT_ULLON || ullat > MapServer.ROOT_ULLAT
                || lrlat < MapServer.ROOT_LRLAT || lrlon > MapServer.ROOT_LRLON
                || lrlat >= ullat || lrlon <= ullon) {
            System.out.println("query Success is false");
                querySuccess = false;
        } else {

            List<Double> lon = new ArrayList<>();
            List<Double> lat = new ArrayList<>();

            LonDPP = LonDPPCal(ullon, lrlon, w);
            stepLon = QT.searchFileName(ullon, ullat, LonDPP).lonStep(); //determine the scaled LonDPP
            stepLat = QT.searchFileName(ullon, ullat, LonDPP).latiStep(); //determine the scaled LonDPP

            System.out.println("ullon: " + ullon + " lrlon: " + lrlon + " step: " + stepLon);
            System.out.println("ullat: " + ullat + " lrlat: " + lrlat + " step: " + stepLat);

            for (double i = ullon; i < lrlon; i += stepLon) lon.add(i);
            for (double j = ullat; j > lrlat; j = j - stepLat) lat.add(j);
            //lon.add(lrlon); // get the searching grid longitude
            //lat.add(lrlat); // get the searching grid latitude
            // actually not necessary, since the gap might be too small such that it creates replica image

            System.out.println("lon : " + lon);
            System.out.println("lat size: " + lat);

            renderedGrid = gridRendering(lon, lat, LonDPP);

            // for raster geo info
            QuadTree.Node ULNode = QT.searchFileName(ullon, ullat, LonDPP);
            QuadTree.Node LRNode = QT.searchFileName(lon.get(lon.size() - 1), lat.get(lat.size() - 1), LonDPP);
            raster_ul_lat = ULNode.ULLAT;
            raster_ul_lon = ULNode.ULLON;
            raster_lr_lat = LRNode.LRLAT;
            raster_lr_lon = LRNode.LRLON;

            System.out.println("LonDPP needed: " + LonDPP);
            System.out.println("LonDPP returned: " + ULNode.LonDPPcal());

            // depth calculation
            depth = ULNode.fileName.length();
            querySuccess = true;
        }

        results.put("render_grid", renderedGrid);
        results.put("raster_ul_lon", raster_ul_lon);
        results.put("raster_ul_lat", raster_ul_lat);
        results.put("raster_lr_lon", raster_lr_lon);
        results.put("raster_lr_lat", raster_lr_lat);
        results.put("depth", depth);
        results.put("query_success", querySuccess);

        showGrid();


        return results;
    }

    private void showGrid() {
       for (String[] row : renderedGrid) {
           for (String s : row)
               System.out.print(s + " ");
           System.out.println();
       }
    }

    private String[][] gridRendering(List<Double> x, List<Double> y, double LonDPP) {
        String[][] result = new String[y.size()][x.size()];

        for (int i = 0; i < y.size(); i++) {
            for (int j = 0; j < x.size(); j++) {
                result[i][j] = imgRoot + QT.searchFileName(x.get(j), y.get(i), LonDPP).fileName + ".png";
            }
        }

        return result;
    }

    private double LonDPPCal(double ullon, double lrlon, double w) {
        return Math.abs(ullon - lrlon) / w;
    }


}
