package main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
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

	private UDPConnection udpCom;

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
		udpCom = new UDPConnection();
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

					do {
						try {
							gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false, false);
						} catch (SocketException e) {
							e.printStackTrace();
						}
						message = gstt.udpCom.getMessage();
						System.out.println(message);
					} while (!("#STT#1#".equals(message) | "#STT#2#".equals(message) | "#STT#3#".equals(message)));

					// scenario 1 Small talk, 2 yes.no, 3 asking for name
					// directly
					if ("#STT#1#".equals(message)) {
						scenario = 1;
						// set to yes.no scenario
					} else if ("#STT#2#".equals(message)) {

						scenario = 2;
						// set to receiving name scenario.
					} else if ("#STT#3#".equals(message)) {
						scenario = 3;
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
					File file = new File("RT-Lions_MicrophoneRecording_" + fileName + ".wav"); // direct
					// path
					// to
					// audio
					// data
					// folder missing

					while (true) {
						try {
							System.out.println("Recording...");
							mic.captureAudioToFile(file); // starts recording

							// waits for #STT#0# package or waits for 10s
							do {

								gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false, true);
								message = gstt.udpCom.getMessage();
								System.out.println(message);
								if (message.contains("timeout")) {
									break;
								}

							} while (!"#STT#0#".equals(message));

							mic.close();
							System.out.println("Closed mic...");

							byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());

							// mic.getAudioFile().delete();
							try {

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
								connection.setConnectTimeout(10000);
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
									e.printStackTrace();
								}

								BufferedReader in = new BufferedReader(
										new InputStreamReader(connection.getInputStream()));

								String decodedString;
								List<String> ls = new ArrayList<String>();

								while ((decodedString = in.readLine()) != null) {
									ls.add(decodedString);
									System.out.println(decodedString);
								}

								try {
									logData.readData(); // get previous question
														// before being
														// overwritten
									logData.writeData(ls);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							} catch (Exception e) {
								
							}
							if(logData.getFirstResponse()!=null){
								gstt.udpCom.sendSocket("#STT#TEXT#" + logData.getFirstResponse() + "#", gstt.targetIP, gstt.targetPort);
							}else{
								gstt.udpCom.sendSocket("#STT#RETRY#" + "#", gstt.targetIP, gstt.targetPort);
							}
							
							// case 0,1: Get first response, parse and answer
							switch (scenario) {

							case 1: // default Smalltalk
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
										d.setScenario(scenario);
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

								break;
							}
							case 2: // yes no
							{
								if (logData.getFirstResponse() != null) {
									transcript = logData.getFirstResponse();
									Boolean found = false;
									String[] yes = { "yes", "Yes", "jep", "Jep", "yup", "Yup", "Yep", "yep", "ja",
											"Ja" };
									String[] no = { "no", "No", "nope", "Nope", "nah", "Nah" };
									System.out.println(transcript);
									for (int i = 0; i < yes.length; i++) {
										found = transcript.contains(yes[i]);
										if (found) {
											reply = "yes";
											break;
										} else {
											reply = "";
										}
									}
									if (!found) {
										for (int i = 0; i < no.length; i++) {
											found = transcript.contains(no[i]);
											if (found) {
												reply = "no";
												break;
											} else {
												reply = "";
											}
										}
									}
								} else {
									reply = "";
								}
								logReplies.writeData(reply);

								break;
							}
							case 3: // name
							{if (logData.getFirstResponse() != null) {
								transcript = logData.getFirstResponse();
								d.setOriginalTranscript(transcript);
								parsedString = p.parse(transcript);
								d.setScenario(scenario);
								d.decide(parsedString);
								reply = d.getToTTS();
							}}
							}
							if (reply.isEmpty()) {
								/// Just for fun, generate random output
								int randNum = 0;
								String randOut = "";

								Random random = new Random();
								randNum = random.nextInt(3);

								switch (randNum) {
								case 0:
									randOut = "I can't hear what you said. Please repeat.";
									break;
								case 1:
									randOut = "What did you say?";
									break;
								case 2:
									randOut = "Could you repeat that?.";
									break;
								case 3:
									randOut = "Do you mind saying that again?";
									break;
								default:
									randOut = "Hello human.";
								}
								randOut += "[:-)]";
								System.out.println("I can't hear what you said. Please repeat.\n");
								reply = randOut;
								gstt.udpCom.sendSocket("#STT#RETRY#" + reply + "#", gstt.targetIP, gstt.targetPort);

							} else {
								// reply = "#BRAIN##TEXT#" + reply ;
								System.out.println("Reply sent to TTS: " + reply + "\n");
								if (d.getActionCommand()) {
									gstt.udpCom.sendSocket("#STT#ACTION#" + reply + ";" + d.getActionObject() + "#",
											gstt.targetIP, gstt.targetPort);
								} else {
									gstt.udpCom.sendSocket("#STT#ANSWER#" + reply + "#", gstt.targetIP, gstt.targetPort);
								}
								// resetting ActionCommand Boolean
								d.setActionCommand(false);

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
