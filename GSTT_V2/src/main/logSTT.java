package main;

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

public class logSTT {

	public static String data; //data from file
	private String firstResponse;
	private String secondResponse;
	private File logSTT;

	logSTT() {
		
		if(logSTT == null)
		logSTT = new File("logSTT.log");

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
		BufferedReader br = new BufferedReader(new FileReader(logSTT));
		String line = br.readLine();

		return null;
	}

	public void writeData(List<String> otherResponses) throws IOException {
		
		PrintWriter writer;
		List<String> parsed;
		
		try {
			final Date currentTime = new Date();

			final SimpleDateFormat sdf = new SimpleDateFormat("MMdd_hh:mm:ss.SSS"); 
																							
			sdf.setTimeZone(TimeZone.getTimeZone("PT"));
			String currDate = sdf.format(currentTime);

			System.out.println(currDate + "\nLogging data... \n");
			writer = new PrintWriter(new FileWriter(logSTT, true));

			parsed = parseData(otherResponses);
				for (String item : parsed) {
					writer.println("ResponseID " + sdf.format(currentTime) + ": " + item);
			}	
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> parseData(List<String> text) {
		
		List<String> ls = new ArrayList<String>();
		
			for (String item : text.subList( 1, text.size() )) { 
				
				System.out.println("Parsing... \n");

				String txt = StringUtil.substringBetween(item, "{\"result\":[{\"alternative\":[{\"transcript\":\"", "\""); 
				
				int pos = StringUtils.ordinalIndexOf(item, ",", 2);
				String spiltItem = item.substring(pos);
				
				String txt2 = StringUtil.substringBetween(spiltItem, ",{\"transcript\":\"", "\"");  
				
				System.out.println("FirstResponse: " + txt);
				System.out.println("SecondResponse: " + txt2);
				
				setFirstResponse(txt);
				ls.add(txt);
				setSecondResponse(txt2);
				ls.add(txt2);
				
				//System.out.println(ls.toString());
			}

	return ls;
	}

	public String getFirstResponse() {
		return firstResponse;
	}

	public void setFirstResponse(String firstResponse) {
		this.firstResponse = firstResponse;
	}

	public String getSecondResponse() {
		return secondResponse;
	}

	public void setSecondResponse(String secondResponse) {
		this.secondResponse = secondResponse;
	}

}
