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

/**
 * A simple HelloWorld demo showing a simple speech application 
 * built using Sphinx-4. This application uses the Sphinx-4 endpointer,
 * which automatically segments incoming audio into utterances and silences.
 */
public class HelloWorld {

	/**
	 * Main method for running the HelloWorld demo.
	 */
	public static void main(String[] args) {

		try {
			URL url;

			if (args.length > 0) {
				url = new File(args[0]).toURI().toURL();
			} 

			else {
				url = HelloWorld.class.getResource("helloworld.config.xml");
			}

			System.out.println("Loading...");

			boolean sendedSpeech;

			int port = 8884; /*args.length == 0 ? 57 : Integer.parseInt(args[0])*/;
	

			ConfigurationManager cm = new ConfigurationManager(url);

			Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
			Microphone microphone =  (Microphone) cm.lookup("microphone");

			/* allocate the resource necessary for the recognizer */
			recognizer.allocate();
			
			
			
			while(true){
				
				new Receiver().run(port);
					
					if ("#STT#1".equals(Receiver.received)){

						if(microphone.startRecording()){

							System.out.println
							("Say: (Good morning | Hello | How are you) ");

								while(true){
									System.out.println
									("Start speaking. Press Ctrl-C to quit.\n");

									new Receiver().run(port);

									if ("#STT#0".equals(Receiver.received)){

										microphone.stopRecording();
										
										/*
										 * This method will return when the end of speech
										 * is reached. Note that the endpointer will determine
										 * the end of speech.
										 */ 
										Result result = recognizer.recognize();

										if (result != null) {

											String resultText = result.getBestFinalResultNoFiller();
											resultText = "#STT#" + resultText;
											System.out.println("You said: " + resultText + "\n");

											/*über UDP senden*/
											DatagramSocket socket;
											try {
												socket = new DatagramSocket();

												byte[] b = resultText.getBytes();

												InetAddress host = InetAddress.getByName("134.103.111.123");
												//int port2 = 8884;
												DatagramPacket request = new DatagramPacket(b, b.length, host, port);
												socket.send(request);
												System.out.println("Packet versendet an:  " + request.getAddress() + ":" + request.getPort()
												+ " -> " + new String(request.getData()));
												sendedSpeech=true;
											} 
											catch (SocketException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} 
											catch (UnknownHostException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} 
											catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										else {
											System.out.println("I can't hear what you said.\n");
										}
									}
									else{
										System.out.println("Speak again");
									}
									break;	//continue outer;
								}
						}
						else {
							/*microphone.stopRecording();*/
							System.out.println("Stop microphone.");
							recognizer.deallocate();
							System.exit(1);
						}
					}
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