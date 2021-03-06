package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/*Write to file (Data logger)*/

public class Datalogger
{
	public static int counter; // set number before start

	private File countLogger;

	Datalogger()
	{
		countLogger = new File("counterLog.log");
		try
		{
			counter = readCounter();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Counter: " + counter);
	}

	public static boolean isSameDay(Date date1, Date date2) {

	    // Strip out the time part of each date.
	    long julianDayNumber1 = date1.getTime() / TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
	    long julianDayNumber2 = date2.getTime() / TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

	    System.out.println(date1 + " " + date2);
	    System.out.println(julianDayNumber1 + " " + julianDayNumber2);
	    // If they now are equal then it is the same day.
	    return julianDayNumber1 == julianDayNumber2;
	}
	
	public int readCounter() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(countLogger));
		String line = br.readLine();
		String[] parts = line.split("-");
		
		Date currentTime = new Date();
		currentTime.setHours(0);
		currentTime.setMinutes(0);
		
		DateFormat currDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		Date readDate=null;
		try
		{
			readDate = currDateFormat.parse(parts[0]);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		if(isSameDay(currentTime, readDate))
			return Integer.parseInt(parts[1]);
		else
			return 0;
	}

	public void writeData() throws IOException
	{
		PrintWriter writer;
		try
		{
			final Date currentTime = new Date();

			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd"); // end of stream #

			sdf.setTimeZone(TimeZone.getTimeZone("PT"));
			String currDate = sdf.format(currentTime);

			if (counter < 50)// currDate.equals(main4.FileRead.parts[0]))
			{
				System.out.println(currDate + "Current count..." + counter);
				writer = new PrintWriter(countLogger, "UTF-8");
				writer.println(sdf.format(currentTime)+ "-" + counter);
				counter++;
				writer.close();
			} else
			{
				System.out.println("Quota reached, change APIKey");
			}

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
