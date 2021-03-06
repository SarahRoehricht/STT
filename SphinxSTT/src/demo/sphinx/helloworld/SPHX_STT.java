package demo.sphinx.helloworld;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;


public class SPHX_STT {

	static String message;
	static String filter;
	static String toBeSent = "";

	private UDPConnection udpCom;

	private InetAddress myIP;
	private int myPort;
	private InetAddress targetIP;
	private int targetPort;

	public static void main(String[] args) {

		try {
			URL url;
			url = SPHX_STT.class.getResource("helloworld.config.xml");
			
			//System.out.println("Waiting for a signal...");

			ConfigurationManager cm = new ConfigurationManager(url);

			Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
			Microphone microphone =  (Microphone) cm.lookup("microphone");

			/* allocate the resource necessary for the recognizer */
			recognizer.allocate();

			if (args.length == 4) {

				SPHX_STT stt = new SPHX_STT();
				stt.myIP = InetAddress.getByName(args[0]);
				stt.myPort = Integer.parseInt(args[1]);
				stt.targetIP = InetAddress.getByName(args[2]);
				stt.targetPort = Integer.parseInt(args[3]);

				stt.udpCom = new UDPConnection();
				

				
				while(true){
					
					/* receive via UDP*/
					System.out.println("Listening for start cmd...");
					
					do
					{
						stt.udpCom.receiveSocket(stt.myIP, stt.myPort, false);
						message = stt.udpCom.getMessage();
						System.out.println(message);
					} while (!("#STT#1#".equals(message)|"#STT#name#".equals(message)| "#STT#yesno#".equals(message)));
					
					
					if("#STT#name#".equals(message))
					{
						filter = "name";
					}else if("#STT#yesno#".equals(message))
					{
						filter = "yesno";
					}
					
					if ("#STT#1#".equals(message)| "#STT#name#".equals(message) | "#STT#yesno#".equals(message))
					{
						if(microphone.startRecording()){

							while(true){
								
							System.out.println
								("Start speaking.");
							do
							{
								stt.udpCom.receiveSocket(stt.myIP, stt.myPort, false);
								message = stt.udpCom.getMessage();
								System.out.println(message);
							} while (!"#STT#0#".equals(message));
								
									microphone.stopRecording();

									/*
									 * This method will return when the end of speech
									 * is reached. Note that the endpointer will determine
									 * the end of speech.
									 */ 
									Result result = recognizer.recognize();
																		
									if (result != null) {
										
										String resultText = result.getBestFinalResultNoFiller();
										
										if(filter == "name")
										{
											Boolean found;
											String[] names = {"Robert", "robot", "Marc", "Mark", "mark", "Felix", "Matthias", "Mathias", "Matias", "Mattias", "J�rn", "Joern", "Philipp", "Phillipp", "Philip", "Fillip", "Filip", "Leonie", "leonie", "Onur", "Tobias", "Michelle", "Leony", "Emma", "emma", "Gordon"};
					
											System.out.println(resultText);
											if(resultText != null){
												for(int i = 0 ; i < names.length; i++)
												{
													found = resultText.contains(names[i]);
														if(found)
															{
																toBeSent = names[i];
																break;
															}
														else
															{
																toBeSent = "";
															}
												}
											}else{
												toBeSent = "";
											}
											filter = "";
										}
										else if(filter == "yesno")
										{
											Boolean found;
											String[] janein = {"yes", "Yes", "jep", "Jep", "yup", "Yup", "Yep", "yep", "ja", "Ja", "no", "No", "nope", "Nope", "nah", "Nah"};
					
											System.out.println(resultText);
											if(resultText != null){
												for(int i = 0 ; i < janein.length; i++)
												{
													found = resultText.contains(janein[i]);
														if(found)
															{
																toBeSent = janein[i];
																break;
															}
														else
															{
																toBeSent = "";
															}
												}
											}
											filter = "";
										}
										else
										{
											toBeSent = resultText;
											filter = "";
										}
																			
										toBeSent = "#STT#" + toBeSent + "#";
										System.out.println("You said: " + toBeSent + "\n");

										microphone.clear();

										/*send via UDP*/	
										stt.udpCom.sendSocket(toBeSent, stt.targetIP, stt.targetPort);
									}
									else {
										System.out.println("I can't hear what you said.\n");
										String notHeard = "#STT##";
										stt.udpCom.sendSocket(notHeard, stt.targetIP, stt.targetPort);
									}
								break;
							}
						}
						else {
							System.out.println("Microphone doesn't work!");
							System.exit(1);
						}
					}
				}
			}
			else
			{
				System.err.println("Missing arguments!");
			}

		}
		catch (IOException e) {
			System.err.println("Problem when loading HelloWorld: " + e);
			e.printStackTrace();
		} 
		catch (PropertyException e) {
			System.err.println("Problem configuring HelloWorld: " + e);
			e.printStackTrace();
		} 
		catch (InstantiationException e) {
			System.err.println("Problem creating HelloWorld: " + e);
			e.printStackTrace();
		}
	}
}