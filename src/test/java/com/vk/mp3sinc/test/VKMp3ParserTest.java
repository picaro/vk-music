/**
 * 
 */
package com.vk.mp3sinc.test;

import static org.junit.Assert. *;

import java.util.ArrayList;
import java.util.Map;

import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;

import com.vk.mp3sinc.VkMp3Parser;

/**
 * Test parser
 * @author picaro
 *
 */
public class VKMp3ParserTest {

	private static String PAGE1 = " ewrewre er er wr wr wer>  wer wer  http://sss/aaaa.mp3  erer er er er http://erer.mp3 ere r";
	
	private static String PAGE2 = 
	"<table cellspacing=\"0\" cellpadding=\"0\"> " +
	"<tbody><tr>" +
	"<td class=\"play_btn\">" +
	"<a onclick=\"playAudioNew(&#39;6220607_118004365&#39;)\"><div class=\"play_new\" id=\"play6220607_118004365\"></div></a>" +
	"<input type=\"hidden\" id=\"audio_info6220607_118004365\" value=\"http://cs4623.vkontakte.ru/u55655941/audio/fa12606d45de.mp3,194\" parsed=\"1\">" +
	"</td>" +
	"<td class=\"info\">" +
	"  <div class=\"duration fl_r\" id=\"dur6220607_118004365\">3:14</div>" +
    " <div class=\"title_wrap\"><b><a href=\"http://vkontakte.ru/search?c[q]=Bahh%20Tee&c[section]=audio\" onclick=\"if (checkEvent(event)) return; Audio.selectPerformer(event, &#39;Bahh Tee&#39;); return false\">Bahh Tee</a></b> - <span class=\"title\">Ненавидь меня (SunJinn prod.) </span><span class=\"user\"></span></div>" +
    "</td>" +
    "<td><a id=\"6220607_118004365\" href=\"http://cs4623.vkontakte.ru/u55655941/audio/fa12606d45de.mp3?/Bahh%20Tee%20-%20%CD%E5%ED%E0%E2%E8%E4%FC%20%EC%E5%ED%FF%20(SunJinn%20prod.).mp3\" class=\"downloadbtn\" title=\"Для загрузки перетащите мышкой" +
    " иконку в нужную папку\" download=\"Bahh Tee - Ненавидь меня (SunJinn prod.).mp3\" parsed=\"1\"><img src=\"chrome-extension://hanjiajgnonaobdlklncdjdmpbomlhoa/res/download.gif\" width=\"16\" height=\"17\"\";";
	
	//play_btn     mp3.
	//info       title_wrap div
	// /td
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.vk.mp3sinc.VkMp3Parser#getAllMp3s(java.lang.String)}.
	 */
	@Test
	public void testGetAllMp3s() {
		VkMp3Parser vkp = new VkMp3Parser();
		Map<String,String> mp3s = vkp.getAllMp3s(PAGE2);
		assertEquals(1,mp3s.size());
		assertEquals("http://cs4623.vkontakte.ru/u55655941/audio/fa12606d45de.mp3",mp3s.get("Bahh Tee - Ненавидь меня (SunJinn prod.)"));
		
	}

}
 