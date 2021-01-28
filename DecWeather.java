/**
 * CSIS 2450 -- Software Engineering I
 * Assignment: Java Review II
 * Russell Wadsworth
 * Review of key fundamental concepts in Java
 * Part 1: Read .csv Files
 * Part 2: Prepare Report File
 * Part 3: Write File
 */
package javaReview2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DecWeather {

	public static void main(String[] args) throws IOException {
		String filename = "SLCDecember2020Temperatures.csv";
		File main = new File("C:\\Users\\russe\\git\\CSIS2450\\2450_JavaReview\\src\\javaReview2\\" + filename);
		int[][] decTemps = new int[31][4];
		// Read data in
		decWeatherReader(main, decTemps);
		for (int i = 0; i < 31; i++) {
			decTemps[i][3] = decTemps[i][1] - decTemps[i][2];
		}
		
		// Calculates averages
		double hi = 0.0;
		hi = average(decTemps, 1);
		double lo = 0.0;
		lo = average(decTemps, 2);
		// Calculates high and low temperatures
		// 0: high temp, 1: day of high temp; 2: low temp; 3: day of low temp
		int[] extremes = new int[4];
		extremes = extremeTempFind(decTemps);
		
		// Create and Print file for report
		String finale = printReport(decTemps, hi, lo, extremes);
		System.out.println(finale);
		
		// Write text file for report
		// Location default is found in 2450_JavaReview folder, not src or javaReview2
		FileWriter fw = new FileWriter("TemperaturesReport.txt", true);
		fw.write(finale);
		System.out.println("\"TemperaturesReport.txt\" writing successful.");
		fw.close();
	}

	/**
	 * Creates the string that holds the entire report, with formatting built-in
	 * @param decTemps December temperatures
	 * @param hi average for high temps
	 * @param lo average for low temps
	 * @param extremes max high and low temps, plus days
	 * @return
	 */
	public static String printReport(int[][] decTemps, double hi, double lo, int[] extremes) {
		String av1 = String.format("%,.1f", hi);
		String av2 = String.format("%,.1f", lo);
		String finale = "--------------------------------------------------------------\n" + 
				"December 2020: Temperatures in Utah\n" + 
				"--------------------------------------------------------------\n" + 
				"Day  High  Low  Variance\n" + 
				"--------------------------------------------------------------\n";
		for(int i = 0; i < 31; i++) {
			finale = finale + String.format("%-5d%-6d%-5d%d\n", 
					decTemps[i][0], decTemps[i][1], decTemps[i][2], decTemps[i][3]);
		}
		finale = finale + "--------------------------------------------------------------\n" + 
				"December Highest Temperature: 12/" + extremes[1] + ": " + extremes[0] + " Average Hi: " + av1 + "\n" + 
				"December Lowest Temperature:  12/" + extremes[3] + ": " + extremes[2] + " Average Lo: " + av2 + "\n" + 
				"--------------------------------------------------------------\n" + 
				"--------------------------------------------------------------\n" + 
				"Graph\n" + 
				"--------------------------------------------------------------\n" + 
				"      1   5    10   15   20   25   30   35   40   45   50\n" + 
				"      |   |    |    |    |    |    |    |    |    |    |\n" + 
				"--------------------------------------------------------------\n";
		for (int i = 0; i < 31; i++) {
			finale = finale + String.format("%-2d Hi ", decTemps[i][0]);
			for (int j = 0; j < decTemps[i][1]; j++) {
				finale = finale + "+";
			}
			finale = finale + "\n   Lo ";
			for (int j = 0; j < decTemps[i][2]; j++) {
				finale = finale + "-";
			}
			finale = finale + "\n";
		}
		finale = finale + "--------------------------------------------------------------\n" + 
				"      |   |    |    |    |    |    |    |    |    |    |\n" + 
				"      1   5    10   15   20   25   30   35   40   45   50\n" + 
				"--------------------------------------------------------------";
		return finale;
	}

	/**
	 * Finds average with all temperatures
	 * @param decTemps December temperatures
	 * @param x number within array that determines high or low; 1--high, 2--low
	 * @return
	 */
	public static double average(int[][] decTemps, int x) {
		double num = 0.0;
		for (int i = 0; i < 31; i++) {
			num = num + decTemps[i][x];
		}
		num = num / 31;
		return num;
	}

	/**
	 * Searches and assigns the highest and lowest temperatures/the days it happens.
	 * Also averages high and low temps.
	 * @param decTemps December temperatures
	 */
	public static int[] extremeTempFind(int[][] decTemps) {
		int[] rtn = new int[] { 0, 0, 100, 0 };
		for (int i = 0; i < 31; i++) {
			// Finds high
			int current1 = decTemps[i][1];
			// Finds low
			int current2 = decTemps[i][2];
			// High
			if (rtn[0] < current1) {
				rtn[0] = current1;
				rtn[1] = decTemps[i][0];
			}
			// Low
			if (rtn[2] > current2) {
				rtn[2] = current2;
				rtn[3] = decTemps[i][0];
			}
		}
		return rtn;
	}

	/**
	 * Fills the decTemps 2D array with the information from the csv file
	 * @param main the csv file
	 * @param decTemps array for the December temperatures
	 * @throws IOException
	 */
	public static void decWeatherReader(File main, int[][] decTemps) throws IOException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(main));
			String line = "";
			int count = 0;
			String csv = ",";
			while ((line = br.readLine()) != null) {
				String[] buff = line.split(csv);
				for (int i = 0; i < buff.length; i++) {
					decTemps[count][i] = Integer.parseInt(buff[i]);
				}
				count++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found. Error.");
		}
	}

}
