/**
 * 
 */
package com.vk.mp3sinc.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.vk.mp3sinc.VKNetwork;

/**
 * @author picaro
 *
 */
public class VKNetworkTest {

	private VKNetwork vkNetwork = new VKNetwork(null,null,null);
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.vk.mp3sinc.VKNetwork#vkLogin()}.
	 */
	@Test
	public void testVkLogin() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.vk.mp3sinc.VKNetwork#getVKMP3Page()}.
	 */

    @Ignore
	@Test
	public void testGetVKMP3Page() {
		String page = vkNetwork.getVKMP3PageByFile("d:/page.html");
		assertTrue("size!", page.length() > 10);
	}

	/**
	 * Test method for {@link com.vk.mp3sinc.VKNetwork#download(java.lang.String, java.lang.String)}.
	 */
    @Ignore
	@Test
	public void testDownload() {
		String inputURL = 
			"http://127.0.0.1:4444/stream/52/Adele-RollingInTheDeepDirtyNoiseMeetsTheDubstepRemix";
		String filename = "d:\\a.mp3";
		File file = new File(filename);
		file.delete();
		vkNetwork.download(inputURL, filename);
		file = new File(filename);
		assertTrue(file.exists()); 
	}

}
