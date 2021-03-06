package main;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

import javaFlacEncoder.FLACFileWriter;

/*GSTT with UDP Receiver and Sender #STT(String)*/

public class GSTT
{

	static String question = "";
	static int stage = 0;
	static String filter = "";
	static String toBeSent = "";
	static String beforeFirstComma = "";

	private UDPConnection udpCom;

	private Datalogger log;

	private InetAddress myIP;
	private int myPort;
	private InetAddress targetIP;
	private int targetPort;

	public GSTT()
	{
		udpCom = new UDPConnection();
		log = new Datalogger();
	}

	public static void main(String[] args)
	{

		if (args.length == 4)
		{
			GSTT gstt = new GSTT();

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
					
					do
					{
						gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false);
						message = gstt.udpCom.getMessage();
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
						/* GSpeechDuplex */
						System.out.println("started");
						
						GSpeechDuplex dup = null;
						
						if (Datalogger.counter < 50)
							dup = new GSpeechDuplex("AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY");// Instantiate the APIKEY
						else if ((Datalogger.counter >= 50) && (Datalogger.counter < 100))
							dup = new GSpeechDuplex("AIzaSyB6yR8DR6onz9YEBKkHmrLAOQZth5Vv2gs");
						else if ((Datalogger.counter >= 100) && (Datalogger.counter < 150))
							dup = new GSpeechDuplex("AIzaSyCFhY2ogNV4iFX3Hg3EgGU5y9wGodmfLR8");
						else 
							dup = new GSpeechDuplex("AIzaSyDe2nR4mdQYL74iwkZx5pOBM_3MVHNZS8c");
						
						
						
						dup.addResponseListener(new GSpeechResponseListener()
						{ // Adds the listener
							public void onResponse(GoogleResponse gr)
							{									
								System.out.println("Google thinks you said: " + gr.getResponse());

								System.out.println(
										"with " + ((gr.getConfidence() != null) ? (Double.parseDouble(gr.getConfidence()) * 100) : null) + "% confidence.");
								System.out.println("Google also thinks that you might have said:" + gr.getOtherPossibleResponses());

								question = gr.getResponse();
								
								if(filter == "name")
								{
									Boolean found;
									String[] names = {"Robert", "robot", "Marc", "Mark", "mark", "Felix", "Matthias", "Mathias", "Matias", "Mattias", "J�rn", "Joern", "Philipp", "Phillipp", "Philip", "Fillip", "Filip", "Leonie", "leonie", "Onur", "Tobi", "Michelle", "Leony", "Emma", "emma"};
			
									System.out.println(question);
									if(question != null){
										for(int i = 0 ; i < names.length; i++)
										{
											found = question.contains(names[i]);
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
			
									System.out.println(question);
									if(question != null){
										for(int i = 0 ; i < janein.length; i++)
										{
											found = question.contains(janein[i]);
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
									}else{
										toBeSent = "";
									}
									filter = "yesno";
								}
								else if(gr.getResponse() == null | question == null)
								{
									System.out.println("I can't hear what you said.\n");
									toBeSent = "";
								}
								else
								{
									beforeFirstComma = question.split("\"")[0];
									toBeSent = beforeFirstComma;
								}
								
								
								if (toBeSent != null)
								{
									toBeSent = "#STT#" + toBeSent + "#";
									System.out.println("You said: " + toBeSent + "\n");
									gstt.udpCom.sendSocket(toBeSent, gstt.targetIP, gstt.targetPort);
									System.out.println("String sent.");
									if (!toBeSent.equals("#STT##"))  //CHANGED and in Dataloger add "-"
									{	
									try
									{
										gstt.log.writeData();
									} catch (IOException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									}
								} 
							}	
						});

						Microphone mic = new Microphone(FLACFileWriter.FLAC);// Instantiate microphone & record FLAC file.

						File file = new File("CRAudioTest.flac");// The File to record buffer

						while (true)
						{
								try
								{
									System.out.println("Recording...");
									mic.captureAudioToFile(file); // starts recording
									//Thread.sleep(10000);// Records for 10s
									do
									{
										gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false);
										message = gstt.udpCom.getMessage();
										System.out.println(message);
									} while (!"#STT#0#".equals(message));
									
									mic.close();// Stops recording
									
									// Sends 10 second voice recording to Google
									byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());// Saves data into memory

									dup.recognize(data, (int) mic.getAudioFormat().getSampleRate());
									mic.getAudioFile().delete();// Deletes buffer file
								} catch (Exception ex)
								{
									ex.printStackTrace();// Prints an error if smthg goes wrong
								}
							break;
						}
					}	
				}
				}
			}
		} else
		{
			System.err.println("Missing arguments!");
		}
	}
}
