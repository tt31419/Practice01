package com.tt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

// import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.gargoylesoftware.htmlunit.BrowserVersion;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlForm;
//import com.gargoylesoftware.htmlunit.html.HtmlInput;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLDivElement;

public class JsonReader {

  private static String readAll(final Reader rd) throws IOException {
      final StringBuilder sb = new StringBuilder();
      int cp;
      while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
      }
      return sb.toString();
  }
  
  //改成用StringBuilder 之後要取中間內容
  private static StringBuilder readAll_StringBuilder(final Reader rd) throws IOException {
      final StringBuilder sb = new StringBuilder();
      int cp;
      while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
      }
      return sb;
  }

  public static JSONObject readJsonFromUrl(final String url) throws IOException, JSONException {
      final InputStream is = new URL(url).openStream();
      try {
          //final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
          final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("gbk")));
          
          final String jsonText = readAll(rd);
          System.out.println(jsonText);
          final JSONObject json = new JSONObject(jsonText);
          return json;
      } finally {
          is.close();
      }
  }
  
  //
  public static StringBuilder readFromUrl(final String url) throws IOException, JSONException {
      final InputStream is = new URL(url).openStream();
      try {
    	  //System.out.println("is = " + is.toString());
    	 // System.out.println("is = " + is.);
    	  //final BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    	  final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
          //final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("gbk")));
    	  final StringBuilder jsonText = readAll_StringBuilder(rd);
          System.out.println(jsonText);
          
          
          return jsonText;
      } finally {
          is.close();
      }
  }
  

  public static void main(String[] args) throws IOException, JSONException {
      
	      //<div class="bookread-content-box">
	      //<br /><br />　　
	      
	      //</div>
	      
	      
	  //final StringBuilder nowSb;
//	  WebClient webClient = new WebClient();
//	  
//	  String START_URL = "";
//		        try {
//		            webClient = new WebClient(BrowserVersion.CHROME);
//		            HtmlPage page = webClient.getPage(START_URL);
//		            webClient.getOptions().setJavaScriptEnabled(false);
//		            webClient.getOptions().setCssEnabled(false);
//		            webClient.waitForBackgroundJavaScript(10000);
		           /* 
		            try {
						webClient.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		            */
		            //System.out.println("toString = " + webClient.toString());
		            
		            //System.out.println("page = " + page.getFullyQualifiedUrl(null)/*asText());
		            //System.out.println(webClient.getPage(url)());
		            
		            
		    		//The first preceding input that is not hidden
		            //HtmlInput mouse = page.getFirstByXPath("//input[@class='bookread-content-box']");

		         //   mouse.mouseOver();
		            //mouse.getAccept();
		           //page.refresh();
		            //page.isAttachedToPage();
		            //mouse.click();
		            /*
		            
		            HtmlInput inputPassword = page.getFirstByXPath("//input[@type='password']");
		            HtmlInput inputLogin = inputPassword.getFirstByXPath("//preceding::input[@class='username']");
		    		
		            inputLogin.setValueAttribute("davidfinal25@gmail.com");
		    		inputPassword.setValueAttribute("davidfinal25@gmail.com222");
		    	
		    		//get the enclosing form
		    		HtmlForm loginForm = inputPassword.getEnclosingForm();
		    		*/
		    		//System.out.println("toString = " + webClient.getOptions().);
		            
		            //System.out.println("toString = " + page.asXml() + "---" + page.isAttachedToPage());
		            
		          //  System.out.println("toString = " + page.getBody().getTextContent());
		            //div[@class='bookread-content-box']"
		          //  String[] parts = page.asXml().split(regex);
		            //StringBuilder sb = new Stringbuilder());
		           // System.out.println("toString = " + mainStr.getInnerText());
		            
//		        } catch (IOException ex ) {
//		        	
//		            ex.printStackTrace();
//		       
//				} finally {
//		        	webClient.close();
//		        }
	  
	 // String JSON = "";123
	  //JSONArray jsonArray = new JSONArray(JSON);
	  //JSONObject json = new JSONObject(JSON);
	  //final JSONObject json = readJsonFromUrl("https://graph.facebook.com/19292868552");
      
    //System.out.println(nowSb.toString());
    //System.out.println(jsonArray.get("id"));
  }
}