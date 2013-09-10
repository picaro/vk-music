package com.vk.mp3sinc;

import org.apache.http.client.ClientProtocolException;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.swixml.SwingEngine;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;

/**
 * Vkontakte mp3 sinchronizer
 *
 * @author picaro
 */
public final class VKMp3Sinc extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 13432423423L;

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
    // public static String login;
    public static String filepath;

    public final static Dimension hpad10 = new Dimension(10, 1);
    public final static Dimension vpad20 = new Dimension(1, 20);
    public final static Dimension vpad7 = new Dimension(1, 7);
    public final static Dimension vpad4 = new Dimension(1, 4);

    JTextField mp3catalogField = new JTextField();

    JTextField filePathField = new JTextField();

    JTextField loginField = new JTextField();

    JTextField passwordField = new JTextField();

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

    public VKMp3Sinc() {
        try {
            UIManager.setLookAndFeel(UIManager
                    .getCrossPlatformLookAndFeelClassName());
        } catch (Exception exc) {
            System.err.println("Error loading L&F: " + exc);
        }

        // create a radio listener to listen to option changes
        // RunButtonListener runListener = new RunButtonListener(this);
        // showButton.addActionListener(runListener);

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("start");

        try {
            // String login = "alexanders-mail@rambler.ru";
            // String password = "password";
            File fil = new File(fileSettings);
            if (!fil.exists())
                fil.createNewFile();
            prefs = new Ini(fil);
            Section prefsec = prefs.get("settings");
            sincdir = prefsec.get(SINC_DIR);
            // login = prefsec.get(LOGIN);
            password = prefsec.get(PASSWORD);
            filepath = prefsec.get(FILE_PATH);

            VKMp3Sinc vkMp3Sinc = new VKMp3Sinc();
            vkMp3Sinc.setUpFrames(vkMp3Sinc);

        } catch (Throwable t) {
            System.out.println("uncaught exception: " + t);
            t.printStackTrace();
        }

        // vkMp3Sinc.sinchronize(login, password, sincdir);

        // GUI ???
    }

    private void setUpFrames(VKMp3Sinc vkMp3Sinc) {

        try {
            new SwingEngine(this).render("main.xml").setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mp3catalogField.setText(sincdir);
        filePathField.setText(filepath);
        mProgressBar = new JProgressBar();
        mProgressBar.setValue(0);
        mProgressBar.setStringPainted(true);

    }

    private void sinchronize(String filepath, String login2, String password,
                             String sincdir) throws ClientProtocolException, IOException,
            URISyntaxException {
        // login - password
        // get page
        Calendar dModified = null;

        //	while (true) {
        File fmp3 = new File(filepath);
        Calendar newDModified = Calendar.getInstance();
        newDModified.setTimeInMillis(fmp3.lastModified());
        File fdest = new File(sincdir);
        System.out.println("--" + (dModified != null && dModified.before(newDModified)) + " fdest.exists()" + fdest.exists());

        System.out.println("dm:" + dModified);
        if (fdest.exists()) {
            System.out.println("start sync>>");
            dModified = newDModified;
            String mp3page = VKNetwork.getVKMP3PageByFile(filepath);
            VKNetwork vkNetwork = new VKNetwork(sincdir, mp3page, this);
            vkNetwork.start();// downloadAll();
            System.out.println("end sync<<");
        }
        try {
            Thread.yield();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //	}
    }

    /**
     * To shutdown when run as an application. This is a fairly lame
     * implementation. A more self-respecting implementation would at least
     * check to see if a save was needed.
     */
    protected final class AppCloser extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            Section prefsec = prefs.get("settings");
            if (prefsec == null)
                prefsec = prefs.add("settings");
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
        System.out.println("event 1!"
                + mp3catalogField.getDocument().getLength());

    }

    // /** An ActionListener that listens to the radio buttons. */
    // public Action submit = new AbstractAction()class RunButtonListener
    // implements ActionListener {
    //
    // VKMp3Sinc vkMp3Sinc = null;
    //
    // public RunButtonListener(VKMp3Sinc vkMp3Sinc) {
    // this.vkMp3Sinc = vkMp3Sinc;
    // }
    //
    // public void actionPerformed(ActionEvent e) {
    // System.out.println("event!" + mp3catalogField.getDocument().getLength());
    // JComponent c = (JComponent) e.getSource();
    // if(c == showButton) {
    // String sincFile = filePathField.getText();
    // String sincDir = mp3catalogField.getText();
    // vkMp3Sinc.sinchronizeByFile(sincFile, sincDir);
    // }
    //
    // }
    // }

    public void sinchronizeByFile(String sincFile, String sincDir)
            throws ClientProtocolException, IOException, URISyntaxException {
        sinchronize(sincFile, null, null, sincDir);
    }

    /**
     * Action appends a '#' to the textfields content.
     */
    public Action submit = new AbstractAction() {

        // VKMp3Sinc vkMp3Sinc = null;

        public void actionPerformed(ActionEvent e) {
            System.out.println("event!"
                    + mp3catalogField.getDocument().getLength());
            JComponent c = (JComponent) e.getSource();
            System.out.println("event!33" + c);
            // if(c == showButton) {
            String sincFile = filePathField.getText();
            String sincDir = mp3catalogField.getText();
            try {
                sinchronizeByFile(sincFile, sincDir);
            } catch (ClientProtocolException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            // }
        }
    };
}
