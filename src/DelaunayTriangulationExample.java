import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DelaunayTriangulationExample {

	private static List<Point> pointSet;
    private static TriangleSoup triangleSoup;
    private static final float AREA = 3;

    public static void main(String[] args) {
    	pointSet = readVertexes("vertexes.txt");    	
    	//pointSet = generate(100, 50, 150);
    	DelaunayTriangulator delaunayTriangulator = new DelaunayTriangulator(pointSet);
    	try {
            delaunayTriangulator.triangulate();
            triangleSoup = delaunayTriangulator.getTriangleSoup();
            modifyTriangles();
            //triangleSoup.getIsoline(50);
            /*Iterator<Triangle2D> it = triangleSoup.iterator();
            Triangle2D triangle;
            while (it.hasNext()) {
            	triangle = it.next();
            	if (distance(triangle.a, triangle.b) > AREA ||
            			distance(triangle.a, triangle.c) > AREA ||
            			distance(triangle.b, triangle.c) > AREA) {
            		it.remove();
            	}
            }*/
            System.out.println(triangleSoup);
        } catch (NotEnoughPointsException e) {
        	e.printStackTrace();
        }
    	
    	MyFrame myFrame = new MyFrame();
    	myFrame.setTitle("Triangulation");
    	myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	myFrame.setVisible(true);
    	myFrame.setSize(950, 950);
    }
    
    private static void modifyTriangles() {
    	/*for (Triangle triangle : triangleSoup) {
    		if (distance(triangle.a, triangle.b) > AREA ||
        			distance(triangle.a, triangle.c) > AREA ||
        			distance(triangle.b, triangle.c) > AREA) {
    			triangleSoup.remove(triangle);
    		}
    	}*/
    	Iterator<Triangle> it = triangleSoup.iterator();
        Triangle triangle;
        while (it.hasNext()) {
        	triangle = it.next();
        	if (distance(triangle.a, triangle.b) > AREA ||
        			distance(triangle.a, triangle.c) > AREA ||
        			distance(triangle.b, triangle.c) > AREA) {
        		it.remove();
        	}
        }
    }

	private static List<Point> readVertexes(String fileName) {
	    List<Point> pointSet = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File(fileName))) {
			while (scanner.hasNext()) {
				pointSet.add(new Point(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return pointSet;
	}

	private static List<Point> generate(int n, int range, int depth) {
	    List<Point> pointSet = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			pointSet.add(new Point(Math.random() * range, Math.random() * range, Math.random() * depth));
		}
		return pointSet;
	}
	
	static class MyFrame extends JFrame {
		
		private final static int SCALE = 50;
		private final static int POINT_RADIUS = 6;

		private final static double STEP = .1;
		
		Point p1, p2, p3;
		
		double minX = pointSet.get(0).x, minY = pointSet.get(0).y, maxX = 0, maxY = 0;
		int height, width;
		
		double[][] depthMap;
		
		MyFrame() {
			
			double maxOfAnyCoordinate = 0.0d;

	        for (Point vector : pointSet) {
	            maxOfAnyCoordinate = Math.max(Math.max(vector.x, vector.y), maxOfAnyCoordinate);
	            minX = Math.min(minX, vector.x);
	            minY = Math.min(minY, vector.y);
	            maxX = Math.max(maxX, vector.x);
	            maxY = Math.max(maxY, vector.y);
	        }
	        height = (int) Math.ceil((maxY - minY) / STEP);
	        width = (int) Math.ceil((maxX - minX) / STEP);
	        
	        depthMap = new double[height + 1][width + 1];

	        maxOfAnyCoordinate *= 16.0d;

	        p1 = new Point(0.0d, 3.0d * maxOfAnyCoordinate);
	        p2 = new Point(3.0d * maxOfAnyCoordinate, 0.0d);
	        p3 = new Point(-3.0d * maxOfAnyCoordinate, -3.0d * maxOfAnyCoordinate);
	        
			Container c = this.getContentPane();
			c.add(new JPanel() {
				@Override
			    public void paintComponent(Graphics g) {
			        super.paintComponent(g);
			        
			        drawEdge(g, p1, p2);
			        drawEdge(g, p1, p3);
			        drawEdge(g, p3, p2);
			        
			        for (Point point : pointSet) {
			        	g.fillOval((int) (point.x * SCALE - POINT_RADIUS / 2), (int) (point.y * SCALE - POINT_RADIUS / 2), POINT_RADIUS, POINT_RADIUS);
			        }
			        
			        /*for (Triangle triangle : triangleSoup) {
			        	fillTriangle(g, triangle);
			        }*/
			        
			        for (Triangle triangle : triangleSoup) {
			        	drawEdge(g, triangle.a, triangle.b);
			        	drawEdge(g, triangle.a, triangle.c);
			        	drawEdge(g, triangle.c, triangle.b);
			        }
			        

					g.setColor(Color.BLACK);
			        for (int i = 0; i <= height; i++) {
			        	for (int j = 0; j <= width; j++) {
			        		double x = minX + j * STEP;
			        		double y = minY + i * STEP;
			        		Triangle triangle = triangleSoup.findContainingTriangleNew(new Point(x, y));
			        		if (triangle != null) {
				        		depthMap[i][j] = triangle.getDepth(x,  y);
				        		drawDot(g, x, y, (int) depthMap[i][j]);			        			
			        		} else {
			        			depthMap[i][j] = -1;
			        		}
			        	}
			        }
			    }
				
				private void drawEdge(Graphics g, Point a, Point b) {
					if (Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2)) <= AREA)
						g.setColor(Color.BLACK);
					else g.setColor(Color.RED);
			        g.drawLine((int) (a.x * SCALE), (int) (a.y * SCALE), (int) (b.x * SCALE), (int) (b.y * SCALE));
				}
				
				private void fillTriangle(Graphics g, Triangle triangle) {
					

					triangle.calculateNormal();
					
					if (triangle.a.x == 8 && triangle.b.x == 8 && triangle.c.x == 9) {
						System.out.println();		
					}
					
					
					float minX = min(triangle.a.x, triangle.b.x, triangle.c.x);
					float minY = min(triangle.a.y, triangle.b.y, triangle.c.y);
					float maxX = max(triangle.a.x, triangle.b.x, triangle.c.x);
					float maxY = max(triangle.a.y, triangle.b.y, triangle.c.y);

					//
					//System.out.println(minY + " " + maxY);
					//System.out.println(minX + " " + maxX);
					
					for (double y = ((int) (minY / STEP)) * STEP; y <= maxY; y += STEP) {
						for (double x = ((int) (minX / STEP)) * STEP; x <= maxX; x += STEP) {
							
							if (x == 4 && y == 9) {
								System.out.println(y + x);
							}
							//System.out.println(i + j);
							if (triangle.containsNew(new Point(x, y))) {
								System.out.println(triangle);
								int add = (int) triangle.getDepth(x, y);
								System.out.println(x + " " + y + " " + add);
								g.setColor(new Color(100 + add, 255 - add, 100));
								g.fillRect((int) ((x - STEP / 2) * SCALE), (int) ((y - STEP / 2) * SCALE), (int) (STEP * SCALE), (int) (STEP * SCALE)); 
							}
						}
					}
				}
				
				private void drawDot(Graphics g, double x, double y, int depth) {
					g.setColor(new Color(100 + depth, 255 - depth, 100));
					g.drawRect((int) ((x - STEP / 2) * SCALE), (int) ((y - STEP / 2) * SCALE), (int) (STEP * SCALE), (int) (STEP * SCALE));
				}
				
				private float max(double... args) {
					float max = (float) args[0];
					for (double arg : args) {
						max = (float) Math.max(arg, max);
					}
					return max;
				}
				
				private float min(double... args) {
					float min = (float) args[0];
					for (double arg : args) {
						min = (float) Math.min(arg, min);
					}
					return min;
				}
			});
			
		}
		
	}
	
	static float distance(Point a, Point b) {
		return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

}
