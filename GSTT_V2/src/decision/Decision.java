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
	private String[] officialObjects = { "plant", "plants", "table", "tables" };

	// get's set by GSTT_V2 before .decide
	private int scenario;

	public Decision() {
		hs = new HashSet<String>(Arrays.asList("team", "robocup", "robocop", "at home", "time", "date", "bring", "give",
				"hello", "greetings", "hi", "howdy", "hey", "bonjour", "hallo", "go", "name", "joke", "follow", "where",
				"open", "many", "much"));

	}

	public void decide(ArrayList<TaggedWord> parsedString) {
		boolean match = false;

		if (scenario == 1) {

			// match dictionary keywords with TaggedWord values,
			// return action value/command/call next function else look for
			// answer
			if (getOriginalTranscript().contains("how are you")) {
				i.interaction(1);
				setToTTS(i.getReplyInteract());
				match = true;
			} else {
				for (TaggedWord taggedWord : parsedString) {

					if (hs.contains(taggedWord.value().toLowerCase())) {
						// call the dictionary parser method with the matched
						// Word,
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

		}else if(scenario==3){
			i.retrieveName(parsedString);
			
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
			boolean found = false;
			String object = "";
			for (int i = 0; i < parsedString.size(); i++) {

				if (parsedString.get(i).tag().equals("NN")) {
					object = parsedString.get(i).value();
					found = true;
					break;
				}
			}
			if (found) {
				actionObject = object;
				actionCommand = true;
				found = false;
				return ("bring");
			} else {
				try {
					lookforAnswer();
				} catch (IOException e) {
					e.printStackTrace();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// call or return important parameters to the function or the
			// function calling
		}
		// give scenario
		case ("give"): {
			String object = "";
			boolean found = false;
			for (int i = 0; i < parsedString.size(); i++) {

				if (parsedString.get(i).tag().equals("NN")) {
					object = parsedString.get(i).value();
					found = true;
					break;
				}
			}
			if (found) {
				actionObject = object;
				actionCommand = true;
				found = false;
				return ("bring");
			} else {
				try {
					lookforAnswer();
				} catch (IOException e) {
					return ("");
				} catch (Exception e) {
					return ("");
				}
			}
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
			boolean found = false;
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN")) {
					object = parsedString.get(i).value();
					found = true;
				}

			}
			if (found) {

				actionObject = object;
				actionCommand = true;
				found = false;
				return ("surrounding");
			} else {
				try {
					lookforAnswer();
				} catch (IOException e) {
					return ("");
				} catch (Exception e) {
					return ("");
				}
			}
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
			boolean found = false;
			System.out.println(parsedString);
			for (int i = 0; i < officialObjects.length; i++) {
				System.out.println(officialObjects[i]);
			}

			for (int i = 0; i < officialObjects.length; i++) {
				if (getOriginalTranscript().contains(officialObjects[i])) {
					found = true;
					actionObject = officialObjects[i];
					actionCommand = true;

				}

			}

			// if parts of the sentence weren't in the official objects list
			if (!found) {
				boolean foundCrowd = false;
				// if male and not female are in sentence
				if (getOriginalTranscript().contains("male")
						|| getOriginalTranscript().contains("males") && !(getOriginalTranscript().contains("female")
								|| getOriginalTranscript().contains("females"))) {
					foundCrowd = true;
					actionObject = "countMale";
					actionCommand = true;
					// if female and not male are in sentence
				} else if (getOriginalTranscript().contains("female") || getOriginalTranscript().contains("females")
						&& !(getOriginalTranscript().contains("male") || getOriginalTranscript().contains("males"))) {
					foundCrowd = true;
					actionObject = "countFemale";
					actionCommand = true;
					// if male and female are in sentence
				} else if (getOriginalTranscript().contains("female") || getOriginalTranscript().contains("females")
						&& (getOriginalTranscript().contains("male") || getOriginalTranscript().contains("males"))) {
					foundCrowd = true;
					actionObject = "countAll";
					actionCommand = true;
					// if sit/sitting is in sentence
				} else if (getOriginalTranscript().contains("sit") || getOriginalTranscript().contains("sitting")) {
					foundCrowd = true;
					actionObject = "countSitting";
					actionCommand = true;
					// if lay/laying is in sentence
				} else if (getOriginalTranscript().contains("lay") || getOriginalTranscript().contains("sitting")) {
					foundCrowd = true;
					actionObject = "countLaying";
					actionCommand = true;
					// if stand/standing is in sentence
				} else if (getOriginalTranscript().contains("stands") || getOriginalTranscript().contains("standing")
						|| getOriginalTranscript().contains("stand")) {
					foundCrowd = true;
					actionObject = "countStanding";
					actionCommand = true;
					// if wave/waves/waving is in sentence
				} else if (getOriginalTranscript().contains("wave") || getOriginalTranscript().contains("waves")
						|| getOriginalTranscript().contains("waving")) {
					foundCrowd = true;
					actionObject = "countHandSign";
					actionCommand = true;
					// if old is in sentence
				} else if (getOriginalTranscript().contains("old")) {
					foundCrowd = true;
					actionObject = "countOld";
					actionCommand = true;
					// if young is in sentence
				} else if (getOriginalTranscript().contains("young")) {
					foundCrowd = true;
					actionObject = "countYoung";
					actionCommand = true;
				}
				// if one of the above was found
				if (foundCrowd) {
					foundCrowd = false;
					return ("crowd");
				}

			}
			if (found) {
				found = false;
				return ("surrounding");
			}
			return ("");
		}
		case ("go"): {
			String object = "";
			boolean found = false;
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN")) {
					object = parsedString.get(i).value();
				}
			}
			if (found) {
				actionObject = object;
				actionCommand = true;
				found = false;
				return ("goto");
			} else {
				try {
					lookforAnswer();
				} catch (IOException e) {
					return ("");
				} catch (Exception e) {
					return ("");
				}
			}
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
		// sends follow action back if sentence shorter than 5 Words
		case ("follow"): {
			if (getOriginalTranscript().length() < 5) {

				actionCommand = true;
				actionObject = "";
				return ("follow");
			}
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
			e.printStackTrace();
		}
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

	public String[] getOfficialObjects() {
		return officialObjects;
	}

	public void setOfficialObjects(String[] officialObjects) {
		this.officialObjects = officialObjects;
	}

}
