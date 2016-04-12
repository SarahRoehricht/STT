package main;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

import javaFlacEncoder.FLACFileWriter;

/*GSTT with UDP Receiver and Sender #STT(String)*/

public class gsttUDP {

	static String question;
	static int stage = 0;
	static String beforeFirstComma;

	public static void main(String[] args) {
		
		while (true) {
		
			switch (stage) {
		
			case 0: {
				/* �ber UDP empfangen */
				
				System.out.println("Start GSTT? Type start");
				
				do {
					int port = args.length == 0 ? 57 : Integer.parseInt(args[0]);
					new Receiver().run(port);
					System.out.println(Receiver.input);
				} while (!"start".equals(Receiver.input));
				
				if ("start".equals(Receiver.input)) {
					stage = 1;
				}
			}
				break;

			case 1: {
				/* GSpeechDuplex */
				
				System.out.println("started");

				GSpeechDuplex dup = new GSpeechDuplex("AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY");// Instantiate the APIKEY
																									
				dup.addResponseListener(new GSpeechResponseListener() {		// Adds the listener
																		
					public void onResponse(GoogleResponse gr) {
						
						System.out.println("Google thinks you said: " + gr.getResponse());

						System.out.println("with "
								+ ((gr.getConfidence() != null) ? (Double.parseDouble(gr.getConfidence()) * 100) : null)
								+ "% confidence.");
						System.out.println(
								"Google also thinks that you might have said:" + gr.getOtherPossibleResponses());

						question = gr.getResponse();
						beforeFirstComma = question.split("\"")[0];
						
						if (beforeFirstComma != null) {

							beforeFirstComma = "#STT#" + beforeFirstComma;
							System.out.println("You said: " + beforeFirstComma + "\n");
							stage = 2;

						} else {
							System.out.println("I can't hear what you said.\n");
							stage = 0;
						}
					}

				});

			
				Microphone mic = new Microphone(FLACFileWriter.FLAC);// Instantiate microphone & record FLAC file.
																		
				File file = new File("CRAudioTest.flac");// The File to record buffer
															
				while (true) {
					if (stage == 1) {
						try {
							System.out.println("Recording...");
							mic.captureAudioToFile(file); // starts recording

							Thread.sleep(10000);// Records for 10s
							// System.in.read(); or stop with press any keys

							mic.close();// Stops recording

							// Sends 10 second voice recording to Google
							byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());// Saves data into memory
																							
							dup.recognize(data, (int) mic.getAudioFormat().getSampleRate());
							mic.getAudioFile().delete();// Deletes buffer file
							
							// REPEAT
						} catch (Exception ex) {
							ex.printStackTrace();// Prints an error if smthg goes wrong
						}
					}
					break;
				}
			}
			break;
			
			case 2: {
				/* �ber UDP senden */
				
				System.out.println("Send String? Type stop");
				
				do {
					int port = args.length == 0 ? 57 : Integer.parseInt(args[0]);
					new Receiver().run(port);
					System.out.println(Receiver.input);
				} while (!"stop".equals(Receiver.input));

				if ("stop".equals(Receiver.input)) {
					DatagramSocket socket;
					try {
						socket = new DatagramSocket();

						byte[] b = beforeFirstComma.getBytes("UTF-8");

						InetAddress host = InetAddress.getByName("10.0.0.3"); // Subnet Calculator (Packet Sender)
																				
						int port = 55056;
						DatagramPacket request = new DatagramPacket(b, b.length, host, port);
						socket.send(request);
						System.out.println("Packet versendet an:  " + request.getAddress() + ":" + request.getPort()
								+ " -> " + new String(request.getData()));
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("String sent. To start again, type start");
					do {
						int port = args.length == 0 ? 57 : Integer.parseInt(args[0]);
						new Receiver().run(port);
						System.out.println(Receiver.input);
					} while (!"start".equals(Receiver.input));
					if("start".equals(Receiver.input)){
						stage = 1;
					}
					}
				}
				break;
			}
		}
	}
}