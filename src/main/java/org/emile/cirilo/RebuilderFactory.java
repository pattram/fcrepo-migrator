package org.emile.cirilo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.emile.cirilo.fedora.Fedora3Connector;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.jdom.Element;
import org.jdom.Attribute;

public class RebuilderFactory {

	private static String HOST = "https://gams.uni-graz.at/";
	
	private static Logger log = Logger.getLogger(RebuilderFactory.class);

	private static  SAXBuilder builder = new SAXBuilder();
	
	private ArrayList<String>objects = null;
	private Fedora3Connector source;
	
	public RebuilderFactory(Fedora3Connector source) {
		try {
		  this.source = source;	
		  log.info("Fetching PID list from source repository ...");
		  objects = this.source.stubGetAllObjects();	        	     
		  log.info(objects.size()+" objects found in the source repository");
		} catch (Exception e) {
		  e.printStackTrace();
		}

	}
	
	
	public void replace() {
		
		 	 Attribute cogr;
		 	 
    	     for (int i = 0; i < objects.size(); i++) {
    	    	 try {
    	    		 
    	    		 String pid = objects.get(i);	  
    	    		 
    	 			 Document xml = builder.build(new ByteArrayInputStream(source.stubGetObjectXML(pid)));
    			    
    			     XPath x0path = XPath.newInstance("//foxml:datastream[@ID='PID']/@CONTROL_GROUP");
    			     x0path.addNamespace(Namespaces.xmlns_foxml);   
    			     cogr =  (Attribute) x0path.selectSingleNode( (Document) xml );
    			     
    			     if (cogr != null && cogr.getValue().equals("R")) {
    			     
    			    	 XPath upath = XPath.newInstance("//foxml:datastream[@ID='PID']//foxml:contentLocation[@TYPE='URL']");
        			     upath.addNamespace(Namespaces.xmlns_foxml);

    			    	 List<Element> contentLocations = (List<Element>) upath.selectNodes( (Document) xml );
    			     
    			    	 if (contentLocations != null) {
    			    		Element contentLocation = contentLocations.get(contentLocations.size()-1);    			 
    			    		String location = contentLocation.getAttributeValue("REF");
    			    		if (location != null) {
    			    			
    			    			source.stubModifyDatastream(pid, "PID", location);
    			    			log.info(pid+"/PID"+"|changed");
    			    			 
    			    		}     	   		    	    		
    			    	 }
    			     }
    			     
    			    
    			     XPath x1path = XPath.newInstance("//foxml:datastream[@ID='RELS-EXT']//model:hasModel/@rdf:resource");
			    	 x1path.addNamespace(Namespaces.xmlns_foxml);
			    	 x1path.addNamespace(Namespaces.xmlns_rdf);
			    	 x1path.addNamespace(Namespaces.xmlns_F3_model);
    			     Attribute model =  (Attribute) x1path.selectSingleNode( (Document) xml );

    			    	 
		    		 if (model != null && model.getValue().equals("info:fedora/cm:Resource")) {	
		    			  
		       			XPath upath = XPath.newInstance("//foxml:datastream[@ID='URL']/@CONTROL_GROUP");
		    			upath.addNamespace(Namespaces.xmlns_foxml);   
		    			cogr =  (Attribute) upath.selectSingleNode( (Document) xml );
		    			  
		    			if (cogr != null && cogr.getValue().equals("R")) {			  
		    				XPath opath = XPath.newInstance("//foxml:datastream[@ID='URL']//foxml:contentLocation[@TYPE='URL']/@REF");
	    			    	opath.addNamespace(Namespaces.xmlns_foxml);
	    			    	Attribute loc =  (Attribute) opath.selectSingleNode( (Document) xml );
		    				source.stubModifyDatastream(pid, "URL", loc.getValue());
			    			log.info(pid+"/URL"+"|changed");
		    			 }
    			     }
		    		 
		    		 if (model != null && !model.getValue().equals("info:fedora/cm:OAIRecord")) {	
		    			 genQR(pid);
		    	    	 log.info(pid+"/QR"+"|updated");
		    		 }
		    		 
     			  } catch (Exception e) {
    		      }	

     	      }	    
   }
	
   public void genQR (String pid) {	
    	try { 
    		String toEncode = HOST+pid;
    		ByteArrayOutputStream stream = QRCode.from(toEncode).to(ImageType.JPG).withSize(125, 125).stream();
    		File temp = File.createTempFile("tmp","xml");
    		OutputStream out = new FileOutputStream (temp);
    		stream.writeTo(out);
    		source.stubModifyQRStream(pid, temp);
    		temp.delete();
    	} catch (Exception e) {    		
    	}
    }
}
