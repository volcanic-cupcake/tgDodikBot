package tgAyeBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class TextFile {
	public static void write(File file, String text, boolean append) throws IOException {
		
		FileWriter writer;
		if (append)	writer = new FileWriter(file, true);
		else writer = new FileWriter(file);
		
		writer.write(text);
		writer.close();
	}
	public static void write(String filePath, String text, boolean append) throws IOException {
		File file = new File(filePath);
		
		FileWriter writer;
		if (append)	writer = new FileWriter(file, true);
		else writer = new FileWriter(file);
		
		writer.write(text);
		writer.close();
	}
	
	public static void writeLines(File file, List<String> lines, boolean append) throws IOException {
		String text = "";
		for (String line : lines) {
			text += line + "\n";
		}
		try {
			//removing last character
			text = text.substring(0, text.length() - 1);
		}
		catch (StringIndexOutOfBoundsException e) {}
		
		if (append) {
			List<String> existingLines = readLines(file);
			String existingText = "";
			for (String existingLine : existingLines) {
				existingText += existingLine + "\n";
			}
			
			try {
				//removing last character
				existingText = existingText.substring(0, existingText.length() - 1);
				
				text = existingText + "\n" + text;
			}
			catch (StringIndexOutOfBoundsException e) {}
		}
		
		FileWriter writer = new FileWriter(file);
		writer.write(text);
		writer.close();
	}
	public static void writeLines(String filePath, List<String> lines, boolean append) throws IOException {
		File file = new File(filePath);
		
		String text = "";
		for (String line : lines) {
			text += line + "\n";
		}
		try {
			//removing last character
			text = text.substring(0, text.length() - 1);
		}
		catch (StringIndexOutOfBoundsException e) {}
		
		if (append) {
			List<String> existingLines = readLines(file);
			String existingText = "";
			for (String existingLine : existingLines) {
				existingText += existingLine + "\n";
			}
			
			try {
				//removing last character
				existingText = existingText.substring(0, existingText.length() - 1);
				
				text = existingText + "\n" + text;
			}
			catch (StringIndexOutOfBoundsException e) {}
		}
		
		FileWriter writer = new FileWriter(file);
		writer.write(text);
		writer.close();
	}
	public static String read(File file) throws FileNotFoundException {
		String text = "";
		Scanner scan = new Scanner(file);
		String nextLine;
		while (scan.hasNextLine()) {
			nextLine = scan.nextLine();
			text += nextLine + "\n";
		}
		scan.close();
		try {
			//removing last character
			text = text.substring(0, text.length() - 1);
		}
		catch (StringIndexOutOfBoundsException e) {}
		return text;
	}
	public static String read(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		
		String text = "";
		Scanner scan = new Scanner(file);
		String nextLine;
		while (scan.hasNextLine()) {
			nextLine = scan.nextLine();
			text += nextLine + "\n";
		}
		scan.close();
		try {
			//removing last character
			text = text.substring(0, text.length() - 1);
		}
		catch (StringIndexOutOfBoundsException e) {}
		return text;
	}
	public static List<String> readLines(File file) throws FileNotFoundException {
		List<String> lines = new ArrayList<String>();
		Scanner scan = new Scanner(file);
		String nextLine;
		while (scan.hasNextLine()) {
			nextLine = scan.nextLine();
			lines.add(nextLine);
		}
		scan.close();
		return lines;
	}
	public static List<String> readLines(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		
		List<String> lines = new ArrayList<String>();
		Scanner scan = new Scanner(file);
		String nextLine;
		while (scan.hasNextLine()) {
			nextLine = scan.nextLine();
			lines.add(nextLine);
		}
		scan.close();
		return lines;
	}
}
