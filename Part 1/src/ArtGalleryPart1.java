import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import math.geom2d.Point2D;
import math.geom2d.line.Line2D;
import math.geom2d.polygon.SimplePolygon2D;

public class ArtGalleryPart1 {
	
	static ArrayList<String> coordinates = new ArrayList<String>();
	static HashMap<String, Integer> visibleCounts = new HashMap<String, Integer>();
	static ArrayList<String> notVisible = new ArrayList<String>();
	static ArrayList<String> guards = new ArrayList<String>();
	static ArrayList<Line2D> borders = new ArrayList<Line2D>();
	static ArrayList<Line2D> nonVisBorders = new ArrayList<Line2D>();
	static SimplePolygon2D p;
	
	
	public static void main(String[] args) {
		readCoordinates();
		computeBorders();
		computeGuards();
		checkGuards();
		System.out.println("Guard Positions: ");
		int count = 0;
		for (int i = 0; i < guards.size(); i++) {
			count++;
			System.out.print(guards.get(i)+"), ");
		}
		System.out.println();
		System.out.println("total number of guards: " + count);
	}
	
	private static void checkGuards() {
		for (int i = 0; i < borders.size(); i++) {
			for (int j = 0; j < guards.size(); j++) {
				double x = Double.parseDouble(guards.get(j).substring(1, guards.get(j).indexOf(',')));
				double y = Double.parseDouble(guards.get(j).substring(guards.get(j).indexOf(',') + 2, guards.get(j).length()));
				Point2D guard = new Point2D(x, y);
				if (borders.get(i).getPoint1().equals(guard) || borders.get(i).getPoint2().equals(guard)) {
					nonVisBorders.remove(borders.get(i));
				}
				else {
					if (checkVisible(guard, borders.get(i).getPoint1()) && checkVisible(guard, borders.get(i).getPoint2())) {
						nonVisBorders.remove(borders.get(i));
					}
				}
			}
		}
		int count = 0;
		for (int i = 0; i < nonVisBorders.size(); i++) {
			String guard = "(" + String.valueOf(nonVisBorders.get(i).getX1()) + ", " + String.valueOf(nonVisBorders.get(i).getY1());
			System.out.println("additional guards:");
			System.out.println(guard);
			guards.add(guard);
			count++;
		}
		System.out.println("added " + count + " additional guards");
	}
	
	private static void computeGuards() {
		int visibleCount = 0;
		visibleCounts.clear();
		for (int i = 0; i < notVisible.size(); i++) {
			for (int j = 0; j < coordinates.size(); j++) {
				System.out.println(notVisible.get(i) + " " + coordinates.get(j));
				if (!notVisible.get(i).equals(coordinates.get(j))) {
					if (checkVisible(notVisible.get(i), coordinates.get(j))) {
						visibleCount++;
					}
				}
			}
			visibleCounts.put(notVisible.get(i), visibleCount++);
			visibleCount = 0;
		}
		int biggestVis = 0;
		String mostVis = null;
		for (HashMap.Entry<String, Integer> entry : visibleCounts.entrySet()) {
		    if (entry.getValue() > biggestVis) {
		    	mostVis = entry.getKey();
		    	biggestVis = entry.getValue();
		    }
		}
		if (biggestVis == 0) {
			for (HashMap.Entry<String, Integer> entry : visibleCounts.entrySet()) {
				guards.add(entry.getKey());
			}
			return;
		}
		else {
			guards.add(mostVis);
			for (int i = 0; i < notVisible.size(); i++) {
				if (notVisible.get(i).equals(mostVis)) {
					notVisible.remove(notVisible.get(i));
				}
			}
			removeVisible(mostVis);
			if (notVisible.isEmpty()) {
				return;
			}
			else {
				computeGuards();
			}
		}
	}
	
	private static void removeVisible(String mostVis) {
		for (int i = 0; i < coordinates.size(); i++) {
			if (!coordinates.get(i).equals(mostVis)) {
				if (checkVisible(mostVis, coordinates.get(i))) {
					notVisible.remove(coordinates.get(i));
				}
			}
		}
	}
	
	private static boolean checkVisible(Point2D source, Point2D target) {
		Line2D visLine = new Line2D(source, target);
		
		for (int i = 0; i < borders.size(); i++) {
			Line2D compareLine = borders.get(i);
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
		double sourcex, sourcey, targetx, targety;
		System.out.println(source);
		sourcex = Double.parseDouble(source.substring(1, source.indexOf(',')));
		sourcey = Double.parseDouble(source.substring(source.indexOf(',') + 2, source.length()));
		targetx = Double.parseDouble(target.substring(1, target.indexOf(',')));
		targety = Double.parseDouble(target.substring(target.indexOf(',') + 2, target.length()));
		Line2D visLine = new Line2D(sourcex, sourcey, targetx, targety);
		
		for (int i = 0; i < borders.size(); i++) {
			Line2D compareLine = borders.get(i);
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
	
	private static void computeBorders() {
		double[] x;
		double[] y;
		x = new double[coordinates.size()];
		y = new double[coordinates.size()];
		for (int i = 0; i < coordinates.size(); i = i+1) {
			x[i] = Double.parseDouble(coordinates.get(i).substring(1, coordinates.get(i).indexOf(',')));
			y[i] = Double.parseDouble(coordinates.get(i).substring(coordinates.get(i).indexOf(',') + 2, coordinates.get(i).length()));
		}
		for(int i = 0; i < coordinates.size() - 1; i= i + 1) {
			borders.add(new Line2D(x[i], y[i], x[i+1], y[i+1]));
			nonVisBorders.add(new Line2D(x[i], y[i], x[i+1], y[i+1]));
		}
		borders.add(new Line2D(x[coordinates.size()-1], y[coordinates.size()-1], x[0], y[0]));
		nonVisBorders.add(new Line2D(x[coordinates.size()-1], y[coordinates.size()-1], x[0], y[0]));
		
		p = new SimplePolygon2D(x, y);
	}
	
	private static void readCoordinates() {
		String delimiter = "\\), ";
		try {
			Scanner read = new Scanner(new File("C:\\Users\\litao_000\\Desktop\\data.txt"));
			read.useDelimiter(delimiter);
			while (read.hasNext()) {
				String text = read.next();
				coordinates.add(text);
				notVisible.add(text);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
