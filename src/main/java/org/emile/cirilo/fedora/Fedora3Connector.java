/*
 * ZIM: Center for Information Modelling, University Graz
 * 
 * Licensed to ZIM under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * ZIM licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.emile.cirilo.fedora;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.emile.cirilo.IShared;
import org.emile.cirilo.Namespaces;
import org.emile.cirilo.exceptions.FedoraConnectionException;
import org.emile.cirilo.exceptions.FedoraException;
import org.emile.cirilo.models.DatastreamListEntry;
import org.emile.cirilo.models.FedoraEnvironment;
import org.emile.cirilo.models.ObjectListEntry;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.GetDatastream;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.GetObjectXML;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.GetDatastreamResponse;
import com.yourmediashelf.fedora.client.response.RiSearchResponse;
import com.yourmediashelf.fedora.generated.access.DatastreamType;
import com.yourmediashelf.fedora.generated.management.DatastreamProfile;

/**
 * @author Johannes Stigler
 */

public class Fedora3Connector  {
	 		
    private static Logger log = Logger.getLogger(Fedora3Connector.class);
  	
	private FedoraClient connector;
	private FedoraEnvironment environment;
	
	public void stubOpenConnection(String protocol, String hostname, String username, String passwd ) throws FedoraException, FedoraConnectionException {
	       
        this.environment = new FedoraEnvironment(protocol,hostname, null, username);
        try {
            connector = new FedoraClient(new FedoraCredentials(environment.getProtocol()+"://"+environment.getHostname(), username, passwd)); 
            FedoraClient.getObjectXML("fedora-system:ContentModel-3.0").execute((FedoraClient)connector);
        } catch (FedoraClientException fce) {
            if (fce.getStatus() == 401) {
                throw new FedoraConnectionException ( "Error while connecting to repository "+ environment.getProtocol()+"://"+environment.getHostname()+": No valid authorization");               
            } else { 
            	fce.printStackTrace();
                throw new FedoraConnectionException ("No Fedora 3.x service found on "+ environment.getProtocol()+"://"+environment.getHostname());
            }
        } catch (MalformedURLException mue) {
            throw new FedoraException(mue.getMessage(), mue);
        }
	}

	public byte[] stubGetDatastream(String pid, String dsid) throws FedoraException {
	    InputStream is = null;
	    FedoraResponse fr = null;
	    byte[] stream = null;
	    try {
         	GetDatastreamDissemination gdd=new GetDatastreamDissemination(pid,dsid);      
         	fr = gdd.execute(connector);   
         	fr.getEntityInputStream();
         	is = fr.getEntityInputStream();
         	stream = IOUtils.toByteArray(is);
	    } catch (Exception e) {
	    	throw new FedoraException(e.getMessage(), e);
	    } finally {
	        try {
	            is.close();
	        } catch (IOException io) {}    
	    }
        return stream;
	}


	public  byte[] stubGetObjectXML(String pid) throws FedoraException {
	    InputStream is = null;
	    FedoraResponse fr = null;
	    byte[] stream = null;
	    try {
		    GetObjectXML xml = new GetObjectXML(pid);
        	fr = xml.execute(connector);   
         	fr.getEntityInputStream();
         	is = fr.getEntityInputStream();
         	stream = IOUtils.toByteArray(is);
	    } catch (Exception e) {
	    	throw new FedoraException(e.getMessage(), e);
	    }
        return stream;
	}

	
	public boolean stubModifyDatastream(String pid, String dsid, String location) throws FedoraException {
		boolean status = false;
	    try {

			//  ModifyDatastream md = FedoraClient.modifyDatastream(pid, dsid);

	    	FedoraClient.purgeDatastream(pid, dsid).execute(connector);
	    	AddDatastream ad = FedoraClient.addDatastream(pid, dsid);
	    	
		    ad.controlGroup("E");
		    ad.dsLabel(dsid.equals("PID") ? "Permalink" : "URL to a Website");
		    
		    if (dsid.equals("URL")) ad.mimeType("text/html");
		    ad.dsLocation(location);
		    ad.versionable(false);
		    ad.execute(connector);	    
		    status = true;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new FedoraException(e.getMessage(), e);
	    }
        return status;
	}

	public boolean stubModifyQRStream(String pid, File stream) throws FedoraException {
		boolean status = false;
	    try {

	    	ModifyDatastream md = FedoraClient.modifyDatastream(pid, "QR");	
	    	md.controlGroup("M");
		    md.dsLabel("QR Code");
		    md.content(stream);
		    md.execute(connector);	    
		    status = true;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new FedoraException(e.getMessage(), e);
	    }
        return status;
	}
	
	public boolean exist(String pid) {
		
		return exist(pid, "DC");
			
	}
	
	public boolean exist(String pid, String dsid) {
	    GetDatastreamResponse gdr = null;
	    boolean status = false;
		try {
			GetDatastream gd = FedoraClient.getDatastream(pid, dsid);
			gdr = gd.execute(connector);
			status = true;
		} catch (Exception e) {	
		} finally {
		}
		return status;
	}
	
	public String getCreated(String pid) {
	    String created = null;
		try {
      		RiSearchResponse res = FedoraClient.riSearch(IShared.CREATED.replaceAll("[$]pid", pid)).format("sparql").execute(connector);	
  	        XPath xpath = XPath.newInstance("//s:result");
            xpath.addNamespace( Namespaces.xmlns_sparql2001 );
            Element result =  (Element) xpath.selectSingleNode(new SAXBuilder().build(res.getEntityInputStream()));
            created = result.getChildText("created", Namespaces.xmlns_sparql2001 );
		} catch (Exception e) {	
		} finally {
		}
		return created;
	}
	

	@SuppressWarnings("unchecked")
	public ArrayList<ObjectListEntry>stubGetObjectList(String owner, String exclude) throws FedoraException {
		 
        ArrayList<ObjectListEntry> objects = new ArrayList<ObjectListEntry>();
        String query = IShared.SPARQL; 
        
        try {
             	
        	if (owner !=  null) {     		
        		query = query.replaceAll("[$]owner", "?object <info:fedora/fedora-system:def/model#ownerId> '"+owner+"'. ");
        	} else {
        		query = query.replaceAll("[$]owner",  "?object <info:fedora/fedora-system:def/model#ownerId> ?owner. ");
        	}
        	
        	exclude = exclude == null ? "" :  " && !regex(?owner,'^(" + exclude.replaceAll(",", "|") + ")','i')";  	
        	query = query.replaceAll("[$]exclude", exclude);
               	
       		RiSearchResponse res = FedoraClient.riSearch(query).format("sparql").execute(connector);	
	        Document data = new SAXBuilder().build(res.getEntityInputStream());
 	   
  	        XPath xpath = XPath.newInstance("//s:result");
            xpath.addNamespace( Namespaces.xmlns_sparql2001 );
            
            List<Element> results =  (List<Element>) xpath.selectNodes( data );

            for (Element el : results) {
                objects.add(new ObjectListEntry( el.getChildText("pid", Namespaces.xmlns_sparql2001 ),
                                                 el.getChildText("title", Namespaces.xmlns_sparql2001 ),
                                                 el.getChildText("model", Namespaces.xmlns_sparql2001 ),
                                                 el.getChildText("owner", Namespaces.xmlns_sparql2001 ),
                                                 el.getChildText("handle", Namespaces.xmlns_sparql2001 ) == null  ? "-" : el.getChildText("handle", Namespaces.xmlns_sparql2001 ),
                                                 el.getChildText("modified", Namespaces.xmlns_sparql2001 )
                                               )
                           );
             }
            
	     } catch (Exception e) {
	    	 e.printStackTrace();
	    	 throw new FedoraException(e); }
        
         return objects;
	
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String>stubGetAllObjects() throws FedoraException {
		 
        ArrayList<String> objects = new ArrayList<String>();
        String query = IShared.SPARQLALL; 
        
        try {
             	          	
       		RiSearchResponse res = FedoraClient.riSearch(query).format("sparql").execute(connector);	
	        Document data = new SAXBuilder().build(res.getEntityInputStream());
 	   
  	        XPath xpath = XPath.newInstance("//s:result");
            xpath.addNamespace( Namespaces.xmlns_sparql2001 );
            
            List<Element> results =  (List<Element>) xpath.selectNodes( data );

            for (Element el : results) {
                objects.add( el.getChildText("pid", Namespaces.xmlns_sparql2001 ) );
             }
            
	     } catch (Exception e) {
	    	 e.printStackTrace();
	    	 throw new FedoraException(e);
	     }
        
         return objects;
	
	}
	public String stubGetOwner(String pid) throws FedoraException {
	   
	       try {
	            String owner = null;
	            
	            String query ="select ?owner where { "+
	                    "?object  <http://purl.org/dc/elements/1.1/identifier> '"+pid+ "'."+
	                    "?object <info:fedora/fedora-system:def/model#ownerId> ?owner }";
	
	            RiSearchResponse res = FedoraClient.riSearch(query).format("sparql").execute(connector);    
	            Document data = new SAXBuilder().build(res.getEntityInputStream());
	            
	            XPath xpath = XPath.newInstance("//s:result");
	            xpath.addNamespace( Namespaces.xmlns_sparql2001 );
	            Element el =  (Element) xpath.selectSingleNode( data );
	            owner = el.getChildText("owner", Namespaces.xmlns_sparql2001 );
	            
	            return owner;
	            
	          } catch (FedoraClientException fce) {
	              throw new FedoraException(fce.getMessage(), fce);
	          } catch (IOException io) {
	              throw new FedoraException(io.getMessage(), io);
	          } catch (JDOMException je) {
	              throw new FedoraException(je.getMessage(), je);
	          } catch (Exception e) {
	              throw new FedoraException(e.getMessage(), e);
	        }
	       
	}

	@SuppressWarnings("unchecked")
	public ArrayList<DatastreamListEntry> stubGetDatastreamList(String pid) throws FedoraException{
		try {
			List<DatastreamType>datastreams=listDatastreams(pid);
			ArrayList<DatastreamListEntry> datastreamlist = new ArrayList<DatastreamListEntry>();
			HashMap<String, DatastreamListEntry> dsl = new HashMap<String, DatastreamListEntry>();
			
			for (DatastreamType ds:datastreams) {	
				if (dsl.get(ds.getDsid()) == null) {
					DatastreamProfile dp = getDatastreamProfile(pid, ds.getDsid());
					dsl.put(dp.getDsID(),new DatastreamListEntry(dp.getDsID(),dp.getDsLabel(),dp.getDsMIME(),dp.getDsControlGroup(),dp.getDsLocation()));
				}	
			}
			
		    Iterator iterator = dsl.entrySet().iterator();
		    while (iterator.hasNext()) {
		    	    Map.Entry<String, DatastreamListEntry> me = (Map.Entry<String, DatastreamListEntry>)iterator.next(); 
		    	    datastreamlist.add(me.getValue());
		    }
			
            return datastreamlist;
		} catch (Exception e) {
		    throw new FedoraException(e.getMessage(), e);
   	   	}
	}

    public FedoraEnvironment getEnvironment() {
        return this.environment;
    }
    
 					
	public List<DatastreamType> listDatastreams(String pid) throws FedoraException {
		 try {			  
			  List<DatastreamType>datastreams= FedoraClient.listDatastreams(pid).execute(connector).getDatastreams();
			  return datastreams;
		  } catch (FedoraClientException fce) {
		      throw new FedoraException(fce.getMessage(), fce);
		  }
	}

	public DatastreamProfile getDatastreamProfile(String pid, String dsid)  throws  FedoraException {
	     GetDatastreamResponse gdr = null;
	     DatastreamProfile dp = null;
		 try {
		     GetDatastream gd = FedoraClient.getDatastream(pid, dsid);
		     gdr= gd.execute(connector);
		     dp = gdr.getDatastreamProfile();
		 } catch (FedoraClientException fce) {
		     throw new FedoraException(fce.getMessage(), fce);
		 }	finally {
   	 }
         return dp;
	}
	
}
 