package decision;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
			"following", "where", "open", "many", "much", "what", "big", "biggest", "smallest", "lightest", "heaviest",
			"largest" };
	private String[] locationList = new String[] { "living room", "shelf", "cabinet", "couch table", "desk", "shelf",
			"house", "table", "kitchen", "dining room", "closet", "bedroom" };
	private List<String> categoryList = new ArrayList<String>();
	private Object heaviestObject = null;
	private Object lightestObject = null;
	private Object largestObject = null;
	private Object smallestObject = null;
	private Map<String, Object> heavyCat;
	private Map<String, Object> smallCat;
	private Map<String, Object> largeCat;
	private Map<String, Object> lightCat;

	public Decision() {

	}

	public void setOfficialObjects(ArrayList<Object> officialObjects) {

		for (Object object : officialObjects) {
			System.out.println(object.getName());
			officialObjectsNames.add(object.getName());
			if (!categoryList.contains(object.getCategory())) {
				categoryList.add(object.getCategory());

			}

		}
		for (String category : categoryList) {
			System.out.print(category + ", ");
		}
		this.officialObjects = officialObjects;
		setLargestObject();
		setLargestCategory();
		setSmallestObject();
		setSmallestCategory();
		setLightestObject();
		setLightestCategory();
		setHeaviestObject();
		setHeaviestCategory();

	}

	private void setHeaviestObject() {

		int heaviest = 0;

		for (Object object : officialObjects) {
			if (object.getWeight() > heaviest) {
				this.heaviestObject = object;
			}
		}
	}

	private void setHeaviestCategory() {
		heavyCat = new HashMap<String, Object>();
		int heaviest = 0;
		int index = 0;
		for (int i = 0; i < categoryList.size(); i++) {
			for (int j = 0; j < officialObjects.size(); j++) {
				if (categoryList.get(i).contains(officialObjects.get(j).getCategory())) {
					if (officialObjects.get(j).getWeight() > heaviest) {
						index = j;
						heaviest = officialObjects.get(j).getWeight();
					}
				}
			}
			heaviest = 0;
			heavyCat.put(officialObjects.get(index).getCategory(), officialObjects.get(index));
		}

	}

	private void setLightestCategory() {
		lightCat = new HashMap<String, Object>();
		int lightest = 10000;
		int index = 0;
		for (int i = 0; i < categoryList.size(); i++) {
			for (int j = 0; j < officialObjects.size(); j++) {
				if (categoryList.get(i).contains(officialObjects.get(j).getCategory())) {
					if (officialObjects.get(j).getWeight() < lightest) {
						index = j;
						lightest = officialObjects.get(j).getWeight();
					}
				}
			}
			lightest = 10000;
			lightCat.put(officialObjects.get(index).getCategory(), officialObjects.get(index));
		}
	}

	private void setLightestObject() {
		int lightest = 10000;
		for (Object object : officialObjects) {
			if (object.getWeight() < lightest) {
				lightest = object.getWeight();
				this.lightestObject = object;
			}
		}
	}

	private void setSmallestCategory() {
		smallCat = new HashMap<String, Object>();
		int smallest = 10000;
		int index = 0;
		for (int i = 0; i < categoryList.size(); i++) {
			for (int j = 0; j < officialObjects.size(); j++) {
				if (categoryList.get(i).contains(officialObjects.get(j).getCategory())) {
					if (officialObjects.get(j).getSize() < smallest) {
						index = j;
						smallest = officialObjects.get(j).getSize();
					}
				}
			}
			smallest = 10000;
			smallCat.put(officialObjects.get(index).getCategory(), officialObjects.get(index));
		}
	}

	private void setSmallestObject() {
		int smallest = 10000;
		for (Object object : officialObjects) {
			if (object.getSize() < smallest) {
				smallest = object.getSize();
				this.smallestObject = object;
			}
		}
	}

	private void setLargestCategory() {
		largeCat = new HashMap<String, Object>();
		int largest = 0;
		int index = 0;
		for (int i = 0; i < categoryList.size(); i++) {
			for (int j = 0; j < officialObjects.size(); j++) {
				if (categoryList.get(i).contains(officialObjects.get(j).getCategory())) {
					if (officialObjects.get(j).getSize() > largest) {
						index = j;
						largest = officialObjects.get(j).getSize();
					}
				}
			}
			largest = 0;
			largeCat.put(officialObjects.get(index).getCategory(), officialObjects.get(index));
		}
	}

	private void setLargestObject() {
		int largest = 0;
		for (Object object : officialObjects) {
			if (object.getSize() > largest) {
				largest = object.getSize();
				this.largestObject = object;
			}
		}
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
			} else if ((getOriginalTranscript().toLowerCase().contains("rosie")
					|| getOriginalTranscript().contains("series")) && getOriginalTranscript().contains("robot")) {
				setToTTS("The Jetsons.[:-)]");
				match = true;
			} else if ((getOriginalTranscript().contains("baby") && getOriginalTranscript().contains("series"))
					|| getOriginalTranscript().toLowerCase().contains("bambam")
					|| getOriginalTranscript().toLowerCase().contains("bum")) {
				setToTTS("The Flintstones.[:-)]");
				match = true;
			} else if (getOriginalTranscript().contains("main")
					&& getOriginalTranscript().toLowerCase().contains("matrix")) {
				setToTTS("Neo.[:-)]");
				match = true;
			} else if (((getOriginalTranscript().contains("robocop") || getOriginalTranscript().contains("robocup")
					|| getOriginalTranscript().contains("home") || getOriginalTranscript().contains("rubber")))
					&& (getOriginalTranscript().contains("platforms") || getOriginalTranscript().contains("platforms")
							|| getOriginalTranscript().contains("standard")
							|| getOriginalTranscript().contains("headphones"))) {
				setToTTS("Pepper and HSR.[:-)]");
				match = true;
			} else if (getOriginalTranscript().contains("store") && getOriginalTranscript().contains("memories")) {
				setToTTS(
						"[blush:true]In my Random Access Memory and my Solid State Drive located under my skirt.[blush:false]");
				match = true;

			} else {
				for (TaggedWord taggedWord : parsedString) {
					for (int i = 0; i < hs.length; i++) {
						if (hs[i].toLowerCase().equals(taggedWord.value().toLowerCase())) {
							System.out.println(taggedWord + " is in here.");

							String strReturn = matchdecide(taggedWord, parsedString);
							if (!strReturn.isEmpty()) {
								match = true;
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
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")
						|| parsedString.get(i).tag().equals("NNS")) {
					for (int j = 0; j < officialObjects.size(); j++) {
						if (parsedString.get(i).value().equals(officialObjects.get(j).getName())
								|| parsedString.get(i).value().equals(officialObjects.get(j).getPluralName())) {
							actionObject = officialObjects.get(j).getName();
							actionCommand = true;
							found = false;
							return ("bring");
						}
					}
				}
				for (int j = 0; j < officialObjects.size(); j++) {
					if (getOriginalTranscript().contains(officialObjects.get(j).getPluralName())) {
						actionObject = officialObjects.get(j).getName();
						actionCommand = true;
						found = false;
						return ("bring");
					} else if (getOriginalTranscript().contains(officialObjects.get(j).getName())) {
						actionObject = officialObjects.get(j).getName();
						actionCommand = true;
						found = false;
						return ("bring");
					}
				}

			}

			return ("");
		}
			// call or return important parameters to the function or the
			// function calling

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
				// i.retrieveName(parsedString);
				// return ("Your name is " + i.getName());
				return ("");
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
						if (parsedString.get(i).value().equals(officialObjects.get(j).getName())
								|| parsedString.get(i).value().equals(officialObjects.get(j).getPluralName())) {
							return ("The location of the " + officialObjects.get(j).getName() + " is "
									+ officialObjects.get(j).getLocation());
						}
					}
				}
			}
			for (int i = 0; i < officialObjects.size(); i++) {
				if (getOriginalTranscript().contains(officialObjects.get(i).getPluralName())) {
					return ("The location of the " + officialObjects.get(i).getPluralName() + " is "
							+ officialObjects.get(i).getLocation());
				} else if (getOriginalTranscript().contains(officialObjects.get(i).getName())) {
					return ("The location of the " + officialObjects.get(i).getName() + " is "
							+ officialObjects.get(i).getLocation());
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
		case ("largest"): {

		}
		case ("biggest"): {
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")
						|| parsedString.get(i).tag().equals("NNP")) {
					for (int j = 0; j < categoryList.size(); j++) {
						if (parsedString.get(i).value().equals(categoryList.get(j))) {

							return ("The largest " + largeCat.get(parsedString.get(i).value()).getCategory()
									+ " is the " + largeCat.get(parsedString.get(i).value()).getName()
									+ ", which is about " + largeCat.get(parsedString.get(i).value()).getSize()
									+ " centimetres tall");
						}
					}

				}
			}
		}
		case ("smallest"): {
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")
						|| parsedString.get(i).tag().equals("NNP")) {
					for (int j = 0; j < categoryList.size(); j++) {
						if (parsedString.get(i).value().equals(categoryList.get(j))) {
							return ("The smallest " + smallCat.get(parsedString.get(i).value()).getCategory()
									+ " is the " + smallCat.get(parsedString.get(i).value()).getName()
									+ ", which is about " + smallCat.get(parsedString.get(i).value()).getSize()
									+ " centimetres.");
						}
					}

				}
			}
		}
		case ("lightest"): {
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")
						|| parsedString.get(i).tag().equals("NNP")) {
					for (int j = 0; j < categoryList.size(); j++) {
						if (parsedString.get(i).value().equals(categoryList.get(j))) {
							return ("The lightest" + lightCat.get(parsedString.get(i).value()).getCategory()
									+ " is the " + lightCat.get(parsedString.get(i).value()).getName()
									+ ", which weighs about " + lightCat.get(parsedString.get(i).value()).getWeight()
									+ " grams.");
						}
					}

				}
			}
		}
		case ("heaviest"): {
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")
						|| parsedString.get(i).tag().equals("NNP")) {
					for (int j = 0; j < categoryList.size(); j++) {
						if (parsedString.get(i).value().equals(categoryList.get(j))) {
							return ("The heaviest " + heavyCat.get(parsedString.get(i).value()).getCategory()
									+ " is the " + heavyCat.get(parsedString.get(i).value()).getName()
									+ ", which weighs about " + heavyCat.get(parsedString.get(i).value()).getWeight()
									+ " grams.");
						}
					}

				}
			}

		}

			// fall-through
		case ("much"): {

		}
		case ("many"): {
			boolean found = false;
			/*
			 * In case for open challenge for (int i = 0; i <
			 * officialObjectsNames.size(); i++) { if
			 * (getOriginalTranscript().contains(officialObjectsNames.get(i))) {
			 * found = true; actionObject = officialObjectsNames.get(i);
			 * actionCommand = true;
			 * 
			 * return ("");
			 * 
			 * }
			 * 
			 * }
			 */
			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")) {
					for (int j = 0; j < officialObjects.size(); j++) {
						if (parsedString.get(i).value().equals(officialObjects.get(j).getName())
								|| parsedString.get(i).value().equals(officialObjects.get(j).getPluralName())) {
							if (officialObjects.get(j).getCount() == 1) {
								return ("There is " + officialObjects.get(j).getCount() + " "
										+ officialObjects.get(j).getName());
							} else {
								return ("There are " + officialObjects.get(j).getCount() + " "
										+ officialObjects.get(j).getPluralName());
							}
						}
					}
				} else if (parsedString.get(i).tag().equals("NNS")) {
					for (int j = 0; j < locationList.length; j++) {

						if (getOriginalTranscript().toLowerCase().contains(locationList[j])) {
							for (int j2 = 0; j2 < officialObjects.size(); j2++) {
								if (parsedString.get(i).value().equals(officialObjects.get(j2).getName())
										|| parsedString.get(i).value()
												.equals(officialObjects.get(j2).getPluralName())) {

									if (officialObjects.get(j2).getLocation().contains(locationList[j])) {
										if (officialObjects.get(j2).getCount() == 1) {
											return ("There is " + officialObjects.get(j2).getCount() + " "
													+ officialObjects.get(j2).getName() + " "
													+ officialObjects.get(j2).getLocation());
										} else {
											return ("There are " + officialObjects.get(j2).getCount() + " "
													+ officialObjects.get(j2).getPluralName() + " "
													+ officialObjects.get(j2).getLocation());
										}
									} else {
										return ("There are no " + officialObjects.get(j2).getPluralName());
									}
								}
							}
						}
					}
				}
			}

			// if parts of the sentence weren't in the official objects list
			if (!found) {
				boolean foundCrowd = false;
				boolean foundMale = false;
				boolean foundFemale = false;
				for (int i = 0; i < parsedString.size(); i++) {
					if (parsedString.get(i).value().toLowerCase().equals("male")
							|| parsedString.get(i).value().toLowerCase().equals("males")
							|| parsedString.get(i).value().toLowerCase().equals("men")
							|| parsedString.get(i).value().toLowerCase().equals("man")) {
						foundMale = true;
					} else if (parsedString.get(i).value().toLowerCase().equals("female")
							|| parsedString.get(i).value().toLowerCase().equals("females")
							|| parsedString.get(i).value().toLowerCase().equals("women")
							|| parsedString.get(i).value().toLowerCase().equals("woman")) {
						foundFemale = true;
					}
				}
				// if male and not female are in sentence
				if (foundMale && !foundFemale) {
					foundCrowd = true;
					actionObject = "countMale";
					actionCommand = true;
					// if female and not male are in sentence
				} else if (!foundMale && foundFemale) {
					foundCrowd = true;
					actionObject = "countFemale";
					actionCommand = true;
					// if male and female are in sentence
				} else if (foundMale && foundFemale) {
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
				} else if (getOriginalTranscript().contains("lay") || getOriginalTranscript().contains("lying")) {
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

			// For Magdeburg
			if (getOriginalTranscript().toLowerCase().contains("dinner")
					&& getOriginalTranscript().toLowerCase().contains("table")) {
				actionObject = "dinnerTable";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("cabinet")) {
				actionObject = "cabinet";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("bookshelf")
					|| getOriginalTranscript().toLowerCase().contains("bookshelves")) {
				actionObject = "bookshelf";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if ((getOriginalTranscript().toLowerCase().contains("kitchen")
					&& (!getOriginalTranscript().toLowerCase().contains("counter")))) {
				actionObject = "kitchen";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if ((getOriginalTranscript().toLowerCase().contains("kitchen")
					&& getOriginalTranscript().toLowerCase().contains("counter"))
					|| getOriginalTranscript().toLowerCase().contains("kitchencounter")) {
				actionObject = "kitchencounter";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if ((getOriginalTranscript().toLowerCase().contains("sofa")
					|| getOriginalTranscript().toLowerCase().contains("couch"))
					&& !(getOriginalTranscript().toLowerCase().contains("table"))) {
				actionObject = "sofa";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("couch")
					&& getOriginalTranscript().toLowerCase().contains("table")) {
				actionObject = "couchTable";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("side")
					&& getOriginalTranscript().toLowerCase().contains("table")) {
				actionObject = "sideTable";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("dining")
					&& getOriginalTranscript().toLowerCase().contains("room")) {
				actionObject = "diningRoom";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("living")
					&& getOriginalTranscript().toLowerCase().contains("room")) {
				actionObject = "livingRoom";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("stove")) {
				actionObject = "stove";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("bed")) {
				actionObject = "bed";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("closet")) {
				actionObject = "closet";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("desk")) {
				actionObject = "desk";
				actionCommand = true;
				found = false;
				return ("goto");
			} else if (getOriginalTranscript().toLowerCase().contains("bar")) {
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
			// how big is something?
		case ("big"): {

			for (int i = 0; i < parsedString.size(); i++) {
				if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")) {
					for (int j = 0; j < officialObjects.size(); j++) {
						if (parsedString.get(i).value().equals(officialObjects.get(j).getName())
								|| parsedString.get(i).value().equals(officialObjects.get(j).getPluralName())) {
							return ("The size of the " + officialObjects.get(j).getName() + " is about "
									+ officialObjects.get(j).getSize() + " centimetres.");
						}
					}
				}
			}

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
							if (parsedString.get(i).value().toLowerCase().equals(officialObjects.get(j).getName())
									|| parsedString.get(i).value().equals(officialObjects.get(j).getPluralName())) {
								return ("The color of the " + officialObjects.get(j).getName() + " is "
										+ officialObjects.get(j).getColor());
							}
						}
					}
				}
			} else if (getOriginalTranscript().contains("size")) {
				for (int i = 0; i < parsedString.size(); i++) {
					if (parsedString.get(i).tag().equals("NN") || parsedString.get(i).tag().equals("NNP")) {
						for (int j = 0; j < officialObjects.size(); j++) {
							if (parsedString.get(i).value().equals(officialObjects.get(j).getName())
									|| parsedString.get(i).value().equals(officialObjects.get(j).getPluralName())) {
								return ("The size of the " + officialObjects.get(j).getName() + " is about "
										+ officialObjects.get(j).getSize() + " centimetres.");
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
