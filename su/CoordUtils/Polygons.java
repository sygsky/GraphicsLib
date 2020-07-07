package su.CoordUtils;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Ray-tracing algorithm of point testing to be in oply
 * handle with different polygons
 */
public class Polygons {

    public static void main( String[] args ) {

    }

    /**
     * method to check if a Coordinate is located in a poly. Poly is not
     * expected to be completed that is the last point is not equal to the
     * first
     *
     * @param pnt
     *         {@link Point2D}
     * @param poly
     *         {@link List} of {@link Point2D}
     *
     * @return true if pntis  in poly else false
     */
    public static final boolean isPointInPolygon( Point2D pnt, List<Point2D> poly ) {
        //this method uses the ray tracing algorithm to determine if the point is in the polygon
        int nPoints = poly.size();
        int j = -999;
        int i = -999;
        double testX = pnt.getX();
        double testY = pnt.getY();
        boolean locatedInPolygon = false;
        for ( i = 0; i < ( nPoints ); i++ ) {
            //repeat loop for all sets of points
            //if i is the last vertex, let j be the first vertex
            //for all-else, let j=(i+1)th vertex
            if ( i == ( nPoints - 1 ) )
                j = 0;
            else
                j = i + 1;

            double vertY_i = poly.get( i ).getY();
            double vertX_i = poly.get( i ).getX();
            double vertY_j = poly.get( j ).getY();
            double vertX_j = poly.get( j ).getX();

            // following statement checks if testPoint.Y is below Y-coord of i-th vertex
            boolean belowLowY = vertY_i > testY;
            // following statement checks if testPoint.Y is below Y-coord of i+1-th vertex
            boolean belowHighY = vertY_j > testY;

            /* following statement is true if testPoint.Y satisfies either (only one is possible)
            -->(i).Y < testPoint.Y < (i+1).Y        OR
            -->(i).Y > testPoint.Y > (i+1).Y

            (Note)
            Both of the conditions indicate that a point is located within the edges of the Y-th coordinate
            of the (i)-th and the (i+1)- th vertices of the polygon. If neither of the above
            conditions is satisfied, then it is assured that a semi-infinite horizontal line draw
            to the right from the testpoint will NOT cross the line that connects vertices i and i+1
            of the polygon
            */
            boolean withinYsEdges = belowLowY != belowHighY;

            if ( withinYsEdges ) {
                // this is the slope of the line that connects vertices i and i+1 of the polygon
                double slopeOfLine = ( vertX_j - vertX_i ) / ( vertY_j - vertY_i );

                // this looks up the x-coord of a point lying on the above line, given its y-coord
                double pointOnLine = ( slopeOfLine * ( testY - vertY_i ) ) + vertX_i;

                //checks to see if x-coord of testPoint is smaller than the point on the line with the same y-coord
                boolean isLeftToLine = testX < pointOnLine;

                if ( isLeftToLine ) {
                    //this statement changes true to false (and vice-versa)
                    locatedInPolygon = !locatedInPolygon;
                }//end if (isLeftToLine)
            }//end if (withinYsEdges
        }
        return locatedInPolygon;
    }
    public static final boolean PointIsInPoly( double[] pnt, double[] polyx, double[] polyy ) {
        {
            int i, j;
            boolean res = false;
            for (i = 0, j = polyx.length-1; i < polyx.length; j = i++) {
                final double pnty = pnt[1];
                final double pntx = pnt[0];
                final double poly_i = polyy[i];
                final double polx_i = polyx[i];
                final double poly_j = polyy[j];
                final double polx_j = polyx[j];
                if ( (( poly_i > pnty ) != ( poly_j > pnty ) ) &&
                        ( pntx < ( polx_j - polx_i ) * ( pnty - poly_i ) / ( poly_j - poly_i ) + polx_i ) )
                    res = !res;
            }
            return res;
        }
    }
}
