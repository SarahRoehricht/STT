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

	private UDPConnection udpCom;

	private InetAddress myIP;
	private int myPort;
	private InetAddress targetIP;
	private int targetPort;

	public static void main(String[] args) {

		try {
			URL url;

			/*if (args.length > 0) {
				url = new File(args[0]).toURI().toURL();
			} 

			else {*/
			url = SPHX_STT.class.getResource("helloworld.config.xml");
			//}

			System.out.println("Loading...");


			//int port = 8884;


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
					stt.udpCom.receiveSocket(stt.myIP, stt.myPort,false);
					message = stt.udpCom.getMessage();
					System.out.println(message);
					
					System.out.println("Listening...");

					if ("#STT#1#".equals(message)){

						if(microphone.startRecording()){

//							System.out.println ("Say: (Good morning | Hi) (Leonie) ");
//							System.out.println ("Say: (Yes | No) ");
//							System.out.println ("Say: (Okay)(Take Care | See you) ");

							while(true){
							System.out.println
								("Start speaking. Press Ctrl-C to quit.\n");

								//new Receiver().run(port);
								stt.udpCom.receiveSocket(stt.myIP, stt.myPort,false);
								message = stt.udpCom.getMessage();
								System.out.println(message);
								
								if ("#STT#0#".equals(message)){

									microphone.stopRecording();

									/*
									 * This method will return when the end of speech
									 * is reached. Note that the endpointer will determine
									 * the end of speech.
									 */ 
									Result result = recognizer.recognize();

									if (result != null) {

										String resultText = result.getBestFinalResultNoFiller();
										resultText = "#STT#" + resultText + "#";
										System.out.println("You said: " + resultText + "\n");

										microphone.clear();

										/*send via UDP*/	
										stt.udpCom.sendSocket(resultText, stt.targetIP, stt.targetPort);
										/*DatagramSocket socket;
									try {
										socket = new DatagramSocket();

										byte[] b = resultText.getBytes();

										InetAddress host = InetAddress.getByName("192.168.188.23");
										//int port2 = 8884;
										DatagramPacket request = new DatagramPacket(b, b.length, host, port);
										socket.send(request);
										System.out.println("Packet versendet an:  " + request.getAddress() + ":" + request.getPort()
										+ " -> " + new String(request.getData()));
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
									}*/
									}
									else {
										System.out.println("I can't hear what you said.\n");
									}
								}
								else{
									System.out.println("Speak again");
								}
								break;
							}
						}
						else {
							System.out.println("Microphone doesn't work!");
							//recognizer.deallocate();
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