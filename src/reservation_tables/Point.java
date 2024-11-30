package reservation_tables;

public class Point {
    private int x; // x-coordinate
    private String y; // y-coordinate
    private String z; // y-coordinate
    private String op;
    

    // Constructor
    public Point(int x, String y,String z,String op) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.op = op;
        
    }

    // Getter for x
    public int getX() {
        return x;
    }

    // Setter for x
    public void setX(int x) {
        this.x = x;
    }

    // Getter for y
    public String getY() {
        return y;
    }

    // Setter for y
    public void setY(String y) {
        this.y = y;
    }
    // Getter for z
    public String getZ() {
        return z;
    }

    // Setter for z
    public void setZ(String z) {
        this.z = z;
    }
 // Getter for z
    public String getOp() {
        return op;
    }

    // Setter for z
    public void setOp(String op) {
        this.op = op;
    }
 
 


    @Override
    public String toString() {
        return "Point{" +
               "x=" + x +
               ", y=" + y +
               ", z=" + z +
               ", op=" + op +
               '}';
    }
}
