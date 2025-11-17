package ilp.cw2.dtos;

public class PositionRequest {

    private Point position1;
    private Point position2;

    public Point getPosition1() {
        return position1;
    }
    public void setPosition1(Point position1) {
        this.position1 = position1;
    }
    public Point getPosition2() {
        return position2;
    }
    public void setPosition2(Point position2) {
        this.position2 = position2;
    }
    public PositionRequest(Point position1, Point position2) {
        this.position1 = position1;
        this.position2 = position2;
    }
}
