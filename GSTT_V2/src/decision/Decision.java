package decision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import edu.stanford.nlp.ling.TaggedWord;

public class Decision {
	HashSet<String> hs;

	public Decision() {
		hs = new HashSet<String>(
				Arrays.asList("bring", "give", "hello", "greetings", "hi", "howdy", "hey", "bonjour", "hallo"));

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
				matchdecide(taggedWord, parsedString);
				break;

			}

		}
		if (!match) {
			// call the look for answer API
			lookforAnswer();

		}
	}

	private void matchdecide(TaggedWord match, ArrayList<TaggedWord> parsedString) {

		System.out.println(match);
		System.out.println(parsedString);

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
			// call or return important parameters to the function or the
			// function calling
			break;
		}

		case ("give"): {
			System.out.println("I would give you something if i could.");

			break;
		}

		case ("hello"): {

			if (parsedString.size() < 5) {
				System.out.println("I would say hello if i could.");
				// CallTTS-Module or Return Command ID for saying hello
			}
			break;
		}

		}

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

	private void lookforAnswer() {

	}
}
