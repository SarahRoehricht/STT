package decision;

import parser.Parser;
import java.util.ArrayList;

import edu.stanford.nlp.ling.TaggedWord;

public class DecisionMain {

	public static void main(String[] args) {

		String str = "Bring me the toaster on the table.";
		Parser p = new Parser();

		ArrayList<TaggedWord> parsedString = p.parse(str);

		Decision d = new Decision();

		d.decide(parsedString);

	}

}
