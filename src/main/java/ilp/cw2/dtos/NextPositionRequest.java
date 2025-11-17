package ilp.cw2.dtos;

public class NextPositionRequest {
    private Point start;
    private double angle;

    public Point getStart() {
        return start;
    }
    public void setStart(Point start) {
        this.start = start;
    }
    public double getAngle() {
        return angle;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public NextPositionRequest (Point start, double angle) {
        this.start = start;
        this.angle = angle;
    }
}
