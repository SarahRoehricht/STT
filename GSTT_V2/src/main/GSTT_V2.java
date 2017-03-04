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
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import com.darkprograms.speech.microphone.Microphone;
import javaFlacEncoder.FLACFileWriter;

import log.Datalogger;
import log.logSTT;
import log.logReply;
import tcp.TCPServer;

import decision.Decision;
import edu.stanford.nlp.ling.TaggedWord;
import parser.Parser;

/*GSTT with TCP and Sender #STT#(String)#*/

public class GSTT_V2
{
	static String APIKEY = "";
	static int stage = 0;
	
	static String transcript = "";
	static List<String> logOtherResponse;
	static String filter = "";
	static String reply = "";

	private static TCPServer tcpServer;
	private static Datalogger logCalls; 	//number of GSTT API requests
	private static logSTT logData;	//log transcript 
	private static logReply logReplies;	//log replies 
	private static Parser p;
	private static Decision d;
	private static ArrayList<TaggedWord> parsedString;

	private InetAddress myIP;
	private int myPort;
	private InetAddress targetIP;
	private int targetPort;

	public GSTT_V2()
	{
		logCalls = new Datalogger();
		logData = new logSTT();
		logReplies = new logReply();
		p = new Parser();
		d = new Decision();
		parsedString = new ArrayList<TaggedWord>();
	}

	public static void main(String[] args)
	{

		if (args.length == 4)
		{
			GSTT_V2 gstt = new GSTT_V2();
			String message;

			try
			{
				gstt.myIP = InetAddress.getByName(args[0]);
				gstt.myPort = Integer.parseInt(args[1]);
				gstt.targetIP = InetAddress.getByName(args[2]);
				gstt.targetPort = Integer.parseInt(args[3]);
			} catch (NumberFormatException e1)
			{
				e1.printStackTrace();
			} catch (UnknownHostException e1)
			{
				e1.printStackTrace();
			}

			while (true)
			{
				switch (stage)
				{
				case 0:
				{
					System.out.println("GSTT");

					// establish new Connection for receiving txt
					tcpServer = new TCPServer(gstt.myIP, gstt.myPort, 5, true);
					do
					{
						message = gstt.tcpServer.receive();
						System.out.println(message);
					} while (!("#STT#1#".equals(message)));
					// end Connection
					gstt.tcpServer.endConnection();

					if ("#STT#1#".equals(message))
					{

						System.out.println("started");

						if (gstt.logCalls.counter < 50)
							APIKEY = "AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY";
						else if ((gstt.logCalls.counter >= 50) && (gstt.logCalls.counter < 100))
							APIKEY = "AIzaSyB6yR8DR6onz9YEBKkHmrLAOQZth5Vv2gs";
						else if ((gstt.logCalls.counter >= 100) && (gstt.logCalls.counter < 150))
							APIKEY = "AIzaSyCFhY2ogNV4iFX3Hg3EgGU5y9wGodmfLR8";
						else
							APIKEY = "AIzaSyDe2nR4mdQYL74iwkZx5pOBM_3MVHNZS8c";
					}

					Microphone mic = new Microphone(FLACFileWriter.FLAC);
					File file = new File("CRAudioTest.flac");

					while (true)
					{
						try
						{
							System.out.println("Recording...");
							mic.captureAudioToFile(file); // starts recording

							Thread.sleep(1000);// Records for 10s
							// Path path = Paths.get("good-morning-google.flac"); //get audio file directly
							// byte[] data = Files.readAllBytes(path);

							// establish new Connection for receiving txt
							tcpServer = new TCPServer(gstt.myIP, gstt.myPort, 5, true);
							do
							{
								message = gstt.tcpServer.receive();
							} while (!"#STT#0#".equals(message));
							// end Connection
							gstt.tcpServer.endConnection();

							mic.close();
							System.out.println("Closed mic...");

							byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());
				
//							mic.getAudioFile().delete();

							String request = "https://www.google.com/speech-api/v2/recognize?client=chromium&lang=en-us&key=" + APIKEY;

							URL url = new URL(request);
							HttpURLConnection connection = (HttpURLConnection) url.openConnection();
							connection.setDoOutput(true);
							connection.setDoInput(true);
							connection.setInstanceFollowRedirects(false);
							connection.setRequestMethod("POST");
							connection.setRequestProperty("Content-Type", "audio/x-flac; rate=8000"); // 8000 (from Mic), 44100, 16000
							connection.setRequestProperty("User-Agent", "speech2text");
							connection.setConnectTimeout(60000);
							connection.setUseCaches(false);

							DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
							wr.write(data);
							wr.flush();
							wr.close();
							connection.disconnect();

							System.out.println("Retrieving results...");
							
							try
							{
								gstt.logCalls.writeData();
							} catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

							String decodedString;
							List<String> ls = new ArrayList<String>();

							while ((decodedString = in.readLine()) != null)
							{
								ls.add(decodedString);
								System.out.println(decodedString);
							}

							try
							{
								logData.writeData(ls);
							} catch (IOException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							//Get first response, parse and answer
							if(logData.getFirstResponse()!= null){
								transcript = logData.getFirstResponse();
								d.setOriginalTranscript(transcript);
								parsedString = p.parse(transcript);
								d.decide(parsedString);
								reply = d.getToTTS();
								logReplies.writeData(reply);
							}

							if (reply == null)
							{
								System.out.println("I can't hear what you said. Please repeat.\n");

								// establish new Connection for sending txt
								tcpServer = new TCPServer(gstt.myIP, gstt.myPort, 5, true);
								gstt.tcpServer.send("#STT##");
								// end Connection
								gstt.tcpServer.endConnection();

							} else {
								reply = "#STT#" + reply + "#";
								System.out.println("Reply sent to TTS: " + reply + "\n");

								// establish new Connection for sending txt
								tcpServer = new TCPServer(gstt.myIP, gstt.myPort, 5, true);
								gstt.tcpServer.send(reply);
								// end Connection
								gstt.tcpServer.endConnection();

								System.out.println("String sent.");
									
							}
							
						} catch (Exception e)
						{
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
