package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {
    private double x;
    private double y;
    public CloudPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public CloudPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
    }
    public void mergePoints(CloudPoint point) {
        this.x=(this.x+ point.x)/2;
        this.y=(this.y+ point.y)/2;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getDistance() {
        return (double) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
