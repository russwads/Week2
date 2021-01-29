/**
 * CSIS 2450 -- Software Engineering I
 * Assignment: Java Review III
 * Russell Wadsworth
 * Review of key fundamental concepts in Java
 * Part 1: Read from a DB
 * Part 2: Prepare Report File
 * Part 3: Grab user input to create either a November or December report
 */
package javaReview3;

import java.io.FileWriter;
import java.sql.*;
import java.util.Scanner;

public class PracticeDBDemo {

	public static void main(String[] args) {
		Scanner inPut = new Scanner(System.in);
        String connectionString = "jdbc:mysql://127.0.0.1:3306/practice";
        String dbLogin = "root";
        String dbPassword = "DBAdmin";
        Connection conn = null;
        int numRows;
        int numCols = 5;
        int[][] dbResults;
        
        String sql = "SELECT month, day, year, hi, lo FROM temperatures";

        try {
            conn = DriverManager.getConnection(connectionString, dbLogin, dbPassword);
            if (conn != null) {
                try (Statement stmt = conn.createStatement(
                         ResultSet.TYPE_SCROLL_INSENSITIVE, 
                         ResultSet.CONCUR_UPDATABLE);
                     ResultSet rs = stmt.executeQuery(sql)) {
                	// Get full SQL data for number of rows
                    rs.last();
                    numRows = rs.getRow();
                    dbResults = new int[numRows][numCols];
                    rs.first();
                    // Grabbing actual data from DB
                    for (int i = 0; i < numRows; i++) {
                        dbResults[i][0] = rs.getInt("month");
                        dbResults[i][1] = rs.getInt("day");
                        dbResults[i][2] = rs.getInt("year");
                        dbResults[i][3] = rs.getInt("hi");
                        dbResults[i][4] = rs.getInt("lo");
                        rs.next();
                    }
                    
                    // User Input
                    String m = "";
                    int start = 0;
                    int days = 0;
                    System.out.println("Which month to create report with?");
                    System.out.println("1: November, 2: December");
                    int month = 0;
                    month = inPut.nextInt();
                    // Evaluate numbers for months
                    if (month == 1 ^ month == 2) {
                    	if (month == 1) {
                    		m = "November"; 
                    		start = 0; 
                    		days = 30; 
                    	}
                    	else { 
                    		m = "December"; 
                    		start = 30; 
                    		days = 31; 
                    	}
                    }
                    else { throw new IllegalArgumentException("Month must be either 1 or 2 for Nov or Dec."); }
                    
                    // Averages
                    double hi = 0.0;
                    hi = average(dbResults, 3, month);
                    double lo = 0.0;
                    lo = average(dbResults, 4, month);
                    // Calculates high and low temperatures
            		// 0: high temp, 1: day of high temp; 2: low temp; 3: day of low temp
            		int[] extremes = new int[4];
            		extremes = extremeTempFind(dbResults, start, days);
                    
                    String finish = printReport(dbResults, hi, lo, start, days, m, extremes);
                    System.out.println(finish);
                    
                    // Write text file for report
            		// Location default is found in 2450_JavaReview folder, not src or javaReview2
            		FileWriter fw = new FileWriter("TemperaturesReportFromDB.txt", false);
            		fw.write(finish);
            		System.out.println("\"TemperaturesReportFromDB.txt\" writing successful.");
            		fw.close();
                } 
                catch (SQLException ex) {
                	System.err.println("Error SQL");
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
                catch (IllegalArgumentException ex) {
                	System.err.println("Error Arg");
                	System.out.println(ex.getMessage());
                	ex.printStackTrace();
                }
            }
        } 
        catch (Exception e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
	}
	
	/**
	 * 
	 * @param decTemps
	 * @param hi
	 * @param lo
	 * @param start
	 * @param days
	 * @param month
	 * @param extremes
	 * @return
	 */
	public static String printReport(int[][] decTemps, double hi, double lo, int start, int days, String month, int[] extremes) {
		String av1 = String.format("%,.1f", hi);
		String av2 = String.format("%,.1f", lo);
		String finale = printLine(70) + 
				month + " 2020: Temperatures in Utah\n" + 
				printLine(70) + 
				"Day           High  Low  Variance\n" + 
				printLine(70);
		for(int i = start; i < (start + days); i++) {
			int variance = decTemps[i][3] - decTemps[i][4];
			finale = finale + String.format("%d/%2d/%-8d%-6d%-5d%d\n", 
					decTemps[i][0], decTemps[i][1], decTemps[i][2], decTemps[i][3],
					decTemps[i][4], variance);
		}
		
		finale = finale + printLine(70) + 
				month + " Highest Temperature: " + decTemps[start][0] + "/" + String.format("%2d", extremes[1]) + ": " + extremes[0] + " Average Hi: " + av1 + "\n" + 
				month + " Lowest Temperature:  " + decTemps[start][0] + "/" + String.format("%2d", extremes[3]) + ": " + extremes[2] + " Average Lo: " + av2 + "\n" + 
				printLine(70) + printLine(70) + 
				"Graph\n" + 
				printLine(70) +  
				"      1   5    10   15   20   25   30   35   40   45   50   55   60\n" + 
				"      |   |    |    |    |    |    |    |    |    |    |    |    |\n" + 
				printLine(70) + "\n";
		for (int i = start; i < (start + days); i++) {
			finale = finale + String.format("%-2d Hi ", decTemps[i][1]);
			for (int j = 0; j < decTemps[i][3]; j++) {
				finale = finale + "+";
			}
			finale = finale + "\n   Lo ";
			for (int j = 0; j < decTemps[i][4]; j++) {
				finale = finale + "-";
			}
			finale = finale + "\n";
		}
		finale = finale + printLine(70) + 
				"      |   |    |    |    |    |    |    |    |    |    |    |    |\n" + 
				"      1   5    10   15   20   25   30   35   40   45   50   55   60\n" + 
				printLine(70);
		return finale;
	}
	
	/**
	 * Searches and assigns the highest and lowest temperatures/the days it happens.
	 * Also averages high and low temps.
	 * @param decTemps December temperatures
	 * @param x offset for month
	 * @param y number of days in month
	 * @return
	 */
	public static int[] extremeTempFind(int[][] decTemps, int x, int y) {
		int[] rtn = new int[] { 0, 0, 100, 0 };
		for (int i = x; i < (x + y); i++) {
			// Finds high
			int current1 = decTemps[i][3];
			// Finds low
			int current2 = decTemps[i][4];
			// High
			if (rtn[0] < current1) {
				rtn[0] = current1;
				rtn[1] = decTemps[i][1];
			}
			// Low
			if (rtn[2] > current2) {
				rtn[2] = current2;
				rtn[3] = decTemps[i][1];
			}
		}
		return rtn;
	}

	/**
	 * Finds average with all temperatures
	 * @param decTemps December temperatures
	 * @param x number within array that determines high or low; 3--high, 4--low
	 * @param y number within array that determines month of temps; 1--Nov, 2--Dec
	 * @return
	 */
	public static double average(int[][] decTemps, int x, int y) {
		int z = 0;
		if (y == 1) { y = 0; z = 30; } // November
		else if (y == 2) { y = 30; z = 31;} // December
		else return -1;
		
		double num = 0.0;
		for (int i = y; i < (y + z); i++) {
			num = num + decTemps[i][x];
		}
		num = num / z;
		return num;
	}
	
	/**
	 * Returns a line that contains the number of specified dashes
	 * @param dashes number of dashes to create
	 * @return
	 */
	private static String printLine(int dashes) {
		String rtn = "";
		for (int i = 1; i <= dashes; i++){
			rtn = rtn + "-";
		}
		rtn = rtn + "\n";
		return rtn;
	}
}
