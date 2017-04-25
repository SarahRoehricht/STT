package answerQ;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class AnswerAPI {

	public AnswerAPI(){
		
	}
	
	//get's utterance and asks wolframAlphaAPI for answer
	public String answerQuestion(String question) throws UnsupportedEncodingException, MalformedURLException{
		//APIKey for Wolfram Alpha API
		String APIKey="9PAKG9-KP7WHG755P";
		try{
		String URLTarget="http://api.wolframalpha.com/v2/query";
		String parameters="?appid="+APIKey+"&query="
				+URLEncoder.encode(question, "UTF-8")+"&output=xml&includepodid=Result&format=plaintext";
		
		
		String request=URLTarget+"?appid="+APIKey+"&input="
				+URLEncoder.encode(question, "UTF-8");
		
		
		URL url=new URL(request);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Content-Type", 
		        "application/x-www-form-urlencoded");
//		 connection.setRequestProperty("Content-Length", 
//			        Integer.toString(question.getBytes().length));
			    connection.setRequestProperty("Content-Language", "en-US");  
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(60000);
		connection.setUseCaches(false);
		
		//request
		 DataOutputStream wr = new DataOutputStream (
			        connection.getOutputStream());
			    wr.writeBytes(parameters);
			    wr.close();
			    
			    
			    
			    //input
			    InputStream is = connection.getInputStream();
			    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			    StringBuffer response = new StringBuffer(); // or StringBuffer if Java version 5+
			    String line;
			    while ((line = rd.readLine()) != null) {
			      response.append(line);
			      response.append('\r');
			    }
			    rd.close();
			    
			    String result =response.toString();
			    
			    
			//xml parse
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(result)));
			Element rootElement = document.getDocumentElement();
			//look for plaintext element
			String requestPlaintext=getString("plaintext", rootElement);
			
			System.out.println(requestPlaintext);
			//check if parsed request answer is null
			    if(requestPlaintext==null){
			    	return "";
			    }
			    //replaces {,},[,] with empty char, to ensure emotionparser working correctly.
			    requestPlaintext=requestPlaintext.replace("{", "");
			    requestPlaintext=requestPlaintext.replace("}", "");
			    requestPlaintext=requestPlaintext.replace("[", "");
			    requestPlaintext=requestPlaintext.replace("]", "");
			    if(requestPlaintext.contains("data not available")){
			    	return "Sorry, i don't have any data on this. [:-(]";
			    }
			   // requestPlaintext=requestPlaintext.replaceAll("\\(.*\\)", "");
			    requestPlaintext=requestPlaintext.trim();
			    
			    if(requestPlaintext.length()>=200){
			    	requestPlaintext=requestPlaintext.substring(0, 199);
			    }
			    return "I think the answer is " + requestPlaintext;
			    
	
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	//getter ensuring, list is not null, and subList is not null;
	protected String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }
}
