package main;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

public class Sphinx4 {

	//static final long RECORD_TIME = 5000;  

	private static String resultText;

	static String message;
	static String filter;
	static String toBeSent = "";

	private UDPConnection udpCom;

	private InetAddress myIP;
	private int myPort;
	private InetAddress targetIP;
	private int targetPort;

	private static final String ACOUSTIC_MODEL = "resource:/main/cmusphinx-en-us-ptm-5.2";
	private static final String DICTIONARY_PATH = "resource:/main/5561.dic";
	private static final String LANGUAGE_MODEL = "resource:/main/5561.lm";
	//private static final String	GRAMMAR_PATH = "resource:/main";

	public static void main(String[] args) throws Exception {

		Configuration configuration = new Configuration();
		configuration.setAcousticModelPath(ACOUSTIC_MODEL);
		configuration.setDictionaryPath(DICTIONARY_PATH);
		configuration.setLanguageModelPath(LANGUAGE_MODEL);
		//configuration.setGrammarPath(GRAMMAR_PATH);
		//configuration.setGrammarName("Hello");
		//configuration.setUseGrammar(true);

		//LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
		StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);

		if (args.length == 4) {

			Sphinx4 stt = new Sphinx4();
			stt.myIP = InetAddress.getByName(args[0]);
			stt.myPort = Integer.parseInt(args[1]);
			stt.targetIP = InetAddress.getByName(args[2]);
			stt.targetPort = Integer.parseInt(args[3]);

			stt.udpCom = new UDPConnection();
			
			while(true){

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

				final Recorder recorder = new Recorder();

				// creates a new thread that waits for a specified
				// of time before stopping
				Thread stopper = new Thread(new Runnable() {
					public void run() {
							do{
								stt.udpCom.receiveSocket(stt.myIP, stt.myPort,false);
								message = stt.udpCom.getMessage();
								System.out.println(message);
								}while(!"#STT#0#".equals(message));
							//Thread.sleep(RECORD_TIME); 
						recorder.finish();
					}
				});

				// Start the record
				if("#STT#1#".equals(message)| "#STT#name#".equals(message) | "#STT#yesno#".equals(message)){

					stopper.start();
					
					// start recording
					recorder.start();
					
					// recognition from the recorded wave-file
					recognizer.startRecognition(new FileInputStream("RecordAudio.wav"));
					SpeechResult result;
					System.out.println("Recognition starts");
					
					while ((result = recognizer.getResult()) != null) {
						resultText = result.getHypothesis();
						resultText = resultText.substring(0).toLowerCase();
						System.out.format("ResultText: %s\n", resultText);
					}
					
					if(filter == "name")
					{
						Boolean found;
						String[] names = {"robert", "marc", "mark", "felix", "matthias", "mathias", "matias", "mattias", "philipp", "phillipp", "philip", "phillip", "fillip", "filip", "leonie", "onur", "tobias", "michelle", "leony", "emma", "gordon"};

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
						String[] janein = {"yes", "jep", "yup", "yep", "ja", "no", "nope" , "nah"};

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
					
					// Send the ResultText to brain
					if("#STT#0#".equals(message)){
						toBeSent= "#STT#" + toBeSent + "#";
						stt.udpCom.sendSocket(toBeSent, stt.targetIP, stt.targetPort);
						recognizer.stopRecognition();
					}
				}
			}
		}
	}
}
