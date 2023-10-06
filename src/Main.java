import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

class ClosestPairOfPoints{
    /*
    Calculates the closest distance between the two points on a planar
    graph.
    */

    public PlanarPoint[] sort_by_x(PlanarPoint[] points, int arr_length){
        /*
        Sorts the given pair of points according to the x-axis
        */
        PlanarPoint[] points_x_sorted = Arrays.copyOf(points, arr_length);
        Arrays.sort(points_x_sorted, Comparator.comparingInt(p -> p.x_co_ordinate));

        return points_x_sorted;
    }

    public PlanarPoint[] sort_by_y(PlanarPoint[] points, int arr_length){
        /*
        Sorts the given pair of points according to the y-axis
        */
        PlanarPoint[] points_y_sorted = Arrays.copyOf(points, arr_length);
        Arrays.sort(points_y_sorted, Comparator.comparingInt(p -> p.y_co_ordinate));

        return points_y_sorted;
    }

    public double ClosestPoint(PlanarPoint[] points, int arr_length){
        /*
         Returns the closest distance between all pairs of points
        */
        PlanarPoint[] x_points_sorted = sort_by_x(points,arr_length);
        PlanarPoint[] y_points_sorted = sort_by_y(points, arr_length);

        return ClosestCalculatedPoint(x_points_sorted, y_points_sorted, arr_length);
    }

    public double ClosestCalculatedPoint(PlanarPoint[] x_points, PlanarPoint[] y_points, int arr_length){
        /*
        Calculates the distances between all the points and
        returns the closest distance between the points by
        first splitting the points by the midpoint and finds the smallest distance
        on the left of the split and then the right of split and lastly calculating
        the smallest distance in the strip between the midpoint and delta distance from midpoint
         */

        /* If the there are less than three elements in the list
           run a bruteforce algorithm to calculate the smallest distance
           as we dont have to go divide the program into small parts for values < 3
         */
        if (arr_length <= 3) {
            return naive_search(x_points, arr_length);
        }

        int mid = arr_length / 2;
        PlanarPoint midPoint = x_points[mid];

        // get all of y sorted values to the left of the dividing line
        PlanarPoint[] y_points_left = Arrays.copyOfRange(y_points,0,mid);
        // get all of y sorted values to the right of the dividing line
        PlanarPoint[] y_points_right = Arrays.copyOfRange(y_points,mid, arr_length);

        // Get the delta left(smallest distance) on the left of the dividing line by
        // repeated dividing the array into smaller halves
        double delta_left = ClosestCalculatedPoint(x_points,y_points_left, mid);
        // Get the delta right(smallest distance) on the right of the dividing line
        // repeated dividing the array into smaller halves
        double delta_right = ClosestCalculatedPoint(
                Arrays.copyOfRange(x_points,mid,arr_length),
                y_points_right,
                arr_length-mid
        );

        double delta = Math.min(delta_left,delta_right); // get the min between the delta left and right

        /*
        We need to create a segment which is Delta plus distance from the midpoint on
        either side, and use and array segment to store all the points closer than delta
        in the line running through the midpoint
         */
        List<PlanarPoint> segment_points = new ArrayList<PlanarPoint>();
        for (PlanarPoint point : y_points){
            if (Math.abs(point.x_co_ordinate - midPoint.x_co_ordinate) < delta){
                segment_points.add(point);
            }
        }

        // If the points in the segment have a smaller distance than the one on the left or right
        // update the value of smallest distance with that
        return segment_closest_point(
                segment_points.toArray(new PlanarPoint[segment_points.size()]),
                segment_points.size(),
                delta);
    }

    public static double segment_closest_point(PlanarPoint[] segment, int size, double delta){
        /*
        calculates the smallest distance between the points in the segment section on the planer
        graph segment section = mid-point + delta distance on either side of line
        */
        double minimum = delta;
        for (int x = 0; x < minimum; ++x) {
            for (int y= x+1; y < size &&
                    (segment[y].y_co_ordinate - segment[x].y_co_ordinate) < delta; ++y){
                double distance = distance(segment[x], segment[y]);
                if (distance < minimum) {
                    minimum = distance;
                }
            }
        }
        return minimum;
    }

    public static double naive_search(PlanarPoint[] Point, int arr_len ){
        /*
        Loop through all the given points when arr_len <=3 and returns
        the least distance between the given points
         */
        double min = Double.MAX_VALUE;
        for (int x = 0; x < arr_len; ++x) {
            for (int y = x + 1; y < arr_len; ++y) {
                double dist = distance(Point[x], Point[y]);
                if (dist < min) {
                    min = dist;
                }
            }
        }
        return min;
    }
    public static double distance(PlanarPoint p1, PlanarPoint p2) {
        /* Calculate the distance between two points using
           pythagorean theorem on a Euclidean plane
         */
        return Math.sqrt(Math.pow(p1.x_co_ordinate - p2.x_co_ordinate, 2)
                + Math.pow(p1.y_co_ordinate - p2.y_co_ordinate, 2));
    }
}

public class Main {
    /*
    Logs the time taken to calculate to the closest pair of point on planar graphs
    and outputs it to a file
     */

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        PlanarPoint[] P =  new PlanarPoint[n];
        Random rand = new Random();

        // Plot n number of  random points to the graph
        for(int a= 0; a < n; a++){
            int x = rand.nextInt(1,200);
            int y = rand.nextInt(1,200);
            P[a] = new PlanarPoint(x,y);
        }

        int arr_length = P.length;
        ClosestPairOfPoints closest_point = new ClosestPairOfPoints();

        // Get the running time of the ClosestPoint class to plot the graph
        long startTime = System.nanoTime();
        double closest_distance = closest_point.ClosestPoint(P, arr_length);
        long endTime = System.nanoTime();
        long timeDifference = endTime - startTime;

        System.out.println("The smallest distance is " + closest_distance);;

        // Write output to file for reference
        try(FileWriter fw = new FileWriter("time_outputs.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println("The time taken to run the program for n = " + n + " is:\n" + timeDifference + "\n");
        } catch (IOException e) {
            System.out.println("Unable to write contents to file");
        }

    }
}

// A Class to provide a way to show point on a planar surface
class PlanarPoint {
    public int x_co_ordinate;
    public int y_co_ordinate;

    public PlanarPoint(int x_co_ordinate, int y_co_ordinate) {
        this.x_co_ordinate = x_co_ordinate;
        this.y_co_ordinate = y_co_ordinate;
    }
}
