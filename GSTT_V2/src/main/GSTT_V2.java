package main;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.darkprograms.speech.microphone.Microphone;

import javaFlacEncoder.FLACFileWriter;

/*GSTT with UDP Receiver and Sender #STT(String)*/

public class GSTT_V2 {

	static String reply = "";
	static String APIKEY = "";
	static List<String> logOtherResponse;
	static int stage = 0;
	static String filter = "";
	static String toBeSent = "";
	static String beforeFirstComma = "";

	private UDPConnection udpCom;

	private Datalogger log;
	private static logSTT logData;

	private InetAddress myIP;
	private int myPort;
	private InetAddress targetIP;
	private int targetPort;
	
	public GSTT_V2() {
		udpCom = new UDPConnection();
		log = new Datalogger();
		logData = new logSTT();
	}
	
	public static void main(String[] args) {
		
		if (args.length == 4) {
			GSTT_V2 gstt = new GSTT_V2();

			String message;

			try {
				gstt.myIP = InetAddress.getByName(args[0]);
				gstt.myPort = Integer.parseInt(args[1]);
				gstt.targetIP = InetAddress.getByName(args[2]);
				gstt.targetPort = Integer.parseInt(args[3]);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		
			while (true) {
				switch (stage) {
				case 0: {
					System.out.println("GSTT");

					do {
						gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false);
						message = gstt.udpCom.getMessage();
						System.out.println(message);
					} while (!("#STT#1#".equals(message)));

					if ("#STT#1#".equals(message)) {
						
						System.out.println("started");
						
						if (Datalogger.counter < 50)
							APIKEY = "AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY";																
						else if ((Datalogger.counter >= 50) && (Datalogger.counter < 100))
							APIKEY = "AIzaSyB6yR8DR6onz9YEBKkHmrLAOQZth5Vv2gs";
						else if ((Datalogger.counter >= 100) && (Datalogger.counter < 150))
							APIKEY = "AIzaSyCFhY2ogNV4iFX3Hg3EgGU5y9wGodmfLR8";
						else
							APIKEY = "AIzaSyDe2nR4mdQYL74iwkZx5pOBM_3MVHNZS8c";
					}	
					
					Microphone mic = new Microphone(FLACFileWriter.FLAC);
					File file = new File("CRAudioTest.flac");
					
					while (true) {
						try {
							System.out.println("Recording...");
							mic.captureAudioToFile(file); // starts recording
//															
//							 Thread.sleep(10000);// Records for 10s
//							 Path path = Paths.get("good-morning-google.flac");
//							 byte[] data = Files.readAllBytes(path);
							
							
							do {
								gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false);
								message = gstt.udpCom.getMessage();
								System.out.println(message);
							} while (!"#STT#0#".equals(message));

							mic.close();

							byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());
//					
//							mic.getAudioFile().delete();

							String request = "https://www.google.com/speech-api/v2/recognize?client=chromium&lang=en-us&key=" + APIKEY;

							URL url = new URL(request);
							HttpURLConnection connection = (HttpURLConnection) url.openConnection();
							connection.setDoOutput(true);
							connection.setDoInput(true);
							connection.setInstanceFollowRedirects(false);
							connection.setRequestMethod("POST");
							connection.setRequestProperty("Content-Type", "audio/x-flac; rate=8000"); //8000 (from Mic), 44100, 16000
							connection.setRequestProperty("User-Agent", "speech2text");
							connection.setConnectTimeout(60000);
							connection.setUseCaches(false);

							DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
							wr.write(data);
							wr.flush();
							wr.close();
							connection.disconnect();

							System.out.println("Retrieving results...");

							BufferedReader in = new BufferedReader(
									new InputStreamReader(connection.getInputStream()));
							
							String decodedString;
							List<String> ls = new ArrayList<String>();

							while ((decodedString = in.readLine()) != null) {
								ls.add(decodedString);
								System.out.println(decodedString);
							}

							try {
								logData.writeData(ls);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							reply = logData.getFirstResponse();
							
							if (reply == null) {
								System.out.println("I can't hear what you said. Please repeat.\n");
								toBeSent = "";
							} else {
								beforeFirstComma = reply.split("\"")[0];
								toBeSent = beforeFirstComma;
							}

							if (toBeSent != null) {
								toBeSent = "#STT#" + toBeSent + "#";
								System.out.println("You said: " + toBeSent + "\n");
								gstt.udpCom.sendSocket(toBeSent, gstt.targetIP, gstt.targetPort);
								System.out.println("String sent.");
								if (!toBeSent.equals("#STT##")) {
									try {
										gstt.log.writeData();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}		
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}
					}
				}
			}
		}
		}
	}


