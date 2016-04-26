package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class readFile 
{
	public static String[] parts;
	public static String part1; 
	public static String part2; 
	//public static int cnt;
	
   public void reading()
	{
      try{
		// Open the file that is the first 
		// command line parameter
		FileInputStream fstream = new FileInputStream("Test Data logger.txt");
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		//Read File Line By Line
		while ((strLine = br.readLine()) != null) 	{
			// Print the content on the console
			System.out.println (strLine);
			parts = strLine.split("-");
			part1 = parts[0]; 
			part2 = parts[1]; 
			datalogger.counter = Integer.parseInt(part2);
			//System.out.println(cnt);
			//System.out.println("split..." + part2);
		}
		//Close the input stream
		in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
   
}