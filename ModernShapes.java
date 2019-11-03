import java.util.ArrayList;
import java.util.Scanner;

public class ModernShapes{

  public static class Connection{
    public Point p1, p2;
    public ConnectionType connectionType;
    public Connection(Point p1, Point p2, ConnectionType connectionType){
      this.p1 = p1;
      this.p2 = p2;
      this.connectionType = connectionType;
    }
  }

  public static enum ConnectionType{Line, Semicircle}
  
  public static double gamma(double v){
    return 1 / Math.sqrt(1 - Math.pow(v,2));
  }
  
  public static Point pointSeen(Point p, Point pObserver, double vx){
    double contractedX = pObserver.x + (p.x - pObserver.x) / gamma(vx);
    double a = 1 - Math.pow(vx, 2);
    double b = -2 * contractedX;
    double c = Math.pow(contractedX - pObserver.x, 2) - Math.pow(vx, 2) * Math.pow(p.y - pObserver.y, 2);
    double radical = Math.sqrt(Math.pow(b, 2) - 4 * a * c);
    double newX = (-b + radical)/(2 * a);
    return new Point(newX, p.y);
  }

  public static double distance(Point p1, Point p2){
    return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
  }

  public static ArrayList<Point> lineToPointList(Point p1, Point p2, double pointSeperation, double startingSeperation){
    ArrayList<Point> points = new ArrayList<>();
    double delx = p2.x - p1.x;
    double dely = p2.y - p1.y;
    double dx = delx / Math.sqrt(Math.pow(delx, 2) + Math.pow(dely, 2)) * pointSeperation;
    double dy = dely / Math.sqrt(Math.pow(delx, 2) + Math.pow(dely, 2)) * pointSeperation;
    double xOn = p1.x + dx * startingSeperation / pointSeperation;
    double yOn = p1.y + dy * startingSeperation / pointSeperation;
    double xsign = Math.abs(dx) / dx;
    double ysign = Math.abs(dy) / dy;
    while(xOn * xsign <= p2.x * xsign || yOn * ysign <= p2.y * ysign){
      points.add(new Point(xOn, yOn));
      System.out.println(yOn);
      xOn += dx;
      yOn += dy;
    }
    return points;
  }

  public static ArrayList<Point> semicricleToPointList(Point p1, Point p2, double pointSeperation, double startingSeperation){
    ArrayList<Point> points = new ArrayList<>();
    Point center = new Point((p1.x + p2.x)/2, (p1.y + p2.y)/2);
    double radius = distance(center, p1);
    double startingAngle = Math.atan((p1.y - center.y)/(p1.x - center.x)) + startingSeperation / radius;
    for(double angle = startingAngle; angle <= startingAngle + Math.PI; angle += pointSeperation / radius){
      double x = Math.cos(angle) * radius + center.x;
      double y = Math.sin(angle) * radius + center.y;
      points.add(new Point(x,y));
    }
    return points;
  }

  public static ArrayList<Point> generatePolygonPoints(ArrayList<Pair<Point,ConnectionType>> shapeInfo, double pointSeperation){
    ArrayList<Point> points = new ArrayList<>();
    Point lastPoint = shapeInfo.get(shapeInfo.size() - 1).first;
    double startingSeperation = 0;
    for(Pair<Point,ConnectionType> currentInfo : shapeInfo){
      switch (currentInfo.second){
        case Semicircle: 
          points.addAll(semicricleToPointList(lastPoint, currentInfo.first, pointSeperation, startingSeperation));
          break;
        case Line: 
          points.addAll(lineToPointList(lastPoint, currentInfo.first, pointSeperation, startingSeperation));
          break;
      }
      //startingSeperation = pointSeperation - distance(points.get(points.size() - 1), currentInfo.first);
      lastPoint = currentInfo.first;
    }
    return points;
  }

  public static ArrayList<Point> generateConnectionPoints(ArrayList<Connection> connections, double pointSeperation){
    ArrayList<Point> points = new ArrayList<>();
    for(Connection conn : connections){
      switch (conn.connectionType){
        case Semicircle: points.addAll(semicricleToPointList(conn.p1, conn.p2, pointSeperation, 0)); break;
        case Line:       points.addAll(lineToPointList(      conn.p1, conn.p2, pointSeperation, 0)); break;
      }
    }
    return points;
  }

  public static ArrayList<Point> pointsSeen(ArrayList<Point> points, Point pObserver, double vx){
    ArrayList<Point> pointsSeen = new ArrayList<>();
    for(Point p : points){pointsSeen.add(pointSeen(p, pObserver, vx));}
    return pointsSeen;
  }

  public static void printXs(ArrayList<Point> points){
    for(Point p : points){System.out.println(p.x);}
  }
  public static void printYs(ArrayList<Point> points){
    for(Point p : points){System.out.println(p.y);}
  }

  public static<T> T optionQuestion(String question, ArrayList<Pair<Character, T>> options){
    Scanner s = new Scanner(System.in);
    String optionsString = "(";
    for(Pair<Character, T> possibility : options){
      optionsString += possibility.first + ", ";
    }
    optionsString = optionsString.substring(0, optionsString.length() - 2) + ")";
    while(true){
      System.out.println(question + " " + optionsString);
      char answer = s.next().toLowerCase().charAt(0);
      for(Pair<Character, T> possibility : options){
        if (answer == possibility.first){
          return possibility.second;
        }
      }
    }
  }

  public static boolean boolQuestion(String question){
    ArrayList<Pair<Character, Boolean>> mapping = new ArrayList<Pair<Character, Boolean>>();
    mapping.add(new Pair<>('y', true));
    mapping.add(new Pair<>('n', false));
    return optionQuestion(question, mapping);
  }

  public static ArrayList<Point> polygonPoints(){
    Scanner s = new Scanner(System.in);
    System.out.println("what point seperation do you want?");
    double pointSeperation = s.nextDouble();
    System.out.println("For each point, type in the x-coordinate and than the y-coordinate.");
    boolean complete = false;
    ArrayList<Pair<Point, ConnectionType>> shapeInfo = new ArrayList<>();
    ArrayList<Pair<Character, ConnectionType>> conCharMap = new ArrayList<Pair<Character, ConnectionType>>();
    conCharMap.add(new Pair<>('l',ConnectionType.Line));
    conCharMap.add(new Pair<>('s',ConnectionType.Semicircle));
    while(!complete){
      System.out.println("x of p" + shapeInfo.size() + ":");
      double x = s.nextDouble();
      System.out.println("y of p" + shapeInfo.size() + ":");
      double y = s.nextDouble();
      //shapeInfo.add(new Pair<>(new Point(x, y), ConnectionType.Line));
      shapeInfo.add(new Pair<>(new Point(x, y), optionQuestion("Do you want a line or semicircle connection?", conCharMap)));
      if (shapeInfo.size() > 1 && !boolQuestion("Would you like to keep on going?")){
        break;
      }
    }
    return generatePolygonPoints(shapeInfo, pointSeperation);
  }

  public static void printValues(ArrayList<Point> points){
    System.out.println("Original Xs: ");
    printXs(points);
    System.out.println("Original Ys: ");
    printYs(points);
  }
  
  public static void main(String[] args){
    Point pObserver = new Point(0,0);
    Scanner s = new Scanner(System.in);
    System.out.println("what point seperation do you want?");
    double pointSeperation = s.nextDouble();

    ArrayList<Connection> connections = new ArrayList<>();
    Point rightLeg = new Point(2, 0);
    Point croch = new Point(0, 2);
    Point leftLeg = new Point(-2, 0);
    Point shoulders = new Point(0,4);
    Point rightArm = new Point(1, 3);
    Point leftArm = new Point(-1, 3);
    Point neck = new Point(0, 4.3);
    Point head = new Point (0, 5);
    connections.add(new Connection(rightLeg, croch, ConnectionType.Line));
    connections.add(new Connection(leftLeg, croch, ConnectionType.Line));
    connections.add(new Connection(shoulders, croch, ConnectionType.Line));
    connections.add(new Connection(rightArm, shoulders, ConnectionType.Line));
    connections.add(new Connection(leftArm, shoulders, ConnectionType.Line));
    connections.add(new Connection(shoulders, neck, ConnectionType.Line));
    connections.add(new Connection(neck, head, ConnectionType.Semicircle));
    connections.add(new Connection(head, neck, ConnectionType.Semicircle));
    //connections.add(new Connection(leftLeg, croch, ConnectionType.Line));

    ArrayList<Point> points = generateConnectionPoints(connections, pointSeperation);
    printValues(points);
  }
  
}