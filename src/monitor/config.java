/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author hmusallam
 */
public class config {
    
    
     private final String email ;
     private final String password;

      public config(String sys_path) throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
//        File fXmlFile = new File(getClass().getResource("/t/config").getFile());
        File fXmlFile = new File(sys_path+"/config");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	 org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
         doc.getDocumentElement().normalize();
         Element e = (Element)doc.getElementsByTagName("config").item(0);
         this.email = e.getAttribute("EMAIL");
         this.password = e.getAttribute("PASSWORD");

        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

}
