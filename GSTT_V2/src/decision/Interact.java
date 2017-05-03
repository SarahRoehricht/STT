
package decision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import edu.stanford.nlp.ling.TaggedWord;

//interactions: greetings "hello", chat "how are you", tell a joke, retrieve name 
public class Interact
{
	private String name = " ";
	private String replyInteract = " ";
	private int randNum = 0;

	public Interact() 
	{
	}

	public String getReplyInteract()
	{
		return replyInteract;
	}

	public void setReplyInteract(String replyInteract)
	{
		this.replyInteract = replyInteract;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	//interactID 0: greetings "hello"
	//interactID 1: chat "how are you"
	//interactID 2: tell a joke
	//retrieve name
	//answer questions about the team
	public void interaction(int interactID)
	{
		switch (interactID)
		{
		case 0:
		{
			greetPeople();
			break;
		}
		case 1:
		{
			chat();
			break;
		}
		case 2:
		{
			tellJokes();
			break;
		}
		default:
		{
			greetPeople();
		}
		}
	}

	private void greetPeople()
	{
		HashSet<String> greetHS = new HashSet<String>(Arrays.asList("hello", "greetings", "hi", "howdy", "hey", "bonjour", "hallo", "good day"));
		int greet;
		greet = generateRand(greetHS.size());

		int i = 0;

		for (Object obj : greetHS)
		{
			if (i == greet)
			{
				setReplyInteract((String) obj);
				System.out.print("REPLY : " + (String) obj);
				break;
			}
			i++;
		}
	}

	private void tellJokes()
	{
		HashSet<String> jokesHS = new HashSet<String>(Arrays.asList("{Person}If you do not pay your exorcist you get repossessed.[3]",
				"{Person}I was not originally going to get a brain transplant, but then I changed my mind.[3]",
				"{Person}I would tell you a chemistry joke but I know I wouldn't get a reaction.", "{Person}Why don't programmers like nature? It has too many bugs.[3]",
				"{Person}Why don't some couples go to the gym? Because some relationships don't work out.[3]"));

		int joke;
		joke = generateRand(jokesHS.size());

		int i = 0;

		for (Object obj : jokesHS)
		{
			if (i == joke)
			{
				setReplyInteract((String) obj);
				System.out.print("REPLY : " + (String) obj);
				break;
			}
			i++;
		}

	}

	public void chat()
	{
		HashSet<String> chatHS = new HashSet<String>(
				Arrays.asList("{Person}I am fine thank you.[:-)]", "{Person}great[:-)]", "{Person}fine, thank you[:-)]", "{Person}I am fine, thank you, hope you are too[:-)]"));
		
		int talk;
		talk = generateRand(chatHS.size());

		int i = 0;

		for (Object obj : chatHS)
		{
			if (i == talk)
			{
				setReplyInteract((String) obj);
				System.out.print("REPLY : " + (String) obj);
				break;
			}
			i++;
		}
	}

	public void retrieveName(ArrayList<TaggedWord> parsedString)
	{
		String name = "";
		int count = 0;

		for (int j = 0; j < parsedString.size(); j++)
		{
			if (parsedString.get(j).tag().equals("NNP")|| parsedString.get(j).tag().equals("JJ"))
			{
				count++;
			}
		}

		for (int j = 0; j < parsedString.size(); j++)
		{

			if (parsedString.get(j).tag().equals("NNP")|| parsedString.get(j).tag().equals("JJ"))
			{
				if (count == 1)
				{
					name = parsedString.get(j).value();
					setName(name);
					System.out.println("Your name is:" + name);
					break;
				} else if (count > 1)
				{
					name = parsedString.get(j).value();
					setName(name);

					StringBuilder stringBuilder = new StringBuilder();

					String extendedName = getName();
					stringBuilder.append(extendedName);

					for (int k = 1; k < count; k++)
					{
						stringBuilder.append(" ");
						stringBuilder.append(parsedString.get(j + k).value());
					}

					String finalName = stringBuilder.toString();
					System.out.println("Your name is: " + finalName);
					setName(finalName);
					break;
				}
			}
		}
	if(name.isEmpty()&& parsedString.size()<2 &&!parsedString.isEmpty()){
		setName(parsedString.get(0).value());
	}
	}
	
	public void questionAboutTeam(String originalText)
	{
		//name, leader, name of leader, how many members, from where
		if (originalText.contains("name"))
		{
			if(originalText.contains("leader"))
			{
				setReplyInteract("Our team leader is");
			}
			else 
			{
				setReplyInteract("Our team name is RT Lions.[:-)]");
			}
		}
		else if (originalText.contains("how many")|originalText.contains("number"))
		{
			setReplyInteract("{Person}There are around 20 people in our team.[:-O]");
		}
		else if (originalText.contains("university")| originalText.contains("where"))
		{
			setReplyInteract("{Person}We are from Reutlingen University in Germany.");
		}
		else
		{
			setReplyInteract("");
		}
	}

	private int generateRand(int sizeHS)
	{
		Random random = new Random();
		randNum = random.nextInt(sizeHS);
		return randNum;
	}

}
