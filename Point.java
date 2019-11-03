public class Point{
  public double x, y;
  public Point(double x, double y){
    this.x = x;
    this.y = y;
  }

  private double rounder(double v){
    int digits = 3;
    return (int) (v * Math.pow(10, 3)) / Math.pow(10.0, 3);
  }

  @Override
  public String toString(){
    return "(" + rounder(this.x) + ", " + rounder(this.y) + ")";
  }
}