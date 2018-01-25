/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import CustomEvent.InterfaceEventObject;
import CustomEvent.InterfaceListener;
import CustomEvent.Interface_event;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class monitorInternet {

    private static boolean running = true;
    private static String username;
    private static String password;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final HashMap<String, HashMap> interface_table = new HashMap();
    private static final Logger logger = Logger.getLogger("SysIntLog");
    private static final Interface_event event_object = new Interface_event();
    private static final monitorInternet monitorInternet_object= new monitorInternet();
    private static short first_time_flage = 0;

    
    
    

    public static void main(String[] args)  {

          event_object.addEventListener(new InterfaceListener() {
            @Override
            public void eventHandler(InterfaceEventObject event, InterfaceData data) {
            
                
                          Date date = new Date();
                                String mesaage_subject = "LoggedIn date to Internet: " + dateFormat.format(date);
                                String message_content = "Date : " + dateFormat.format(date)
                                        + "\n" + "User Name: " + data.getUser_name()
                                        + "\n" + "NetWork physcial address: " + data.getInterface_physical_address()
                                        + "\n" + "Interface: " + data.getInterface_name()
                                        + "\n" + "Windows Version:  " + data.getWindows()
                                        + "\n" + "Version number: " + data.getWindows_version();

                                logger.info("Date: " + dateFormat.format(date) + "  interface name :" + data.getInterface_name() + " MAC Address: " + data.getInterface_physical_address());
                                monitorInternet_object.send_email(monitorInternet.username, mesaage_subject, message_content);
                
            }
        });
          
          
       String sys_path = Utility.getRootPath();

        FileHandler fh = null;
        try {
           fh = new FileHandler(sys_path+"/SysIntLog.log");
//            fh = new FileHandler("./SysIntLog.log");
        } catch (IOException ex) {
        } catch (SecurityException ex) {
        }
        
        logger.addHandler(fh);

        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        config conf = null;
        try {
            conf = new config(sys_path);
        } catch (ParserConfigurationException ex) {
        } catch (SAXException ex) {
        } catch (IOException ex) {
        } catch (URISyntaxException ex) {
        }

        monitorInternet.username = conf.getEmail();
        monitorInternet.password = conf.getPassword();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Date date = new Date();
                logger.info("Applicak8tion shutdown : " + dateFormat.format(date));
            }
        });


      
        

        monitorInternet_object.scanInterfaces();

        while (running) {

            Enumeration<NetworkInterface> interfaces = null;
            try {
                interfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                e.printStackTrace();
            }

            while (interfaces.hasMoreElements()) {
                NetworkInterface nic = interfaces.nextElement();
                try {

                    if ((nic.getName().contains("wlan") || nic.getName().contains("eth"))) {

                        byte[] mac = nic.getHardwareAddress();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; mac != null && i < mac.length; i++) {
                            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                        }
                        boolean is_up = Boolean.parseBoolean(interface_table.get(nic.getName()).get("is_up").toString());
                        
                        if(nic.isUp()){
                        if(first_time_flage == 0){
                        
                          while (!monitorInternet_object.isConnectedToInternet()) {
                                    continue;
                                }
                      first_time_flage =1;   
                      
                      
                     event_object.fireEvent(new InterfaceData(System.getProperty("user.name"), sb.toString(),nic.getName(),System.getProperty("os.name"), System.getProperty("os.version")));


                        }
                        }
                        
                        
                        if (is_up != nic.isUp()) {

                            interface_table.get(nic.getName()).put("is_up", nic.isUp());

                            if (nic.isUp()) {

                                while (!monitorInternet_object.isConnectedToInternet()) {

                                    continue;
                                }

                                event_object.fireEvent(new InterfaceData(System.getProperty("user.name"), nic.getName(), sb.toString(),System.getProperty("os.name"), System.getProperty("os.version")));
                      

                            }
                        }

                    }
                } catch (SocketException e) {

                }
            }
            
            
              try {
                  Thread.sleep(5000);
              } catch (InterruptedException ex) {
//                  Logger.getLogger(monitorInternet.class.getName()).log(Level.SEVERE, null, ex);
              }

        }

    }

    public void send_email(String reciver_email, String message_subject, String message_content) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(monitorInternet.username, monitorInternet.password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(monitorInternet.username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(reciver_email));
            message.setSubject(message_subject);
            message.setText(message_content);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
//            throw new RuntimeException(e);
        }

    }

    public void scanInterfaces() {

        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {

            interfaces = NetworkInterface.getNetworkInterfaces();

        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface nic = interfaces.nextElement();
            try {
                if ((nic.getName().contains("wlan") || nic.getName().contains("eth"))) {

                    byte[] mac = nic.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; mac != null && i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }

                    HashMap<String, String> interface_data_table = new HashMap();
                    interface_data_table.put("os.name", System.getProperty("os.name"));
                    interface_data_table.put("os.arch", System.getProperty("os.arch").toString());
                    interface_data_table.put("os.version", System.getProperty("os.version"));
                    interface_data_table.put("os.version", System.getProperty("os.version"));
                    interface_data_table.put("user.name", System.getProperty("user.name"));
                    interface_data_table.put("mac_address", sb.toString());
                    interface_data_table.put("is_up", Boolean.toString(nic.isUp()));
                    interface_data_table.put("interface_name", nic.getDisplayName());
                    interface_table.put(nic.getName(), interface_data_table);

                }
            } catch (SocketException e) {
                e.printStackTrace();

            }
        }

    }

    public boolean isConnectedToInternet() {
        try {
            try {
                URL url = new URL("http://www.google.com");
//				System.out.println(url.getHost());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.connect();
                if (con.getResponseCode() == 200) {
                    return true;
                }
            } catch (Exception exception) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void stop() {
        running = false;
        Date date = new Date();
        logger.warning("Service shutdown : " + dateFormat.format(date));
    }
    
    
    
    
    
}
