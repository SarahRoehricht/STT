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

public class Decision
{
	HashSet<String> hs;
	Interact i = new Interact();
	ActionCommand action = new ActionCommand(); // parameter = command

	private String toTTS = "";
	private String originalTranscript = "";
	private AnswerAPI ans;

	public Decision()
	{
		hs = new HashSet<String>(Arrays.asList("team", "robocup", "at home", "time", "date", "bring", "give", "hello", "greetings", "hi", "howdy", "hey", "bonjour", "hallo", "go", "name",
				"joke", "follow"));

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
	
	public void decide(ArrayList<TaggedWord> parsedString)
	{
		boolean match = false;
		// match dictionary keywords with TaggedWord values,
		// return action value/command/call next function else look for answer
		if (getOriginalTranscript().contains("how are you"))
		{
			i.interaction(1);
			setToTTS(i.getReplyInteract());
			match=true;
		} 
//		else if (getOriginalTranscript().contains("last question") | getOriginalTranscript().contains("previous question"))
//		{
//			//get previous question
//			setToTTS("The previous question is " + );
//		}
		else
		{
			for (TaggedWord taggedWord : parsedString)
			{

				if (hs.contains(taggedWord.value().toLowerCase()))
				{
					// call the dictionary parser method with the matched Word, and
					// the parsedString
					System.out.println(taggedWord + " is in here.");

					match = true;

					// looks for "how are you"
					// if(taggedWord.value().toLowerCase().equals("how")){
					// for (int j = 0; j < parsedString.size(); j++)
					// {
					// if (parsedString.get(j).value().equals("how") && parsedString.get(j+1).value().equals("are")&&
					// parsedString.get(j+2).value().equals("you") )
					// {
					// i.interaction(1);
					// setToTTS(i.getReplyInteract());
					// }
					// }
					// }
					
					
							
							String strReturn=matchdecide(taggedWord, parsedString);
								if(!strReturn.isEmpty()){
									setToTTS(strReturn);
								}else{
									setToTTS("");
								}
									
					break;
				}
			}
		}
		if (!match)
		{
			setToTTS("");

		}
	}

	private String matchdecide(TaggedWord match, ArrayList<TaggedWord> parsedString)
	{

		// System.out.println(match);
		// System.out.println(parsedString);

		// simplify match String and bring match into lowercase
		String simpleMatch = simplifyMatch(match.value());

		// executes the corresponding code with the match
		switch (simpleMatch)
		{

		// case to bring something
		// extendable with adjective e.g. 'the' 'blue' 'book'
		case ("bring"):
		{
			String DT = "";
			String object = "";
			for (int i = 0; i < parsedString.size(); i++)
			{

				if (parsedString.get(i).tag().equals("NN"))
				{
					object = parsedString.get(i).value();
					if (parsedString.get(i - 1).tag().equals("DT"))
					{
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

		case ("give"):
		{
			System.out.println("I would give you something if i could.");
			return ("I would give you something if i could.");

		}

		case ("hello"):
		{
			i.interaction(0);
			return (i.getReplyInteract());
			// if (parsedString.size() < 5) {
			// System.out.println("I would say hello if i could.");
			// // CallTTS-Module or Return Command ID for saying hello
			// }
		}

		case ("name"):
		{
			if(getOriginalTranscript().contains("team")){
				i.questionAboutTeam(getOriginalTranscript());
				return (i.getReplyInteract());
			}
			else if(
				getOriginalTranscript().contains("teams")){
				i.questionAboutTeam(getOriginalTranscript());
				return (i.getReplyInteract());	
				}
			
			else if (getOriginalTranscript().contains("your"))
			{
				return ("My name is Leonie.[:-)]");
			} else
			{
				i.retrieveName(parsedString);
				return ("Your name is " + i.getName());
			}
		}

		case ("joke"):
		{
			i.interaction(2);
			return (i.getReplyInteract());
		}

		case ("team"):
		{
			i.questionAboutTeam(getOriginalTranscript());
			return (i.getReplyInteract());
		}

		case ("teams"):
		{
			i.questionAboutTeam(getOriginalTranscript());
			return (i.getReplyInteract());
		}
		
		case ("robocup"):
		{
			return ("Robocup at home is founded in the year 2006");
		}
		
		case ("at home"):
		{
			return ("Robocup at home is founded in the year 2006");
		}
		
		//alternative way to get date and time, can get it from Wolfram Alpha 
		case ("date"):
		{
			String date = new SimpleDateFormat("EEEEE, MMMM dd, yyyy", Locale.US).format(new Date());
			return ("Today is " + date);
		}
		
		case ("time"):
		{
			String time = new SimpleDateFormat("h:mm a, zzzz", Locale.US).format(new Date());
			return ("The current time is " + time);
		}

		case ("follow"):
		{
			action.followPerson();
			return ("I will follow you.");
		}
		}
		return ("");

	}

	// simplifies String e.g. hey -> hello for later-used switch case
	private String simplifyMatch(String match)
	{
		HashSet<String> helloHs = new HashSet<String>(Arrays.asList("hello", "greetings", "hi", "howdy", "hey", "bonjour", "hallo"));
		if (helloHs.contains(match))
		{
			return "hello";
		}

		return match.toLowerCase();
	}

	public void lookforAnswer() throws IOException, Exception
	{
		AnswerAPI ans = new AnswerAPI();
		setToTTS(ans.answerQuestion(getOriginalTranscript()));
	}

}
