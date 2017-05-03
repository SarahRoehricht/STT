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
import java.util.List;
import java.util.Locale;
import knowledge.Object;

import answerQ.AnswerAPI;
import commands.ActionCommand;
import edu.stanford.nlp.ling.TaggedWord;

public class Decision {

	Interact i = new Interact();

	// only action scenarios set this boolean to true.
	private boolean actionCommand; // true = actionCommand
	private String actionObject;

	private String toTTS = "";
	private String originalTranscript = "";
	private List<String> officialObjectsNames = new ArrayList<String>();

	private ArrayList<Object> officialObjects;
	// get's set by GSTT_V2 before .decide
	private int scenario;
	private String[] hs = new String[] { "team", "robocup", "robocop", "at home", "time", "date", "bring", "give",
			"take", "hello", "greetings", "hi", "howdy", "hey", "bonjour", "hallo", "go", "name", "joke", "follow",
			"following", "where", "open", "many", "much", "what" };

	public Decision() {

	}

	public void setOfficialObjects(ArrayList<Object> officialObjects) {

		for (Object object : officialObjects) {
			System.out.println(object.getName());
			officialObjectsNames.add(object.getName());

		}
		this.officialObjects = officialObjects;
	}

	public void decide(ArrayList<TaggedWord> parsedString) {
		boolean match = false;

		if (scenario == 1) {

			// match dictionary keywords with TaggedWord values,
			// return action value/command/call next function else look for
			// answer
			if (getOriginalTranscript().contains("big") && getOriginalTranscript().contains("hairy")
					&& ((getOriginalTranscript().toLowerCase().contains("star")))) {
				setToTTS("Chewbacca.[:-)]");
				match = true;
			} else if ((getOriginalTranscript().contains("law") || getOriginalTranscript().contains("laws"))
					&& getOriginalTranscript().contains("robotics")) {
				setToTTS("Isaac Asimov.[:-)]");
				match = true;
			} else if ((getOriginalTranscript().toLowerCase().contains("rosie") || getOriginalTranscript().contains("series"))
					&& getOriginalTranscript().contains("robot")) {
				setToTTS("The Jetsons.[:-)]");
				match = true;
			} else if ((getOriginalTranscript().contains("baby") && getOriginalTranscript().contains("series"))
					|| getOriginalTranscript().toLowerCase().contains("bambam")) {
				setToTTS("The Flintstones.[:-)]");
				match = true;
			} else if (getOriginalTranscript().contains("main") && getOriginalTranscript().toLowerCase().contains("matrix")) {
				setToTTS("Neo.[:-)]");
				match = true;
			} else if (((getOriginalTranscript().contains("robocop") || getOriginalTranscript().contains("robocup")||getOriginalTranscript().contains("home") ||getOriginalTranscript().contains("rubber"))
					)&& (getOriginalTranscript().contains("platforms")||getOriginalTranscript().contains("platforms") ||getOriginalTranscript().contains("standard")||getOriginalTranscript().contains("headphones"))) {
				setToTTS("Pepper and HSR.[:-)]");
				match = true;
			} else if (getOriginalTranscript().contains("store") && getOriginalTranscript().contains("memories")) {
				setToTTS("In my Random Access Memory and my Solid State Drive located under my skirt.[blush:true]");
				match = true;

			} else {
				for (TaggedWord taggedWord : parsedString) {
					for (int i = 0; i < hs.length; i++) {
						if (hs[i].toLowerCase().equals(taggedWord.value().toLowerCase())) {
							System.out.println(taggedWord + " is in here.");
							match = true;

							String strReturn = matchdecide(taggedWord, parsedString);
							if (!strReturn.isEmpty()) {
								setToTTS(strReturn);
								break;
							}

						}
					}
					// if (hs.contains(taggedWord.value().toLowerCase())) {
					// // call the dictionary parser method with the matched
					// // Word,
					// // and
					// // the parsedString
					// System.out.println(taggedWord + " is in here.");
					//
					// match = true;
					//
					// String strReturn = matchdecide(taggedWord, parsedString);
					// if (!strReturn.isEmpty()) {
					// setToTTS(strReturn);
					// } else {
					//
					
					// }

					// break;
					// }
				}
			}
			if (!match) {
				setToTTS("");

			}

		} else if (scenario == 3) {
			{
				i.retrieveName(parsedString);
				setToTTS(i.getName());
			}
		}
	}

	private String matchdecide(TaggedWord match, ArrayList<TaggedWord> parsedString) {

		// System.out.println(match);
		System.out.println(parsedString);

		// simplify match String and bring match into lowercase
		String simpleMatch = simplifyMatch(match.value());

		// executes the corresponding code with the match
		switch (simpleMatch) {

		// case to bring something
		// extendable with adjective e.g. 'the' 'blue' 'book'
		// fall-through
		// give & take scenario
		case ("take"): {
		}
		case ("give"): {
		}
		case ("bring"): {
			boolean found = false;
			String object = "";
			for (int i = 0; i < parsedString.size(); i++) {

				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")) {
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
				return ("");
			}
			// call or return important parameters to the function or the
			// function calling
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
				//i.retrieveName(parsedString);
				//return ("Your name is " + i.getName());
				return("");
			}
		}

		case ("joke"): {
			i.interaction(2);
			return (i.getReplyInteract());
		}

		case ("team"): {
			i.questionAboutTeam(getOriginalTranscript().toLowerCase());
			return (i.getReplyInteract());
		}

		case ("teams"): {
			i.questionAboutTeam(getOriginalTranscript().toLowerCase());
			return (i.getReplyInteract());

		}
			// fall-through
		case ("robocop"): {

		}
		case ("robocup"): {
			return ("Robocup at home is founded in the year 2006");
		}

		case ("where"): {

			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")) {
					for (int j = 0; j < officialObjects.size(); j++) {
						if (parsedString.get(i).value().equals(officialObjects.get(j).getName())) {
							return ("The location of the " + officialObjects.get(j).getName() + " is "
									+ officialObjects.get(j).getLocation());
						}
					}
				}
			}

			return ("");
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

			for (int i = 0; i < officialObjectsNames.size(); i++) {
				if (getOriginalTranscript().contains(officialObjectsNames.get(i))) {
					found = true;
					actionObject = officialObjectsNames.get(i);
					actionCommand = true;

					return ("");

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
					// if boys is in sentence
				} else if (getOriginalTranscript().contains("boys")) {
					foundCrowd = true;
					actionObject = "countBoys";
					actionCommand = true;
					// if girls is in sentence
				} else if (getOriginalTranscript().contains("girls")) {
					foundCrowd = true;
					actionObject = "countGirls";
					actionCommand = true;
					// if sit/sitting is in sentence
				} else if (getOriginalTranscript().contains("sit") || getOriginalTranscript().contains("sitting")) {
					foundCrowd = true;
					actionObject = "countSitting";
					actionCommand = true;
					// if lay/laying is in sentence
				} else if (getOriginalTranscript().contains("lay") || getOriginalTranscript().contains("laying")) {
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
				} else if (getOriginalTranscript().contains("young") || getOriginalTranscript().contains("children")) {
					foundCrowd = true;
					actionObject = "countYoung";
					actionCommand = true;
				} else if (getOriginalTranscript().contains("crowd")
						|| (getOriginalTranscript().contains("people") && getOriginalTranscript().contains("here"))) {
					foundCrowd = true;
					actionObject = "countAll";
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
			
			//For Magdeburg 
			if(getOriginalTranscript().toLowerCase().contains("dinner")&&getOriginalTranscript().toLowerCase().contains("table")){
				actionObject = "dinnerTable";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if(getOriginalTranscript().toLowerCase().contains("cabinet")){
				actionObject = "cabinet";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if(getOriginalTranscript().toLowerCase().contains("bookshelf")||getOriginalTranscript().toLowerCase().contains("bookshelves")){
				actionObject = "bookshelf";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if((getOriginalTranscript().toLowerCase().contains("kitchen")&&(!getOriginalTranscript().toLowerCase().contains("counter")))){
				actionObject = "kitchen";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if((getOriginalTranscript().toLowerCase().contains("kitchen")&&getOriginalTranscript().toLowerCase().contains("counter"))||getOriginalTranscript().toLowerCase().contains("kitchencounter")){
				actionObject = "kitchencounter";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if((getOriginalTranscript().toLowerCase().contains("sofa")||getOriginalTranscript().toLowerCase().contains("couch"))&&!(getOriginalTranscript().toLowerCase().contains("table"))){
				actionObject = "sofa";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if(getOriginalTranscript().toLowerCase().contains("couch")&&getOriginalTranscript().toLowerCase().contains("table")){
				actionObject = "couchTable";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if(getOriginalTranscript().toLowerCase().contains("side")&&getOriginalTranscript().toLowerCase().contains("table")){
				actionObject = "sideTable";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if(getOriginalTranscript().toLowerCase().contains("stove")){
				actionObject = "stove";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if(getOriginalTranscript().toLowerCase().contains("bed")){
				actionObject = "bed";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if(getOriginalTranscript().toLowerCase().contains("closet")){
				actionObject = "closet";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if(getOriginalTranscript().toLowerCase().contains("desk")){
				actionObject = "desk";
				actionCommand = true;
				found = false;
				return ("goto");
			}else if(getOriginalTranscript().toLowerCase().contains("bar")){
				actionObject = "bar";
				actionCommand = true;
				found = false;
				return ("goto");
			}
			
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")) {
					object = parsedString.get(i).value();
					found = true;
				}
			}
			if (found) {
				actionObject = object;
				actionCommand = true;
				found = false;
				return ("goto");
			} else {
				return ("");
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
			// fall throgh
		case ("following"): {

		} // sends follow action back if sentence shorter than 5 Words
		case ("follow"): {

			if (getOriginalTranscript().contains("stop")) {

				actionCommand = true;
				actionObject = "stop";
				return ("followme");

			}
			actionCommand = true;
			actionObject = "start";
			return ("followme");

		}

			// needs strong checking
		case ("what"): {
			// What city are we in, for Magdeburg
			if (getOriginalTranscript().contains("city") && getOriginalTranscript().contains("in")) {
				return ("Magdeburg");
			} else if (getOriginalTranscript().contains("size") && getOriginalTranscript().contains("crowd")) {

				actionCommand = true;
				actionObject = "countAll";

				return ("crowd");

			} else if (getOriginalTranscript().contains("people") && getOriginalTranscript().contains("number")) {

				actionCommand = true;
				actionObject = "countAll";

				return ("crowd");

			} else if (getOriginalTranscript().contains("color") || getOriginalTranscript().contains("colour")) {
				for (int i = 0; i < parsedString.size(); i++) {
					if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")) {
						for (int j = 0; j < officialObjects.size(); j++) {
							if (parsedString.get(i).value().equals(officialObjects.get(j).getName())) {
								return ("The color of the " + officialObjects.get(j).getName() + " is "
										+ officialObjects.get(j).getColor());
							}
						}
					}
				}
			}

			return ("");
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

	public List<String> getOfficialObjectsNames() {
		return officialObjectsNames;
	}

	public void setOfficialObjectsNames(List<String> officialObjectsNames) {
		this.officialObjectsNames = officialObjectsNames;
	}

	public boolean compareObject(Object o1, Object o2) {

		return true;

	}

}
