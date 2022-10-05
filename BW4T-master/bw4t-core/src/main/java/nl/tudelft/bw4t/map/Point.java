package nl.tudelft.bw4t.map;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

/**
 * Stupid coverr around Point2D.Double. The problem with Point2D.Double is that
 * the XML serializer can not handle it, it gets stuck in an infinite recursion
 * if you try to serialize it.
 */
public class Point implements Serializable {

    /**
     * Serial id.
     */
    private static final long serialVersionUID = -7842093296385905576L;
    
    private double x,y,z;
    /** 
     * Initialize point.
     */
    private Point2D.Double point = new Point2D.Double();

    /** 
     * Empty constructor, intialize point.
     */
    public Point() {
    }

    /** Constructor 
     * 
     * @param newx double
     *         x coordinate
     * @param newy double
     *         y coordinate
     */
//    public Point(double newx, double newy) {
//        point = new Point2D.Double(newx, newy);
//    }
    
    /** Constructor 
     * 
     * @param newx double
     *         x coordinate
     * @param newy double
     *         y coordinate
     * @param newz double
     *         z coordinate
     */
    public Point(double newx, double newy,double newz) {
        this.x=newx;
        this.y=newy;
    	this.z=newz;
    }

    public double getX() {
        //return point.x;
    	return this.x;
    }

    public void setX(double x) {
        //point.x = x;
    	this.x=x;
    }

    public double getY() {
        //return point.y;
    	return this.y;
    }

    public void setY(double y) {
        //point.y = y;
    	this.y=y;
    }
    
    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Point2D getPoint2D() {
        return new Point2D.Double(this.x,this.y);
    }
    
    public Point getPoint() {
        return new Point(this.getX(),this.getY(),this.getZ());
    }

    @Override
    public int hashCode() {
        return point.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Point other = (Point) obj;
        if ((x!=other.x)||(y!=other.y)||(z!=other.z)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return point.toString();
    }

}
