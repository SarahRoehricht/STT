package parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class Parser {

	public Parser() {

	}

	public ArrayList<TaggedWord> parse(String str) {
//		System.out.println(str);
//		System.out.println();

		// initialize parser
		String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		TreebankLanguagePack tlp = lp.getOp().langpack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

		// Tokenize String
		Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(str));
		List<? extends HasWord> StringTokenized = toke.tokenize();

		// parsing tokenized String
		Tree parse = lp.parse(StringTokenized);
//		parse.pennPrint();
//		System.out.println();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
//		System.out.println(tdl);
//		System.out.println();

//		System.out.println("The words of the sentence:");
//		for (Label lab : parse.yield()) {
//			if (lab instanceof CoreLabel) {
//				System.out.println(((CoreLabel) lab).toString(CoreLabel.OutputFormat.VALUE_MAP));
//			} else {
//				System.out.println(lab);
//			}
//		}

//		System.out.println();
//		System.out.println("tagged words:");
//		 System.out.println(parse.taggedYield());
//		System.out.println();

//		lp.parse(str).pennPrint();

		// returns ArrayList<TaggedWord> for processing
		ArrayList<TaggedWord> output = parse.taggedYield();
		return output;

	}

}
