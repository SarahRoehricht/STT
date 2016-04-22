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

	static String question;
	static int stage = 0;
	static String beforeFirstComma;

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
					/* über UDP empfangen */

					System.out.println("Start GSTT? Type start");

					do
					{
						gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false);
						message = gstt.udpCom.getMessage();
						System.out.println(message);
					} while (!"start".equals(message));

					if ("start".equals(message))
					{
						stage = 1;
					}
				}
					break;

				case 1:
				{
					/* GSpeechDuplex */
					System.out.println("started");

					GSpeechDuplex dup = null;
					if (Datalogger.counter < 50)
						dup = new GSpeechDuplex("AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY");// Instantiate the
					else // APIKEY
						dup = new GSpeechDuplex("AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY");

					dup.addResponseListener(new GSpeechResponseListener()
					{ // Adds the listener

						public void onResponse(GoogleResponse gr)
						{

							System.out.println("Google thinks you said: " + gr.getResponse());

							System.out.println(
									"with " + ((gr.getConfidence() != null) ? (Double.parseDouble(gr.getConfidence()) * 100) : null) + "% confidence.");
							System.out.println("Google also thinks that you might have said:" + gr.getOtherPossibleResponses());

							question = gr.getResponse();
							beforeFirstComma = question.split("\"")[0];

							if (beforeFirstComma != null)
							{

								beforeFirstComma = "#STT#" + beforeFirstComma;
								System.out.println("You said: " + beforeFirstComma + "\n");
								stage = 2;

							} else
							{
								System.out.println("I can't hear what you said.\n");
								stage = 0;
							}
							try
							{
								gstt.log.writeData();
							} catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});

					Microphone mic = new Microphone(FLACFileWriter.FLAC);// Instantiate microphone & record FLAC file.

					File file = new File("CRAudioTest.flac");// The File to record buffer

					while (true)
					{
						if (stage == 1)
						{
							try
							{
								System.out.println("Recording...");
								mic.captureAudioToFile(file); // starts
																// recording

								Thread.sleep(10000);// Records for 10s
								mic.close();// Stops recording

								// Sends 10 second voice recording to Google
								byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());// Saves data into memory

								dup.recognize(data, (int) mic.getAudioFormat().getSampleRate());
								mic.getAudioFile().delete();// Deletes buffer file
								// }
								// REPEAT
							} catch (Exception ex)
							{
								ex.printStackTrace();// Prints an error if smthg goes wrong
							}
						}
						break;
					}
				}
					break;
				case 2:
				{
					/* über UDP senden */

					System.out.println("Send String? Type stop");

					/* über UDP empfangen */

					System.out.println("Start GSTT? Type start");

					do
					{
						gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false);
						message = gstt.udpCom.getMessage();
						System.out.println(message);
					} while (!"stop".equals(message));

					if ("stop".equals(message))
					{
						gstt.udpCom.sendSocket(beforeFirstComma, gstt.targetIP, gstt.targetPort);
						System.out.println("Packet versendet an:  " + gstt.targetIP + ":" + gstt.targetPort + " -> " + beforeFirstComma);

						System.out.println("String sent. To start again, type start");
						do
						{
							gstt.udpCom.receiveSocket(gstt.myIP, gstt.myPort, false);
							message = gstt.udpCom.getMessage();
							System.out.println(message);
						} while (!"start".equals(message));

						if ("start".equals(message))
						{
							stage = 1;
						}
					}
				}
					break;
				}
			}
		} else
		{
			System.err.println("Missing arguments!");
		}
	}
}
