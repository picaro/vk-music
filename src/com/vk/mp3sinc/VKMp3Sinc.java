package com.vk.mp3sinc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.text.JTextComponent;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;


/**
 * Vkontakte mp3 sinchronizer
 * @author picaro
 *
 */
public final class VKMp3Sinc extends JPanel  implements ActionListener {

	public static boolean running = false;
	public static int counttodownload = 0;
	public static int downloaded = 0;


	public final static String fileSettings = "vkmp3sinc.ini";

	private static Ini prefs = null;
	
	public static String FILE_PATH = "filepath";
	public static String LOGIN = "login";
	public static String PASSWORD = "password";
	public static String SINC_DIR = "sincdir";
	
	
	public static String sincdir;
	public static String password;
	public static String login;
	public static String filepath;
	
    public final static Dimension hpad10 = new Dimension(10,1);
    public final static Dimension vpad20 = new Dimension(1,20);
    public final static Dimension vpad7 = new Dimension(1, 7);
    public final static Dimension vpad4 = new Dimension(1, 4); 
	
    JTextField mp3catalogField;  
    
    JTextField filePathField;  

    JTextField loginField;  

    JTextField passwordField;  
    
    JButton showButton; 
    
    public JProgressBar getmProgressBar() {
		return mProgressBar;
	}

	public void setmProgressBar(JProgressBar mProgressBar) {
		this.mProgressBar = mProgressBar;
	}


	public JButton getShowButton() {
		return showButton;
	}

	public void setShowButton(JButton showButton) {
		this.showButton = showButton;
	}


	private JProgressBar mProgressBar;

    /**
     * Create an editor to represent the given document.  
     */
    protected JTextComponent createEditor() {
    	JTextComponent c = new JTextArea();
    	c.setDragEnabled(true);
    	c.setFont(new Font("monospaced", Font.PLAIN, 12));
    	return c;
    }
	
	public VKMp3Sinc(){
		// Force SwingSet to come up in the Cross Platform L&F
		try {
		    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		    // If you want the System L&F instead, comment out the above line and
		    // uncomment the following:
		    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exc) {
		    System.err.println("Error loading L&F: " + exc);
		} 
		
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout()); 

		JScrollPane scroller = new JScrollPane(); 
		JViewport port = scroller.getViewport();
		JTextComponent editor = createEditor();
		port.add(editor);
		try {
		    String vpFlag = "ViewportBackingStore";
		    Boolean bs = Boolean.valueOf(vpFlag);
		    port.setBackingStoreEnabled(bs.booleanValue());
		} catch (MissingResourceException mre) {
		    // just use the viewport default
		} 
		
		// Create show button
		showButton = new JButton("Download all");
		showButton.addActionListener(this);
	    showButton.setMnemonic('s'); 
		
		mp3catalogField = new JTextField(8) {
			public Dimension getMaximumSize() {
			return new Dimension(getPreferredSize().width, getPreferredSize().height);
		    }
		};
		mp3catalogField.setText(sincdir);
		mp3catalogField.setAlignmentY(JComponent.TOP_ALIGNMENT);
		//mp3catalogField.setEnabled(true);
		
		filePathField = new JTextField(8) {
			public Dimension getMaximumSize() {
			return new Dimension(getPreferredSize().width, getPreferredSize().height);
		    }
		};
		filePathField.setText(filepath);
		filePathField.setAlignmentY(JComponent.TOP_ALIGNMENT);
		

	    //JTextField loginField;  

	    //JTextField passwordField;  

		
		// create a radio listener to listen to option changes
		RunButtonListener runListener = new RunButtonListener(this); 
		showButton.addActionListener(runListener); 
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());	
//		panel.add("North",createEditor());
		panel.add("North",mp3catalogField);
		panel.add("South",filePathField);
		//panel.add("Center", scroller);
		//panel.add("Center", scroller);
		
		//panel.add(Box.createRigidArea(hpad10));
		
		mProgressBar = new JProgressBar();
		mProgressBar.setValue(0);
		mProgressBar.setStringPainted(true);
		JPanel panel2 = new JPanel();
		panel2.add(showButton);
		panel2.add(mProgressBar);
		
		panel.add(panel2);
		
		
		//panel.add(Box.createRigidArea(hpad10)); 
		
        //add(customField); 
		add("Center", panel);
		//add("South", createStatusbar());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("start");
		
		try{
		//String login = "alexanders-mail@rambler.ru";
		//String password = "password";
		File fil = new File(fileSettings);
		if (! fil.exists()) fil.createNewFile();
		prefs = new Ini(fil);
		Section prefsec = prefs.get("settings");
		sincdir = prefsec.get(SINC_DIR);
		login = prefsec.get(LOGIN);
		password = prefsec.get(PASSWORD);
		filepath = prefsec.get(FILE_PATH);
		//sincFile = "d:\\page.htm";
		//sincDir = "d:\\Music\\my";
		VKMp3Sinc vkMp3Sinc = new VKMp3Sinc();
		vkMp3Sinc.setUpFrames(vkMp3Sinc);
			//vkMp3Sinc.updateState(); 
	    } catch (Throwable t) {
	            System.out.println("uncaught exception: " + t);
	            t.printStackTrace();
	    } 
		
		//vkMp3Sinc.sinchronize(login, password, sincdir);
			
		//GUI ???
	}

	private void setUpFrames(VKMp3Sinc vkMp3Sinc) {
		JFrame frame;

		frame = new JFrame();
		
		frame.setTitle("Музыку в мп3! Синхронизатор ВКонтакте и мп3");
		frame.setBackground(Color.lightGray);
		frame.getContentPane().setLayout(new BorderLayout());
		    //Notepad notepad = new Notepad();
		//frame.getContentPane().add("Center", notepad);
		  //  frame.setJMenuBar(notepad.createMenubar());
		frame.getContentPane().add("Center", vkMp3Sinc); 
		frame.addWindowListener(new AppCloser());
		frame.pack();
		frame.setSize(500, 300);
		frame.setVisible(true);
	}

	private void sinchronize(String filepath, String login, String password, String sincdir) {
		//login - password
		//get page
		String mp3page = VKNetwork.getVKMP3PageByFile(filepath);
		
		try {
			HttpClient client = new HttpClient();
			client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
//			// Теперь составим наш первый запрос и сразу же выполним его:
//			PostMethod first = new PostMethod("http://vkontakte.ru/login.php");
//			first.addParameter("act", "login");
//			first.addParameter("try_to_login", "1");
//			first.addParameter("email", "alexanders-mail@rambler.ru");
//			first.addParameter("pass", "password135");
//			client.executeMethod(first);
//			
//			Source tmp_src = new Source(post.getResponseBodyAsStream());
//			Element tmp_elem = tmp_src.getElementById("s"); //Получаем элемент с id s
//			String s = tmp_elem.getAttributeValue("value"); //Получаем его значение.
//			//Теперь нам осталось создать и выполнить второй запрос:
//			PostMethod login2 = new PostMethod("http://vkontakte.ru/login.php");
//			login2.addParameter("s", s);
//			login2.addParameter("op", "slogin");
//			login2.addParameter("redirect", "1");
//			login2.addParameter("expire", "0");
//			client.executeMethod(login2);
			
//			
//		    //String inputURL = "http://login.vk.com/?act=login";
//		    String inputURL = "http://login.vk.com/?act=login&q=1&al_frame=1&from_host=vkontakte.ru&email=alexanders-mail@rambler.ru&pass=password135";
//			Prowser prowser = new Prowser();
//		    Tab tab = prowser.createTab();
//		   // tab.getProwser().set
//		    
//		    Request request = new Request(inputURL);
////		    request.addParameter("email","alexanders-mail@rambler.ru");
////		    request.addParameter("pass","password135");
////		    request.setHttpMethod("POST");
////		    request.addParameter("act","login");
////		    request.addParameter("q","1");
////		    request.addParameter("al_frame","1");
////		    request.addParameter("pass","password135");
//		    
//		    Response resp = tab.go(request); 
//		    String html = resp.getPageSource();
//
//		    
//		    inputURL = "http://vkontakte.ru/id6220607";
//		    request = new Request(inputURL);
//		     resp = tab.go(request); 
//		    mp3page =  resp.getPageSource();
//		    if (mp3page.length() < 1000) return;
		    //OutputStream out = new FileOutputStream(sincdir);
	//	} catch (URISyntaxException e) {
		//	e.printStackTrace();
		} finally {
			
		}
	    	    
		//https://login.vk.com/?act=login&email=alexanders-mail@rambler.ru&pass=password135
		

		VKNetwork vkNetwork = new VKNetwork(sincdir, mp3page, this);
		vkNetwork.start();// downloadAll();
	}


	


	
    /**
     * To shutdown when run as an application.  This is a
     * fairly lame implementation.   A more self-respecting
     * implementation would at least check to see if a save
     * was needed.
     */
    protected final class AppCloser extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
        	Section prefsec = prefs.get("settings");
        	if (prefsec == null) prefsec = prefs.add("settings");
        	prefsec.put(FILE_PATH, filePathField.getText());
        	prefsec.put(SINC_DIR, mp3catalogField.getText());
        	File output = new File(fileSettings);
        	try {
				prefs.store(output);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    System.exit(0);
        }
    }


	public void actionPerformed(ActionEvent e) {
		System.out.println("event 1!" + mp3catalogField.getDocument().getLength());
		
	}
	
	
    /** An ActionListener that listens to the radio buttons. */
    class RunButtonListener implements ActionListener {
    	
    	VKMp3Sinc vkMp3Sinc = null;
    	
		public RunButtonListener(VKMp3Sinc vkMp3Sinc) {
			this.vkMp3Sinc = vkMp3Sinc;
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println("event!" + mp3catalogField.getDocument().getLength());
		    JComponent c = (JComponent) e.getSource();
		    if(c == showButton) {
		       String sincFile = filePathField.getText();
		       String sincDir = mp3catalogField.getText();
			   vkMp3Sinc.sinchronizeByFile(sincFile, sincDir);
		    }

		}
    }


	public void sinchronizeByFile(String sincFile, String sincDir) {
		sinchronize(sincFile, null,null, sincDir);
	} 
}
