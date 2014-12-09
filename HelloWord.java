import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.StringReader;

import java.io.File;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
  
import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.transform.Transformer;  
import javax.xml.transform.TransformerConfigurationException;  
import javax.xml.transform.TransformerException;  
import javax.xml.transform.TransformerFactory;  
import javax.xml.transform.dom.DOMSource;  
import javax.xml.transform.stream.StreamResult;  
  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  
import org.w3c.dom.Text;  
import org.xml.sax.SAXException; 



import java.net.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
public class HelloWord extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {  response.setContentType("text/html;charset=UTF-8");
       PrintWriter out = response.getWriter();
       Enumeration paramNames = request.getParameterNames();
	 	  String url_php="http://cs-server.usc.edu:19356/test8.php/?";
	  // String url_php="http://default-environment-stxyusygwu.elasticbeanstalk.com/?";
	  	String[] loc=request.getParameterValues("location") ; 
		String[] ty=request.getParameterValues("type");
		String[] te=request.getParameterValues("temp");
		  loc[0]= URLEncoder.encode(loc[0], "UTF-8");
		 url_php+="location="+loc[0]+"&type="+ty[0]+"&tempUnit="+te[0];
		 
       // out.print(url_php+"<br>");
		//out.print(loc[0]+ty[0]+te[0]);
       
	  
	 // String urlString="http://cs-server.usc.edu:19356/test8.php/?location=90007&type=zip&tempUnit=f";
	   URL url=new URL(url_php);
	   URLConnection urlConnection=url.openConnection();
	   urlConnection.setAllowUserInteraction(false);
	   InputStream urlStream =url.openStream();
	   
	   
	   
       String json="{ \"weather\":";json+="{\"forecast\":[";
	   String link="";
	   String feed="";
	   String img="";
	   String nunit="";
	   String vunit="";
	  
	   String nloc[]=new String[3];
	   String vloc[]=new String[3];
	   String ncodi[]=new String[2];
	   String vcodi[]=new String[2];
	 
	   try{
            DocumentBuilderFactory docFactory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(urlStream);
      
            Element  root = doc.getDocumentElement(); 
			NodeList Nodes = root.getChildNodes(); 
			
			String weather=root.getNodeName();//weather
            if (weather=="info")
			{
			   String error="error!";
			   out.println(error);
			}
			else
			{
			int num=0;
            for(int i=0;i<Nodes.getLength();i++)
			{
			    Node emp=Nodes.item(i);
				if (emp.getNodeName().equals("#text"))
                 continue;
				 
				if (emp.getNodeName().equals("forecast")){
				  num++;
				  
					String fname[]=new String[4];
					String fvalue[]=new String[4];
				    for(int j=0;j<4;j++){
					  	
				      fname[j]=emp.getAttributes().item(j).getNodeName();
					  fvalue[j]=emp.getAttributes().item(j).getNodeValue();
			
				  }
				  json+="{\"";
					 	json+=fname[0]+"\":\""+fvalue[0]+"\",";
						json+="\""+fname[1]+"\":"+fvalue[1]+",";
						json+="\""+fname[3]+"\":\""+fvalue[3]+"\",";
						json+="\""+fname[2]+"\":"+fvalue[2]+"}";
						if(num!=5)
						json+=",";
						else if(num==5)
						json+="],";
				}			
				   
			    else if (emp.getNodeName().equals("link")){
				
					link=emp.getFirstChild().getNodeValue();//value of link
				
				}	
				  
				else if (emp.getNodeName().equals("location")){//get Units
				    
					
				    for(int j=0;j<3;j++){
					    nloc[j]=emp.getAttributes().item(j).getNodeName();
						vloc[j]=emp.getAttributes().item(j).getNodeValue();
						
				  }
				  
				}	
                 			
				else if (emp.getNodeName().equals("units")){//get units
					
					 nunit=emp.getAttributes().item(0).getNodeName();
					 vunit=emp.getAttributes().item(0).getNodeValue();
					
				
				}	
			    else if (emp.getNodeName().equals("condition")){
					
				    for(int j=0;j<2;j++){
						ncodi[j]=emp.getAttributes().item(j).getNodeName();
						vcodi[j]=emp.getAttributes().item(j).getNodeValue();
					
					}
				}
				else if (emp.getNodeName().equals("img")){//value of image
				    img=emp.getFirstChild().getNodeValue();
				
				}	
				else if (emp.getNodeName().equals("feed")){
				
				    feed=emp.getFirstChild().getNodeValue();//value of feed
				
				}	
             				
             } //end for 
   
       json+="\"condition\":{";
	   json+="\""+ncodi[1]+"\":\""+vcodi[1]+"\",";
	   json+="\""+ncodi[0]+"\":"+vcodi[0]+"},";
	   json+="\"location\":{";
	   json+="\""+nloc[0]+"\":\""+vloc[0]+"\",";
	   json+="\""+nloc[1]+"\":\""+vloc[1]+"\",";
	   json+="\""+nloc[2]+"\":\""+vloc[2]+"\"},";
	   json+="\"link\":\""+link+"\",";
	   json+="\"img\":\""+img+"\",";
	   json+="\"feed\": \""+feed+"\",";
	   json+="\"units\":{\""+nunit+"\":\""+vunit+"\"}";
	   json+="}}";
	   out.println(json);	
	   
        }
       }
        catch(Exception e){
            out.println(e);
        }
     }
	
  }

        
    


