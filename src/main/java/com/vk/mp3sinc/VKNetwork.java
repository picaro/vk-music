package com.vk.mp3sinc;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class VKNetwork extends Thread implements IVKNetwork {

    private String sincdir;
    private String mp3page;
//	private Map<String, String> mp3s;
    //VKMp3Sinc vkMp3Sinc;

    public VKNetwork(String sincdir, String mp3page, VKMp3Sinc vkMp3Sinc) {
        this.sincdir = sincdir;
        //this.vkMp3Sinc = vkMp3Sinc;
        this.mp3page = mp3page;

    }

    public String vkLogin() {
        return null;
    }

    public class Mp3FileFilter implements FileFilter {

        public boolean accept(File pathname) {
            if (pathname.isFile() && pathname.getAbsolutePath().endsWith(".mp3")) {
                return true;
            }
            return false;
        }
    }

    public void run() {

        //vkMp3Sinc.getShowButton().setEnabled(false);
        //vkMp3Sinc.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));


        System.out.println("thread run");
        //mp3page = "werwer wr  http://rr.rrr.mp3  er er ";
        //parse mp3-s
        VkMp3Parser vkMp3Parser = new VkMp3Parser();
        Map<String, String> mp3s = vkMp3Parser.getAllMp3s(mp3page);
        //check lists


        //sinchronize (download new - delete unnecessary)
        File file = new File(sincdir);
        file.isDirectory();
        FileFilter filter = new Mp3FileFilter();
        File[] flist = file.listFiles(filter);

        //vkMp3Sinc.getmProgressBar().setValue(0);

        Map<String, File> localDir = new Hashtable<String, File>();

        if (flist != null)
            for (int i = 0; i < flist.length; i++) {
                File mfile = flist[i];
                localDir.put(mfile.getName(), mfile);
                //System.out.println(mfile.getName());
            }


        Set<String> keys = mp3s.keySet();
        Iterator<String> iter = keys.iterator();
        //vkMp3Sinc.getmProgressBar().setMaximum(keys.size());

        while (iter.hasNext()) {
            Thread.yield();

            String key = (String) iter.next();
            if (localDir.containsKey(VkMp3Parser.sanitizeFilename(key))) {
                //excluded.add(key);
                iter.remove();
                System.out.println("excluded:" + key);
            } else {
                System.out.println("need:" + key);
            }
        }
        downloadAll(mp3s);
        System.out.println("thread stop");

//    	vkMp3Sinc.getShowButton().setEnabled(true);
//    	vkMp3Sinc.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    }

    private void downloadAll(Map<String, String> mp3s) {
        //download new
        Set<String> keys2 = mp3s.keySet();
        Iterator<String> iter2 = keys2.iterator();

        int n = 0;
        //	vkMp3Sinc.getmProgressBar().setMaximum(keys2.size());
        while (iter2.hasNext()) {
            //	vkMp3Sinc.getmProgressBar().setValue(n);

            Thread.yield();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            n++;
            String key = (String) iter2.next();
            System.out.println(">>" + key);

            String urlenc = null;
          //  try {
                urlenc = mp3s.get(key);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }

            key = VkMp3Parser.sanitizeFilename(key);

            download(urlenc, sincdir + File.separator + key);
        }
    }

    public static String getVKMP3PageByFile(String filepath) {
        StringBuilder page = new StringBuilder();
        try {
            FileInputStream fstream = new FileInputStream(filepath);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "windows-1251"));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                page.append(strLine);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(page);
        return page.toString();
    }

    public void download(String inputURL, String sincdir) {
        try {
            System.setProperty("http.proxyHost", "192.168.32.7");
            System.setProperty("http.proxyPort", "3128");

//				Prowser prowser = new Prowser();
//                Tab tab = prowser.createTab();
//			    Request request =
//			        new Request(inputURL);
//			    byte[] html = tab.go(request).getPageBytes();
//			    if (html == null || html.length < 1000) {
//			    	System.out.println("something wrong!");
//			    	return;
//			    }

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.32.7", 3128)); // set proxy server and port
            System.out.println("b1" + inputURL);

            URL url = new URL(inputURL);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);

            uc.connect();

            System.out.println("b1");
            StringBuffer tmp = new StringBuffer();
            int read = 0;
            byte[] bytes = new byte[1024];
            InputStream in = uc.getInputStream();
            OutputStream out = new FileOutputStream(sincdir);
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            System.out.println("b2");


            //    out.write(tmp.toString().getBytes());
            out.close();
            System.out.println("b3");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
