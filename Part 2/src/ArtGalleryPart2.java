import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import math.geom2d.Point2D;
import math.geom2d.line.DegeneratedLine2DException;
import math.geom2d.line.Line2D;
import math.geom2d.polygon.SimplePolygon2D;

public class ArtGalleryPart2 {
	static ArrayList<String> coord = new ArrayList<String>();
	static ArrayList<Line2D> border = new ArrayList<Line2D>();
	static ArrayList<String> invisCoord = new ArrayList<String>();
	static ArrayList<Line2D> invisBorder = new ArrayList<Line2D>();
	static ArrayList<String> guards = new ArrayList<String>();
	static SimplePolygon2D p;
	
	public static void main(String[] args) {
		readCoordinates();
		computeBorders();
		readGuards();
		checkGuards();
	}
	
	private static void checkGuards() {
		// check if all vertex of the polygon is visible to the guards
		for (int i = 0; i < guards.size(); i++) {
			for (int j = 0; j < coord.size(); j++) {
				if (checkVisible(guards.get(i), coord.get(j))) {
					invisCoord.remove(coord.get(j));
				}
			}
		}
		if (!invisCoord.isEmpty()) {
			System.out.println("Coord Refutation: " + invisCoord.get(0));
			return;
		}
		
		// check if all two pairs of adjacent vertex (line) is visible to the guards		
		for (int i = 0; i < guards.size(); i++) {
			for (int j = 0; j < border.size(); j++) {
				double x = Double.parseDouble(guards.get(i).substring(1, guards.get(i).indexOf(',')));
				double y = Double.parseDouble(guards.get(i).substring(guards.get(i).indexOf(',') + 2, guards.get(i).length()));
				Point2D guard = new Point2D(x, y);
				
				if (border.get(j).getPoint1().equals(guard) || border.get(j).getPoint2().equals(guard)) {
					invisBorder.remove(border.get(j));
				}
				else {
					if (checkVisible(guard, border.get(j).getPoint1()) && checkVisible(guard, border.get(j).getPoint2())) {
						invisBorder.remove(border.get(j));
					}
				}
			}
		}
		if (!invisBorder.isEmpty()) {
			boolean visible = false;
			double refuteX = 0;
			double refuteY = 0;
			for (int i = 0; i < invisBorder.size(); i++) {
				for (int j = 0; j < guards.size(); j++) {
					refuteX = (invisBorder.get(i).getX1() + invisBorder.get(i).getX2())/2;
					refuteY = (invisBorder.get(i).getY1() + invisBorder.get(i).getY2())/2;
					Point2D refutePt = new Point2D(refuteX, refuteY);
					double compareX = Double.parseDouble(guards.get(j).substring(1, guards.get(j).indexOf(',')));
					double compareY = Double.parseDouble(guards.get(j).substring(guards.get(j).indexOf(',') + 2, guards.get(j).length()));
					Point2D comparePt = new Point2D(compareX, compareY);
					if (checkVisible(refutePt, comparePt)) {
						visible = true;
						break;
					}
				}
				if (visible) {
					visible = false;
					continue;
				}
				else {
					String pt = "(" + String.valueOf(refuteX) + ", " + String.valueOf(refuteY);
					System.out.println("Border Refutation: " + pt);
					return;
				}
			}
		}
	}
	
	private static boolean checkVisible(Point2D source, Point2D target) {
		Line2D visLine = new Line2D(source, target);
		
		for (int i = 0; i < border.size(); i++) {
			Line2D compareLine = border.get(i);
			// if two points are adjacent
			if ((visLine.getPoint1().equals(compareLine.getPoint1()) && visLine.getPoint2().equals(compareLine.getPoint2())) || (visLine.getPoint1().equals(compareLine.getPoint2()) && visLine.getPoint2().equals(compareLine.getPoint1()))) { 
				return true;
			}
			// if two lines intersect
			if (!Line2D.intersects(visLine, compareLine)) {
				continue;
			}
			else {
				// intersects a line at its end/start point
				if (visLine.contains(compareLine.getPoint1()) || visLine.contains(compareLine.getPoint2())) {
					continue;
				}
				// if compareLine is a segment of visLine
				if (visLine.contains(compareLine.getPoint1()) && visLine.contains(compareLine.getPoint2())) {
					continue;
				}
				// intersects a line at its end/start point
				if (compareLine.contains(visLine.getPoint1()) || compareLine.contains(visLine.getPoint2())) {
					continue;
				}
				// if visLine is a segment of compareLine
				if (compareLine.contains(visLine.getPoint1()) && compareLine.contains(visLine.getPoint2())) {
					continue;
				}
				// intersects a line that doesn't share a start/end coordinate
				if (!(visLine.getPoint1().equals(compareLine.getPoint1()) || visLine.getPoint1().equals(compareLine.getPoint2()) || visLine.getPoint2().equals(compareLine.getPoint1()) || visLine.getPoint2().equals(compareLine.getPoint2()))) {
					return false;
				}
			}
		}
		// if visLine isn't completely inside polygon
		for (int i = 1; i < 50; i++) {
			double diffX = visLine.getX2()-visLine.getX1();
			double diffY = visLine.getY2()-visLine.getY1();
			double x = visLine.getX1()+ diffX/50*i;
			double y = visLine.getY1()+ diffY/50*i;
			if (!p.contains(x, y)) {
				return false;
			}
		}
//		if (!p.contains((visLine.getX1()+visLine.getX2())/2, (visLine.getY1()+visLine.getY2())/2)) {
//			return false;
//		}
		return true;
	}
	
	private static boolean checkVisible(String source, String target) {
		try {
			double sourcex, sourcey, targetx, targety;
			sourcex = Double.parseDouble(source.substring(1, source.indexOf(',')));
			sourcey = Double.parseDouble(source.substring(source.indexOf(',') + 2, source.length()));
			targetx = Double.parseDouble(target.substring(1, target.indexOf(',')));
			targety = Double.parseDouble(target.substring(target.indexOf(',') + 2, target.length()));
			Line2D visLine = new Line2D(sourcex, sourcey, targetx, targety);
			
			for (int i = 0; i < border.size(); i++) {
				Line2D compareLine = border.get(i);
				// if two points are adjacent
				if ((visLine.getPoint1().equals(compareLine.getPoint1()) && visLine.getPoint2().equals(compareLine.getPoint2())) || (visLine.getPoint1().equals(compareLine.getPoint2()) && visLine.getPoint2().equals(compareLine.getPoint1()))) { 
					return true;
				}
				// if two lines intersect
				if (!Line2D.intersects(visLine, compareLine)) {
					continue;
				}
				else {
					// intersects a line at its end/start point
					if (visLine.contains(compareLine.getPoint1()) || visLine.contains(compareLine.getPoint2())) {
						continue;
					}
					// if compareLine is a segment of visLine
					if (visLine.contains(compareLine.getPoint1()) && visLine.contains(compareLine.getPoint2())) {
						continue;
					}
					// intersects a line at its end/start point
					if (compareLine.contains(visLine.getPoint1()) || compareLine.contains(visLine.getPoint2())) {
						continue;
					}
					// if visLine is a segment of compareLine
					if (compareLine.contains(visLine.getPoint1()) && compareLine.contains(visLine.getPoint2())) {
						continue;
					}
					// intersects a line that doesn't share a start/end coordinate
					if (!(visLine.getPoint1().equals(compareLine.getPoint1()) || visLine.getPoint1().equals(compareLine.getPoint2()) || visLine.getPoint2().equals(compareLine.getPoint1()) || visLine.getPoint2().equals(compareLine.getPoint2()))) {
						return false;
					}
				}
			}
			// if visLine isn't completely inside polygon
			for (int i = 1; i < 50; i++) {
				double diffX = visLine.getX2()-visLine.getX1();
				double diffY = visLine.getY2()-visLine.getY1();
				double x = visLine.getX1()+ diffX/50*i;
				double y = visLine.getY1()+ diffY/50*i;
				if (!p.contains(x, y)) {
					return false;
				}
			}
	//		if (!p.contains((visLine.getX1()+visLine.getX2())/2, (visLine.getY1()+visLine.getY2())/2)) {
	//			return false;
	//		}
			return true;
		}
		catch (DegeneratedLine2DException e) {
			e.getLine();
		}
		return true;
	}
	
	private static void readGuards() {
		String delimiter = "\\), ";
		try {
			Scanner read = new Scanner(new File("C:\\Users\\litao_000\\Desktop\\guard.txt"));
			read.useDelimiter(delimiter);
			while (read.hasNext()) {
				String text = read.next();
				guards.add(text);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void computeBorders() {
		double[] x;
		double[] y;
		x = new double[coord.size()];
		y = new double[coord.size()];
		for (int i = 0; i < coord.size(); i++) {
			x[i] = Double.parseDouble(coord.get(i).substring(1, coord.get(i).indexOf(',')));
			y[i] = Double.parseDouble(coord.get(i).substring(coord.get(i).indexOf(',') + 2, coord.get(i).length()));
		}
		for(int i = 0; i < coord.size() - 1; i++) {
			invisBorder.add(new Line2D(x[i], y[i], x[i+1], y[i+1]));
			border.add(new Line2D(x[i], y[i], x[i+1], y[i+1]));
		}
		invisBorder.add(new Line2D(x[coord.size()-1], y[coord.size()-1], x[0], y[0]));
		border.add(new Line2D(x[coord.size()-1], y[coord.size()-1], x[0], y[0]));
		
		p = new SimplePolygon2D(x, y);
	}
	
	private static void readCoordinates() {
		String delimiter = "\\), ";
		try {
			Scanner read = new Scanner(new File("C:\\Users\\litao_000\\Desktop\\data.txt"));
			read.useDelimiter(delimiter);
			while (read.hasNext()) {
				String text = read.next();
				invisCoord.add(text);
				coord.add(text);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
