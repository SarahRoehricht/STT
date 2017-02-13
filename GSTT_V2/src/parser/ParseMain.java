package parser;

import java.util.ArrayList;

import edu.stanford.nlp.ling.TaggedWord;

public class ParseMain {

	public static void main(String[] args) {

		String str = "This house is rather nice.";

		Parser p = new Parser();
		ArrayList<TaggedWord> parsedString = p.parse(str);

		// syso
		System.out.println(parsedString);
		System.out.println();
		System.out.println(".Value() returns word");
		System.out.println(parsedString.get(0).value());
		System.out.println();
		System.out.println(".tag() returns tag");
		System.out.println(parsedString.get(0).tag());
		System.out.println();

		// Tagged Word into String
		String taggedWord = parsedString.get(0).toString();
		System.out.println(taggedWord);

	}

}
