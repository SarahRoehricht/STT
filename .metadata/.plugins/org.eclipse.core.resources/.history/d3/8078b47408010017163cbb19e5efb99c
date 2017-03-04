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
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

import javaFlacEncoder.FLACFileWriter;
import log.Datalogger;
import log.logSTT;

/*GSTT with UDP Receiver and Sender #STT(String)*/

public class GSTT {

	static String reply = "";
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

	public GSTT() {
		udpCom = new UDPConnection();
		log = new Datalogger();
		logData = new logSTT();
	}

	public static void main(String[] args) {
//		test test1 = new test();
//		test1.f();

		if (args.length == 4) {
			GSTT gstt = new GSTT();

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
						/* GSpeechDuplex */
						System.out.println("started");

//						GSpeechDuplex dup = null;
//
//						if (Datalogger.counter < 50)
//							dup = new GSpeechDuplex("AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY");// Instantiate
//																								// the
//																								// APIKEY
//						else if ((Datalogger.counter >= 50) && (Datalogger.counter < 100))
//							dup = new GSpeechDuplex("AIzaSyB6yR8DR6onz9YEBKkHmrLAOQZth5Vv2gs");
//						else if ((Datalogger.counter >= 100) && (Datalogger.counter < 150))
//							dup = new GSpeechDuplex("AIzaSyCFhY2ogNV4iFX3Hg3EgGU5y9wGodmfLR8");
//						else
//							dup = new GSpeechDuplex("AIzaSyDe2nR4mdQYL74iwkZx5pOBM_3MVHNZS8c");
//
//						dup.addResponseListener(new GSpeechResponseListener() { // Adds
//																				// the
//																				// listener
//							public void onResponse(GoogleResponse gr) {
//								System.out.println("Google thinks you said: " + gr.getResponse());
//
//								System.out
//										.println("with "
//												+ ((gr.getConfidence() != null)
//														? (Double.parseDouble(gr.getConfidence()) * 100) : null)
//												+ "% confidence.");
//								System.out.println("Google also thinks that you might have said:"
//										+ gr.getOtherPossibleResponses());
//
//								reply = gr.getResponse();
//								logOtherResponse = gr.getOtherPossibleResponses();
//
//								try {
//									logData.writeData(reply, logOtherResponse);
//								} catch (IOException e1) {
//									// TODO Auto-generated catch block
//									e1.printStackTrace();
//								}
//
//								if (reply == null) {
//									System.out.println("I can't hear what you said. Please repeat.\n");
//									toBeSent = "";
//								} else {
//									beforeFirstComma = reply.split("\"")[0];
//									toBeSent = beforeFirstComma;
//								}
//
//								if (toBeSent != null) {
//									toBeSent = "#STT#" + toBeSent + "#";
//									System.out.println("You said: " + toBeSent + "\n");
//									gstt.udpCom.sendSocket(toBeSent, gstt.targetIP, gstt.targetPort);
//									System.out.println("String sent.");
//									if (!toBeSent.equals("#STT##")) {
//										try {
//											gstt.log.writeData();
//										} catch (IOException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//									}
//								}
//							}
//						});

						Microphone mic = new Microphone(FLACFileWriter.FLAC);// Instantiate
																				// microphone
																				// &
																				// record
																				// FLAC
																				// file.

						File file = new File("CRAudioTest.flac");// The File to
																	// record
																	// buffer

						while (true) {
							try {
								System.out.println("Recording...");
								mic.captureAudioToFile(file); // starts
																// recording
								// Thread.sleep(10000);// Records for 10s
								do {
									gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false);
									message = gstt.udpCom.getMessage();
									System.out.println(message);
								} while (!"#STT#0#".equals(message));

								mic.close();// Stops recording

								// Sends 10 second voice recording to Google
								byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());// Saves
																								// data
																								// into
																								// memory
								//dup.recognize(data, (int) mic.getAudioFormat().getSampleRate());
								//mic.getAudioFile().delete();// Deletes buffer
															// file

								String request = "https://www.google.com/speech-api/v2/recognize?client=chromium&lang=en-us&key=AIzaSyCFhY2ogNV4iFX3Hg3EgGU5y9wGodmfLR8";

								URL url = new URL(request);
								HttpURLConnection connection = (HttpURLConnection) url.openConnection();
								connection.setDoOutput(true);
								connection.setDoInput(true);
								connection.setInstanceFollowRedirects(false);
								connection.setRequestMethod("POST");
								connection.setRequestProperty("Content-Type", "audio/x-flac; rate=8000");
								connection.setRequestProperty("User-Agent", "speech2text");
								connection.setConnectTimeout(60000);
								connection.setUseCaches(false);

								DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
								wr.write(data);
								wr.flush();
								wr.close();
								connection.disconnect();

								System.out.println("Done");

								BufferedReader in = new BufferedReader(
										new InputStreamReader(connection.getInputStream()));
								String decodedString;
								while ((decodedString = in.readLine()) != null) {
									System.out.println(decodedString);
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
		} else {
			System.err.println("Missing arguments!");
		}
	}
}
