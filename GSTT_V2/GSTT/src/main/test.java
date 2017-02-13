package main;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import org.testng.annotations.Test;

import com.darkprograms.speech.microphone.Microphone;

import javaFlacEncoder.FLACFileWriter;


public class test {

//	@Test
	public void f() {

	  try{
//	  Path path = Paths.get("CRAudioTest.flac");
//	  byte[] data = Files.readAllBytes(path);
	  Microphone mic = new Microphone(FLACFileWriter.FLAC);// Instantiate microphone & record FLAC file.

		File file = new File("CRAudioTest.flac");// The File to record buffer
		
					System.out.println("Recording...");
					mic.captureAudioToFile(file); // starts recording
					Thread.sleep(10000);// Records for 10s

					mic.close();// Stops recording
					
					byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());// Saves data into memory
//					
//					//mic.getAudioFile().delete();// Deletes buffer file
	  String request = "https://www.google.com/speech-api/v2/recognize?client=chromium&lang=en-us&key=AIzaSyCFhY2ogNV4iFX3Hg3EgGU5y9wGodmfLR8";
//	  "https://www.google.com/"+
//	       "speech-api/v2/recognize?"+
//	       "xjerr=1&client=speech2text&lang=en-US&maxresults=10"+
//	       "output=json&key=AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY";

	  URL url = new URL(request);
	  HttpURLConnection connection = (HttpURLConnection) url.openConnection();          
	  connection.setDoOutput(true);
	  connection.setDoInput(true);
	  connection.setInstanceFollowRedirects(false);
	  connection.setRequestMethod("POST");
	  connection.setRequestProperty("Content-Type", "audio/x-flac; rate=8000");
	  connection.setRequestProperty("User-Agent", "speech2text");
	  connection.setConnectTimeout(60000);
	  connection.setUseCaches (false);

	  DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
	  wr.write(data);
	  wr.flush();
	  wr.close();
	  connection.disconnect();

	  System.out.println("Done");

	  BufferedReader in = new BufferedReader(
	      new InputStreamReader(
	      connection.getInputStream()));
	       String decodedString;
	       while ((decodedString = in.readLine()) != null) {
	       System.out.println(decodedString);
	       }

	  }
	  catch(Exception e){
	  e.printStackTrace();
	  }

	  }
	}

