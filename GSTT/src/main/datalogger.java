package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*Write to file (Data logger)*/


public class datalogger {
	
	public static int counter = 0; //set number before start
	
	public void writedata() throws IOException {
		
			PrintWriter writer;
		try {
			
			
			final Date currentTime = new Date();

			final SimpleDateFormat sdf = new SimpleDateFormat("dd-"); //end of stream #

			sdf.setTimeZone(TimeZone.getTimeZone("PT"));
			String currDate = sdf.format(currentTime);			
			
			
			if(counter < 50)//currDate.equals(main4.FileRead.parts[0]))
			{
			System.out.println(currDate + "Current count..." + counter);
			writer = new PrintWriter("Test Data logger.txt", "UTF-8");
			writer.println(sdf.format(currentTime)+ counter);
			counter ++;
			writer.close();
			}
			else 
			{		
				System.out.println("Quota reached, change APIKey");
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	}
