package log;

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
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/*Write to file (Data logger)*/

public class LogCount
{
	public static int counter; // set number before start

	private File countLogger;

	public LogCount()
	{
		countLogger = new File("logCount.log"); 
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

	    // If they now are equal then it is the same day.
	    return date1 == date2;
	}
	
	public int readCounter() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(countLogger));
		String line = br.readLine();
		String[] parts = line.split("-");
		
		final Date currentD = new Date();

		final SimpleDateFormat sdf = new SimpleDateFormat("MMdd"); // end of stream #

		sdf.setTimeZone(TimeZone.getTimeZone("PT"));
		String currDate = sdf.format(currentD);
		
		if(currDate.equals(parts[0])){
			return Integer.parseInt(parts[1]);
			}
		else
			return 0;
	}

	public void writeData() throws IOException
	{
		PrintWriter writer;
		try
		{
			final Date currentTime = new Date();

			final SimpleDateFormat sdf = new SimpleDateFormat("MMdd"); // end of stream #

			sdf.setTimeZone(TimeZone.getTimeZone("PT"));
			String currDate = sdf.format(currentTime);

				counter++;
				System.out.println("Current count..." + counter);
				writer = new PrintWriter(countLogger, "UTF-8");
				writer.println(sdf.format(currentTime) + "-" + counter);
				writer.close();

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
