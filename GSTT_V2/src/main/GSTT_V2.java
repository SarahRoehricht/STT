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
import java.text.SimpleDateFormat;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.darkprograms.speech.microphone.Microphone;
import javaFlacEncoder.FLACFileWriter;

import log.LogCount;
import log.LogSTT;
import log.LogReply;
import tcp.TCPServer;

import decision.Decision;
import edu.stanford.nlp.ling.TaggedWord;
import parser.Parser;

/*GSTT with TCP and Sender #STT#(String)#*/

public class GSTT_V2 {
	static String APIKEY = "";
	static int stage = 0;
	static int scenario = 0;

	static String transcript = "";
	static List<String> logOtherResponse;
	static String filter = "";
	static String reply = "";

	private static TCPServer tcpServer;
	// private UDPConnection udpCom;

	private static LogCount logCalls; // number of GSTT API requests
	private static LogSTT logData; // log transcript
	private static LogReply logReplies; // log replies
	private static Parser p;
	private static Decision d;
	private static ArrayList<TaggedWord> parsedString;
	private static ArrayList<TaggedWord> parsedString2;

	private InetAddress myIP;
	private int myPort;
	private InetAddress targetIP;
	private int targetPort;

	public GSTT_V2() {
		// udpCom = new UDPConnection();
		logCalls = new LogCount();
		logData = new LogSTT();
		logReplies = new LogReply();
		p = new Parser();
		d = new Decision();
		parsedString = new ArrayList<TaggedWord>();
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

					// establish new Connection for receiving txt
					tcpServer = new TCPServer(gstt.myIP, gstt.myPort, 5, true);
					do {
						message = tcpServer.receive();
						System.out.println(message);
					} while (!("#STT#1#".equals(message) | "#STT#name#".equals(message)
							| "#STT#yesno#".equals(message)));
					// end Connection
					tcpServer.endConnection();

					// do
					// {
					// gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false);
					// message = gstt.udpCom.getMessage();
					// System.out.println(message);
					// } while
					// (!("#STT#1#".equals(message)|"#STT#name#".equals(message)|
					// "#STT#yesno#".equals(message)));

					// scenario 0 and 1: with parsing, scenario 2: answer
					// directly
					if ("#STT#1#".equals(message)) {
						scenario = 0;
					} else if ("#STT#name#".equals(message)) {
						scenario = 1;
					} else if ("#STT#yesno#".equals(message)) {
						scenario = 2;
					}

					System.out.println("started");

					if (gstt.logCalls.counter < 50)
						APIKEY = "AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY";
					else if ((gstt.logCalls.counter >= 50) && (gstt.logCalls.counter < 100))
						APIKEY = "AIzaSyB6yR8DR6onz9YEBKkHmrLAOQZth5Vv2gs";
					else if ((gstt.logCalls.counter >= 100) && (gstt.logCalls.counter < 150))
						APIKEY = "AIzaSyCFhY2ogNV4iFX3Hg3EgGU5y9wGodmfLR8";
					else
						APIKEY = "AIzaSyDe2nR4mdQYL74iwkZx5pOBM_3MVHNZS8c";

					Microphone mic = new Microphone(FLACFileWriter.FLAC);
					String fileName = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
					File file = new File("CRAudio" + fileName + ".flac"); // direct
																			// path
																			// to
																			// audio
																			// data
																			// folder

					while (true) {
						try {
							System.out.println("Recording...");
							mic.captureAudioToFile(file); // starts recording

							Thread.sleep(1000);// Records for 10s
							// Path path =
							// Paths.get("good-morning-google.flac"); //get
							// audio file directly
							// byte[] data = Files.readAllBytes(path);

							// do
							// {
							// gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort,
							// false);
							// message = gstt.udpCom.getMessage();
							// System.out.println(message);
							// } while (!"#STT#0#".equals(message));

							// establish new Connection for receiving txt
							tcpServer = new TCPServer(gstt.myIP, gstt.myPort, 5, true);
							do {
								message = tcpServer.receive();
							} while (!"#STT#0#".equals(message));
							// end Connection
							// tcpServer.endConnection();

							mic.close();
							System.out.println("Closed mic...");

							byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());

							// mic.getAudioFile().delete();

							String request = "https://www.google.com/speech-api/v2/recognize?client=chromium&lang=en-us&key="
									+ APIKEY;

							URL url = new URL(request);
							HttpURLConnection connection = (HttpURLConnection) url.openConnection();
							connection.setDoOutput(true);
							connection.setDoInput(true);
							connection.setInstanceFollowRedirects(false);
							connection.setRequestMethod("POST");
							connection.setRequestProperty("Content-Type", "audio/x-flac; rate=8000"); // 8000
																										// (from
																										// Mic),
																										// 44100,
																										// 16000
							connection.setRequestProperty("User-Agent", "speech2text");
							connection.setConnectTimeout(60000);
							connection.setUseCaches(false);

							DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
							wr.write(data);
							wr.flush();
							wr.close();
							connection.disconnect();

							System.out.println("Retrieving results...");

							try {
								gstt.logCalls.writeData();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

							String decodedString;
							List<String> ls = new ArrayList<String>();

							while ((decodedString = in.readLine()) != null) {
								ls.add(decodedString);
								System.out.println(decodedString);
							}

							try {
								logData.readData(); // get previous question
													// before being overwritten
								logData.writeData(ls);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							// case 0,1: Get first response, parse and answer
							switch (scenario) {
							case 0: // default
							case 1: // name
							{
								if (logData.getFirstResponse() != null) {
									transcript = logData.getFirstResponse();
									if (transcript.contains("previous question") | transcript.contains("last question")
											| transcript.contains("previous questions")
											| transcript.contains("last questions")) {
										reply = "The previous question is, " + logData.getPreviousResponse();
									} else {
										d.setOriginalTranscript(transcript);
										parsedString = p.parse(transcript);
										d.decide(parsedString);
										reply = d.getToTTS();
										if (reply.isEmpty()) {
											String transcript2 = logData.getSecondResponse();
											d.setOriginalTranscript(transcript2);
											parsedString2 = p.parse(transcript2);
											d.decide(parsedString2);
											reply = d.getToTTS();
										}
										if (reply.isEmpty()) {
											// call the look for answer API
											try {
												d.setOriginalTranscript(transcript);
												d.lookforAnswer();
												reply = d.getToTTS();
											} catch (IOException e) {
												e.printStackTrace();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}

									}
								} else {
									reply = "";
								}
								logReplies.writeData(reply);
							}
								break;
							case 2: // yesno
							{
								if (logData.getFirstResponse() != null) {
									transcript = logData.getFirstResponse();
									Boolean found;
									String[] janein = { "yes", "Yes", "jep", "Jep", "yup", "Yup", "Yep", "yep", "ja",
											"Ja", "no", "No", "nope", "Nope", "nah", "Nah" };
									System.out.println(transcript);
									for (int i = 0; i < janein.length; i++) {
										found = transcript.contains(janein[i]);
										if (found) {
											reply = janein[i];
											break;
										} else {
											reply = "";
										}
									}
								} else {
									reply = "";
								}
								logReplies.writeData(reply);
							}
								break;
							}

							if (reply.isEmpty()) {
								/// Just for fun, generate random output
								int randNum = 0;
								String randOut = "";

								Random random = new Random();
								randNum = random.nextInt(6);

								switch (randNum) {
								case 0:
									randOut = "I think someone stole your car.";
									break;
								case 1:
									randOut = "What did you say?";
									break;
								case 2:
									randOut = "Does your partner know that you are pregnant in the third month?";
									break;
								case 3:
									randOut = "By the way, your car is just being towed away!";
									break;
								case 4:
									randOut = "By the way, you look great!";
									break;
								case 5:
									randOut = "Do you know in Season 7 of Game of Thrones, both Sansa and Arya will survive?";
									break;
								case 6:
									randOut = "Do you know in Season 7 of Game of Thrones, Cersei is pregnant?";
									break;
								default:
									randOut = "Hello human.";
								}

								System.out.println("I can't hear what you said. Please repeat.\n");

								// gstt.udpCom.sendSocket(randOut,
								// gstt.targetIP, gstt.targetPort);

								// establish new Connection for sending txt
								// tcpServer = new TCPServer(gstt.myIP,
								// gstt.myPort, 5, true);
								tcpServer.send("#BRAIN##TEXT#" + randOut);
								// end Connection
								tcpServer.endConnection();

							} else {
								// reply = "#BRAIN##TEXT#" + reply ;
								System.out.println("Reply sent to TTS: " + reply + "\n");

								// gstt.udpCom.sendSocket(reply, gstt.targetIP,
								// gstt.targetPort);

								// establish new Connection for sending text
								// tcpServer = new TCPServer(gstt.myIP,
								// gstt.myPort, 5, true);
								tcpServer.send("#BRAIN##TEXT#" + reply);
								// end Connection
								tcpServer.endConnection();

								System.out.println("String sent.");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						reply = "";
						break;
					}

				}

				}
			}
		}
	}
}
