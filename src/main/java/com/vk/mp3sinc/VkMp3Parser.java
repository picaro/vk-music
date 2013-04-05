package com.vk.mp3sinc;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

/**
 * 
 * @author picaro
 *
 */
public class VkMp3Parser implements IVkMp3Parser{

	//private Logger log = new Logger(this.getClass());
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	
	 /**
	  * replace illegal characters in a filename with "_"
	  * illegal characters :
	  *           : \ / * ? | < >
	  * @param name
	  * @return
	  */
	  public static String sanitizeFilename(String name) {
		  if (name.length() > 240) name = name.substring(0,240) ;
		  name = name + ".mp3";
		  return name.replaceAll("[:\\\\/*?\"|<>]", "_");
	  }
	  
	/**
	 * Parse all mp3s from the page
	 */
	public Map<String,String> getAllMp3s(String mp3page) {
		Hashtable<String,String> mp3s = new Hashtable<String,String>();

		//lower case?
		System.out.println("mp" );
		
		int i = 0;
		while(mp3page.indexOf("class=\"play_btn") > 0 && i++ < 1000){
			int startCutPos = mp3page.indexOf("class=\"play_btn");
			mp3page = mp3page.substring(startCutPos);
			
			int indexmp = mp3page.indexOf(".mp3") + 4;
			if (indexmp < 15) continue;
			String mp3 = mp3page.substring(0,indexmp);
			mp3 = mp3.substring(mp3.lastIndexOf("http://"));
			
			mp3page = mp3page.substring(mp3page.indexOf("<div class=\"title_wrap"));
			
			indexmp = mp3page.indexOf("</div>") + 6;
			String mp3name = Jsoup.parse(mp3page.substring(0,indexmp)).text();
			mp3page = mp3page.substring(indexmp);

			
			mp3 = mp3 + "?/" + mp3name;

			mp3s.put(mp3name,mp3);				
			System.out.println("mp3:" + mp3);
		}
		return mp3s;
	}

}
