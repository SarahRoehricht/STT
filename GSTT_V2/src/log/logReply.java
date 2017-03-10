package log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.darkprograms.speech.util.StringUtil;

public class LogReply {

	public static String data; //data from file
	private File logResponse;

	public LogReply() {
		
		if(logResponse == null)
		logResponse = new File("logReply.log");

//		try {
//			data = readData();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		System.out.println(data);
	}

	public String readData() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(logResponse));
		String line = br.readLine();

		return null;
	}

	public void writeData(String response) throws IOException {
		
		PrintWriter writer;
		List<String> parsed;
		
		try {
			final Date currentTime = new Date();

			final SimpleDateFormat sdf = new SimpleDateFormat("MMdd_hh:mm:ss.SSS"); 
																							
			sdf.setTimeZone(TimeZone.getTimeZone("PT"));
			String currDate = sdf.format(currentTime);

//			System.out.println("Logging response... \n");
			writer = new PrintWriter(new FileWriter(logResponse, true));
	
			writer.println("ResponseID " + sdf.format(currentTime) + ": " + response);

			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
