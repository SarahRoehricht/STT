package decision;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import answerQ.AnswerAPI;
import commands.actionCommand;
import edu.stanford.nlp.ling.TaggedWord;

public class Decision {
	HashSet<String> hs;
	Interact i = new Interact();
	actionCommand action = new actionCommand(); //parameter = command
	
	private String toTTS = "";
	private String originalTranscript = "";
	private AnswerAPI ans;
	
	public Decision() {
		hs = new HashSet<String>(
				Arrays.asList("bring", "give", "hello", "greetings", "hi", "howdy", "hey", "bonjour", "hallo", "go", "name", "joke", "how", "are", "you", "follow"));

	}
	
	public String getToTTS()
	{
		return toTTS;
	}

	public void setToTTS(String toTTS)
	{
		this.toTTS = toTTS;
	}

	public String getOriginalTranscript()
	{
		return originalTranscript;
	}

	public void setOriginalTranscript(String originalTranscript)
	{
		this.originalTranscript = originalTranscript;
	}

	public void decide(ArrayList<TaggedWord> parsedString) {
		boolean match = false;
		// match dictionary keywords with TaggedWord values,
		// return action value/command/call next function else look for answer

		for (TaggedWord taggedWord : parsedString) {

			if (hs.contains(taggedWord.value().toLowerCase())) {
				// call the dictionary parser method with the matched Word, and
				// the parsedString
				System.out.println("it's in here.");
	
				match = true;
				
				//looks for "how are you"
				if(taggedWord.value().toLowerCase().equals("how")){
					for (int j = 0; j < parsedString.size(); j++)
					{
						if (parsedString.get(j).value().equals("how") && parsedString.get(j+1).value().equals("are")&& parsedString.get(j+2).value().equals("you") )
						{
							i.interaction(1);
							setToTTS(i.getReplyInteract());
						}
					}
				}
				setToTTS(matchdecide(taggedWord, parsedString));
				break;
			}

		}
		if (!match) {
			// call the look for answer API
			try
			{
				lookforAnswer();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private String matchdecide(TaggedWord match, ArrayList<TaggedWord> parsedString) {

		//System.out.println(match);
		//System.out.println(parsedString);

		// simplify match String and bring match into lowercase
		String simpleMatch = simplifyMatch(match.value());

		// executes the corresponding code with the match
		switch (simpleMatch) {

		// case to bring something
		// extendable with adjective e.g. 'the' 'blue' 'book'
		case ("bring"): {
			String DT = "";
			String object = "";
			for (int i = 0; i < parsedString.size(); i++) {

				if (parsedString.get(i).tag().equals("NN")) {
					object = parsedString.get(i).value();
					if (parsedString.get(i - 1).tag().equals("DT")) {
						DT = parsedString.get(i - 1).value();
					}
					break;
				}
			}
			System.out.println("OK - I will bring " + DT + " " + object + "." + " ... beep boop");
			return ("OK - I will bring " + DT + " " + object + "." + " ... beep boop");
			// call or return important parameters to the function or the
			// function calling
		}

		case ("give"): {
			System.out.println("I would give you something if i could.");
			return("I would give you something if i could.");

		}

		case ("hello"): {
			i.interaction(0);
			return(i.getReplyInteract());
//			if (parsedString.size() < 5) {
//				System.out.println("I would say hello if i could.");
//				// CallTTS-Module or Return Command ID for saying hello
//			}
		}
		
		case ("name"): {
			i.retrieveName(parsedString);
			return(i.getName());
		}
		
		case ("joke"): {
			i.interaction(2);
			return(i.getReplyInteract());
		}
		case ("follow"): {
			action.followPerson();
			return("I will follow you.");
		}
		}
		return null;

	}

	// simplifies String e.g. hey -> hello for later-used switch case
	private String simplifyMatch(String match) {
		HashSet<String> helloHs = new HashSet<String>(
				Arrays.asList("hello", "greetings", "hi", "howdy", "hey", "bonjour", "hallo"));
		if (helloHs.contains(match)) {
			return "hello";
		}
		
		return match.toLowerCase();
	}
	
	private void lookforAnswer() throws IOException, Exception {
		AnswerAPI ans = new AnswerAPI();
		setToTTS(ans.answerQuestion(getOriginalTranscript()));
	}

	
}
