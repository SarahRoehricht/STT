package decision;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import answerQ.AnswerAPI;
import commands.ActionCommand;
import edu.stanford.nlp.ling.TaggedWord;

public class Decision {
	HashSet<String> hs;
	Interact i = new Interact();

	// only action scenarios set this boolean to true.
	private boolean actionCommand; // true = actionCommand
	private String actionObject;

	private String toTTS = "";
	private String originalTranscript = "";
	private AnswerAPI ans;

	// get's set by GSTT_V2 before .decide
	private int scenario;

	public Decision() {
		hs = new HashSet<String>(Arrays.asList("team", "robocup", "robocop", "at home", "time", "date", "bring", "give",
				"hello", "greetings", "hi", "howdy", "hey", "bonjour", "hallo", "go", "name", "joke", "follow", "where",
				"open","many","much"));

	}

	public String getActionObject() {
		return actionObject;
	}

	public void setActionObject(String actionObject) {
		this.actionObject = actionObject;
	}

	public boolean getActionCommand() {
		return actionCommand;
	}

	public void setActionCommand(boolean actionCommand) {
		this.actionCommand = actionCommand;
	}

	public int getScenario() {
		return scenario;
	}

	public void setScenario(int scenario) {
		this.scenario = scenario;
	}

	public String getToTTS() {
		return toTTS;
	}

	public void setToTTS(String toTTS) {
		this.toTTS = toTTS;
	}

	public String getOriginalTranscript() {
		return originalTranscript;
	}

	public void setOriginalTranscript(String originalTranscript) {
		this.originalTranscript = originalTranscript;
	}

	public void decide(ArrayList<TaggedWord> parsedString) {
		boolean match = false;

		if (scenario == 1) {

		}
		// match dictionary keywords with TaggedWord values,
		// return action value/command/call next function else look for answer
		if (getOriginalTranscript().contains("how are you")) {
			i.interaction(1);
			setToTTS(i.getReplyInteract());
			match = true;
		}
		// else if (getOriginalTranscript().contains("last question") |
		// getOriginalTranscript().contains("previous question"))
		// {
		// //get previous question
		// setToTTS("The previous question is " + );
		// }
		else {
			for (TaggedWord taggedWord : parsedString) {

				if (hs.contains(taggedWord.value().toLowerCase())) {
					// call the dictionary parser method with the matched Word,
					// and
					// the parsedString
					System.out.println(taggedWord + " is in here.");

					match = true;

					String strReturn = matchdecide(taggedWord, parsedString);
					if (!strReturn.isEmpty()) {
						setToTTS(strReturn);
					} else {
						setToTTS("");
					}

					break;
				}
			}
		}
		if (!match) {
			setToTTS("");

		}

	}

	private String matchdecide(TaggedWord match, ArrayList<TaggedWord> parsedString) {

		// System.out.println(match);
		// System.out.println(parsedString);

		// simplify match String and bring match into lowercase
		String simpleMatch = simplifyMatch(match.value());

		// executes the corresponding code with the match
		switch (simpleMatch) {

		// case to bring something
		// extendable with adjective e.g. 'the' 'blue' 'book'
		case ("bring"): {

			String object = "";
			for (int i = 0; i < parsedString.size(); i++) {

				if (parsedString.get(i).tag().equals("NN")) {
					object = parsedString.get(i).value();

					break;
				}
			}
			actionObject = object;
			actionCommand = true;
			return ("bring");
			// call or return important parameters to the function or the
			// function calling
		}

		case ("give"): {
			String object = "";
			for (int i = 0; i < parsedString.size(); i++) {

				if (parsedString.get(i).tag().equals("NN")) {
					object = parsedString.get(i).value();

					break;
				}
			}
			actionObject = object;
			actionCommand = true;
			return ("bring");

		}

		case ("hello"): {
			i.interaction(0);
			return (i.getReplyInteract());
			// if (parsedString.size() < 5) {
			// System.out.println("I would say hello if i could.");
			// // CallTTS-Module or Return Command ID for saying hello
			// }
		}

		case ("name"): {
			if (getOriginalTranscript().contains("team")) {
				i.questionAboutTeam(getOriginalTranscript());
				return (i.getReplyInteract());
			} else if (getOriginalTranscript().contains("teams")) {
				i.questionAboutTeam(getOriginalTranscript());
				return (i.getReplyInteract());
			}

			else if (getOriginalTranscript().contains("your")) {
				return ("My name is Leonie.[:-)]");
			} else {
				i.retrieveName(parsedString);
				return ("Your name is " + i.getName());
			}
		}

		case ("joke"): {
			i.interaction(2);
			return (i.getReplyInteract());
		}

		case ("team"): {
			i.questionAboutTeam(getOriginalTranscript());
			return (i.getReplyInteract());
		}

		case ("teams"): {
			i.questionAboutTeam(getOriginalTranscript());
			return (i.getReplyInteract());

		}
		// fall-through
		case ("robocop"): {

		}
		case ("robocup"): {
			return ("Robocup at home is founded in the year 2006");
		}

		case ("where"): {
			String object = "";
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN")) {
					object = parsedString.get(i).value();
				}

			}
			actionObject = object;
			actionCommand = true;
			return ("surrounding");

		}
		case ("open"): {

			String object = "";
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN")) {
					object = parsedString.get(i).value();
				}
			}
			actionObject = object;
			actionCommand = true;
			return ("open");
		}
		// fall-through
		case ("much"): {

		}
		case ("many"): {
			System.out.println(parsedString);
//			String object = "";
//			for (int i = 0; i < parsedString.size(); i++) {
//				if (parsedString.get(i).tag().equals("NN")) {
//					object = parsedString.get(i).value();
//				}
//			}
//			actionObject = object;
//			actionCommand = true;
			return ("");
		}

		case ("at home"): {
			return ("Robocup at home is founded in the year 2006");
		}

		// alternative way to get date and time, can get it from Wolfram
		// Alpha
		case ("date"): {
			String date = new SimpleDateFormat("EEEEE, MMMM dd, yyyy", Locale.US).format(new Date());
			return ("Today is " + date);
		}

		case ("time"): {
			String time = new SimpleDateFormat("h:mm a, zzzz", Locale.US).format(new Date());
			return ("The current time is " + time);
		}

		case ("follow"): {
			// action.followPerson();
			return ("I will follow you.");
		}
		}

		return ("");

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

	// calls AnswerAPI to get Answer
	public void lookforAnswer() throws IOException, Exception {
		AnswerAPI ans = new AnswerAPI();
		setToTTS(ans.answerQuestion(getOriginalTranscript()));
	}

	//
	public void reset() {
		AnswerAPI ans = new AnswerAPI();
		try {
			setToTTS(ans.answerQuestion(getOriginalTranscript()));
		} catch (UnsupportedEncodingException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
