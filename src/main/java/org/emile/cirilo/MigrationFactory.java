package org.emile.cirilo;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.emile.cirilo.exceptions.FedoraException;
import org.emile.cirilo.fedora.Fedora3Connector;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.models.DatastreamListEntry;
import org.emile.cirilo.models.ObjectListEntry;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class MigrationFactory {

	private static Logger log = Logger.getLogger(MigrationFactory.class);

	private ArrayList<ObjectListEntry>objects = null;
	
	private static  SAXBuilder builder = new SAXBuilder();
	
	private Fedora3Connector source;
	private FedoraConnector target;
	private Pattern pattern;
    private String exclude;
    private String f3_repo;
    private String f3_username;
    private String f3_passwd;
 
    private int[] counter = {0,0,0};  // found, successful, error
    
	private int nobj;
	private int nds;
	private int nnf;

	
	public MigrationFactory(Fedora3Connector source, FedoraConnector target, String owner, String f3_repo, String f3_username, String f3_passwd, String exclude) {
		
		this.source = source;
		this.target = target;
		this.pattern = Pattern.compile("^cirilo:[a-z0-9]+$");
		this.exclude = exclude;
		this.f3_repo = f3_repo;
		this.f3_username = f3_username;
		this.f3_passwd = f3_passwd;
		
		
		try {
			
			objects = source.stubGetObjectList(owner, exclude);	        	     
        	log.info(objects.size()+" objects found in the source repository");

        	counter[0] = objects.size();
        	
    		nobj = 0;
    		nds = 0;
    		nnf= 0;
    		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public long size() {
		return objects.size();
	}
	
	public void analyseObject(int index) {
		
	    try {
	    	String pid = objects.get(index).getPid().replaceAll("container:|collection:", "context:").replaceAll("Backbone","settings").replaceAll("_", "@");		

	    	if (target.stubExist(pid, null) == 200) {
				log.info(String.format("(%6d) object <%s> found", index+1, pid));
	    		nobj++;
	    	} else {
				log.info(String.format("(%6d) object <%s> not found", index+1, pid));
				nnf++;
	    	}
	    } catch (Exception e) {
	    	log.error("Internal Server Error");
	    }
 
	}
	
	
	public void printStats() {
		  
	   System.out.println(objects.size()+ " objects in source repository found.");
	   System.out.println(nobj + " objects of source repository could migrated successfully");
	   System.out.println("In "+ nnf + " objects an error occurred");
	  
	}
	
	public void createObject(int index) {

	    String pid = objects.get(index).getPid();		
        String oid = pid;
        ArrayList<DatastreamListEntry>datastreams;
	    
		try {
	
			target.stubTxBegin();
			
			oid = pid.replaceAll("container:|collection:", "context:").replaceAll("Backbone","settings");
			int i = 0;
			
			String owner = null;
			
			while (true) {
				try {
					datastreams=source.stubGetDatastreamList(pid);
					owner=source.stubGetOwner(pid);
					break;
				} catch (Exception e) {
					 if (i++ == 0) log.info("Waiting for connection to source host for reading data of object "+pid);
					 String err = e.getMessage();
					 if (!err.contains("Service Unavailable") && !err.contains("403") && !err.contains("java.net.UnknownHostException")) { 
						 log.error(String.format("Unrecoverable error during reading object %s, message: %s", pid, err));
						 return;
					 }
					 Thread.sleep(2000);	
					 try {
						 source.stubOpenConnection("http", f3_repo, f3_username, f3_passwd);
					 } catch (Exception q) {}
				}
			}

		
			Document rels_ext = builder.build(new ByteArrayInputStream(source.stubGetDatastream(pid, "RELS-EXT")));
			Element desc = rels_ext.getRootElement().getChild("Description", Namespaces.xmlns_rdf);
			desc.addNamespaceDeclaration(Namespaces.xmlns_cm4f_xsl);
			desc.addNamespaceDeclaration(Namespaces.xmlns_F3_relations);			
			
			Element hasModel = desc.getChild("hasModel",Namespaces.xmlns_F3_model);
			String model = hasModel.getAttributeValue("resource", Namespaces.xmlns_rdf);
			
		    if (model.contains("Image")) {
		    	model = "cm:TEI";
		    	hasModel.setAttribute("resource", model, Namespaces.xmlns_rdf);
		    }			
			
			String prefix = StringUtils.substringAfter(model, "cm:").toLowerCase().replaceAll("dfg", "");
           
			XPath xpath = XPath.newInstance("//dc:*");
			xpath.addNamespace(Namespaces.xmlns_dc);
			
			XPath qpath = XPath.newInstance("//rel:isMemberOf");
			qpath.addNamespace(Namespaces.xmlns_F3_relations);

			boolean tordf = false;
			boolean snippet = false;
			boolean r = false;
			boolean mets = false;
			boolean cust = false;
			boolean bdc = false;
			boolean hssf = false;
			
			
			for (DatastreamListEntry ds:datastreams) {	
				if (!IShared.skipDatastreams.contains("|"+ds.getDsid()+"|")) {
				   if ("R".contains(ds.getControlgroup())) {
				       try {
				           if (ds.getDsid().equals("URL")) {
				               if (model.contains("cm:Resource") || model.contains("cm:OAIRecord")) desc.addContent(new Element("isShownAt",Namespaces.xmlns_edm).setAttribute("resource",URIUtil.encodeQuery(ds.getLocation()),Namespaces.xmlns_rdf));						   
				           } else {	  
				               
			                   if (model.contains("cm:HTML")) continue;
			                   if (model.contains("cm:PDF")) continue;
		                          
			                   if (model.contains("cm:Resource")) continue;
                               if (model.contains("cm:LaTeX")) continue;
                               
                               if (model.contains("cm:dfgMETS")) continue;
		                       
		                       String name = ds.getDsid();
			                   String location = ds.getLocation();
			                   
			                   if (name.equals("STYLESHEET")) {
			                       if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/"+prefix+"2html.xsl";
			                   }
			                   if (name.equals("FO_STYLESHEET")) {
	                               if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/"+prefix+"2fo.xsl";
	                           }			                   
                               if (name.equals("HSSF_STYLESHEET")) {
                                   hssf = true;
                                   if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/"+prefix+"2hssf.xsl";
                               }                               
                               if (name.equals("SNIPPET_STYLESHEET")) {
                                   snippet = true;
                                   if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/"+prefix+"2snippet.xsl";
                               }
                               if (name.equals("R_STYLESHEET")) {
                                   r = true;
                                   if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/"+prefix+"2dataset.xsl";
                               }
                               if (name.equals("TORDF")) {
                                   tordf = true;
                                   if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/"+prefix+"2rdf.xsl";
                               }
                               if (name.equals("GMLTORDF")) {
                                    if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/gml2rdf.xsl";
                               }
                               if (name.equals("GMLTOJSON")) {
                                   if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/gml2json.xsl";
                               }
                               if (name.equals("GMLTOHTML")) {
                                   if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/gml2html.xsl";
                               }
                               if (name.equals("DC_MAPPING")) {
                                    if (location.contains("cirilo:Backbone")) location = "http://apache:82/xsl/"+prefix+"2dc.xsl";
                               }
		                                                  
                               location = location.replaceAll("(http[s]*://)([A-Za-z0-9:.-]+)(/.+)", "http://apache:82$3"); 

                               String distname =  IShared.bindings.get(name) != null ? IShared.bindings.get(name): name;
                               
                               if (model.contains("cm:BibTeX")) {
                                   if (name.equals("STYLESHEET")) distname = "hasXsltToHTML";
                                   if (name.equals("FO_STYLESHEET")) distname = "hasXsltToFO";
                               }
                                                  
				               desc.addContent(new Element(distname,Namespaces.xmlns_cm4f_xsl).setAttribute("resource",URIUtil.encodeQuery(location),Namespaces.xmlns_rdf));
				           }
	                   } catch (Exception eq) {
	                      log.error(String.format("Unrecoverable error during ingest of object %s, message: %s", pid, eq.getMessage()));
	                   }
					}            	 
	           	} 			
		
			}

			desc.addContent(new Element("hasXsltToDefaultView",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/dcmi/dc.xsl"),Namespaces.xmlns_rdf));
	                    
            if (model.contains("cm:Query")) {
                desc.addContent(new Element("hasXsltToJSON",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/query2json.xsl"),Namespaces.xmlns_rdf));
             } 
            
             if (model.contains("cm:TEI") || model.contains("cm:PDF") || model.contains("cm:LaTeX") || model.contains("cm:Resource")) {
                desc.addContent(new Element("hasXsltBibTeXToHTML",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/bibtex2html.xsl"),Namespaces.xmlns_rdf));
                desc.addContent(new Element("hasXsltBibTeXToFO",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/bibtex2fo.xsl"),Namespaces.xmlns_rdf));
             }
  
             if (model.contains("cm:OAIRecord")) {
                desc.addContent(new Element("hasXsltToEDM",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/cirilo:"+owner+"/RECORDtoEDM"),Namespaces.xmlns_rdf));
             }

             if (model.contains("cm:TEI") || model.contains("cm:SKOS") || model.contains("cm:Ontology" ) || model.contains("cm:LIDO")) {
                 if (!hssf) desc.addContent(new Element("hasXsltToHSSF",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/"+prefix+"2hssf.xsl"),Namespaces.xmlns_rdf));
             }  
           
             if (model.contains("cm:TEI")) {
                desc.addContent(new Element("hasXsltPipelines",Namespaces.xmlns_cm4f).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/pipelines.xml"),Namespaces.xmlns_rdf));
                desc.addContent(new Element("hasTokenizerProfile",Namespaces.xmlns_cm4f).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl-tokenizer/profile.xml"),Namespaces.xmlns_rdf));

                ArrayList<DatastreamListEntry>cirilo = null;
                String user = null;
                try {
                  Pattern p = Pattern.compile("^o:([A-Za-z0-9]+).*$");
                  Matcher m = p.matcher(pid);
                  if (m.find()) {
                      user = m.group(1);
                      cirilo = source.stubGetDatastreamList("cirilo:"+m.group(1));
                  }  
                } catch (Exception w) {} 
                
                if (!tordf) desc.addContent(new Element("hasXsltToRDF_Triples",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/tei2rdf.xsl"),Namespaces.xmlns_rdf));
                
                if (!snippet) desc.addContent(new Element("hasXsltToXML_Snippet",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/tei2snippet.xsl"),Namespaces.xmlns_rdf));
                
                if (!r) desc.addContent(new Element("hasXsltToR_Dataset",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/tei2dataset.xsl"),Namespaces.xmlns_rdf));
                                       
                if (cirilo != null) {
                    
                    for (DatastreamListEntry ds:cirilo) {  
                        
                        if ("TEITOMETS".equals(ds.getDsid()) && !ds.getLocation().contains("cirilo:Backbone") ) {
                           mets = true;
                           desc.addContent(new Element("hasXsltToMETS",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/cirilo:"+user+"/TEITOMETS"),Namespaces.xmlns_rdf));
                                                     
                        } else if ("TOTEI".equals(ds.getDsid()) && !ds.getLocation().contains("cirilo:Backbone")) {
                           cust = true;
                           desc.addContent(new Element("hasXsltForCustomization",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/cirilo:"+user+"/TOTEI"),Namespaces.xmlns_rdf));
                        }
                    }
                }    
                
                if (!mets) desc.addContent(new Element("hasXsltToMETS",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/tei2mets.xsl"),Namespaces.xmlns_rdf));
                if (!cust) desc.addContent(new Element("hasXsltForCustomization",Namespaces.xmlns_cm4f_xsl).setAttribute("resource",new URLCodec().decode("http://apache:82/xsl/customization4tei.xsl"),Namespaces.xmlns_rdf));
             } 

            desc.addContent(new Element("rights",Namespaces.xmlns_cm4f).setText("764"));
		
            if (model.contains("cm:Context") || model.contains("cm:Corpus")) {
            	Element id = new Element("identifier",Namespaces.xmlns_dcterms);
            	id.setAttribute("resource", "http://apache:82/"+oid, Namespaces.xmlns_rdf);
            	desc.addContent(id);
            }
            
            List<Element> list = (List<Element>) qpath.selectNodes(rels_ext);
			if (list !=null ) {
				for (Element rels: list) {
					Element el = (Element) rels;
					Attribute res = el.getAttribute("resource", Namespaces.xmlns_rdf);
					if (res.getValue().contains("/undefined")) {
						el.getParent().removeContent(el);
					} else {
						el.setAttribute("resource", "http://apache:82/"+rename(StringUtils.substringAfterLast(res.getValue(), "/")), Namespaces.xmlns_rdf);
					}
				}	
			}
			
			for (Element dc: (List<Element>) xpath.selectNodes(builder.build(new ByteArrayInputStream(source.stubGetDatastream(pid, "DC")))))  {
			    Element el = (Element) dc.clone();
			    Attribute lang = dc.getAttribute("lang", Namespaces.xmlns_xml);
			    if (lang != null) el.setAttribute("lang", lang.getValue(), Namespaces.xmlns_xml);
				desc.addContent(el);		
		    }	
				
			if (target.stubExist(pid, null) != 200) {
				target.stubCreateObject(oid, owner, new XMLOutputter().outputString(rels_ext).getBytes(StandardCharsets.UTF_8));	
			} else {
				
			}
			
			for (DatastreamListEntry ds:datastreams) {				
				if (!IShared.skipDatastreams.contains("|"+ds.getDsid()+"|") && !ds.getDsid().startsWith("TBN.")) {	
				   if (ds.getDsid().equals("BIBTEX") ) {
				       if (!(model.contains("cm:TEI") || model.contains("cm:PDF") || model.contains("cm:LaTeX")  || model.contains("cm:Resource") || model.contains("cm:BibTeX"))) {
				           continue;
				       }
				   }
				   if ("XM".contains(ds.getControlgroup())) { 
					  if (ds.getDsid().equals("TOMETS") && !pid.contains("cirilo:")) continue;
					  if (ds.getDsid().equals("QR") && pid.contains("cirilo:")) continue;
					  if (ds.getDsid().equals("QUERY") && !pid.matches("(context:|collection:|container:|query:|corpus:|cirilo:Context).*")) continue; 
					  byte[] stream = null; 
					  i=0;
					  try {
						  log.info(pid + " | " + 	ds.getDsid());
						  while (true) {
							  try {	 
								  if (ds.getMimetype().contains("xml") && !ds.getDsid().contains("_SOURCE") && !ds.getDsid().contains("RDF"))  {
									  Document dst = builder.build(new ByteArrayInputStream(source.stubGetDatastream(pid, ds.getDsid())));
									  stream = (new XMLOutputter().outputString(dst).replaceAll("container:", "context:")).getBytes(StandardCharsets.UTF_8);
								  }	else {
									  stream = source.stubGetDatastream(pid, ds.getDsid());
									  if (new String(stream, "UTF-16").contains("UTF-16")) {
										  stream = new String(stream, "UTF-16").replaceAll("UTF-16", "UTF-8").getBytes("UTF-8");  
									  }
								  }
								  break;
							  } catch (Exception e) {
									 if (i++ == 0) log.info("Waiting for connection to source host");
									 String err = e.getMessage();
									 if (!err.contains("Service Unavailable") && !err.contains("403") && !err.contains("java.net.UnknownHostException")) { 
										 log.error(String.format("Unrecoverable error while reading datastream %s of object %s, message: %s", ds.getDsid(), pid, err));
										 break;
									 }
									 Thread.sleep(4000);	
									 try {
										 source.stubOpenConnection("http", f3_repo, f3_username, f3_passwd);
									 } catch (Exception q) {
									 }

							  }
						  }
					      target.stubAddDatastream(oid, ds.getDsid(), stream, ds.getMimetype(), null);

					  } catch (Exception eq) {
					      log.error(String.format("Unrecoverable error during ingest of object %s, message: %s", oid, eq.getMessage()));
					  }
				   }
				}
			}	
			log.info(String.format("(%6d) object <%s> ok", index+1, oid));
			target.stubSetCreated(oid, source.getCreated(oid));

			target.stubTxCommit();
			
			counter[1]++;
			
		} catch (Exception e) {
			try {
				target.stubTxRollback();	
			} catch (FedoraException q) {}
		    
			log.error(String.format("Unrecoverable error during ingest of object %s, message: %s ", oid, e.getMessage()));
		    counter[2]++;
		}
		
	}

	public int[] getStat() {
		return counter;
	}
	
	private String rename(String pid) {
		return pid.replaceAll("container:", "context:").replaceAll("collection:", "context:");
	}
	
}
