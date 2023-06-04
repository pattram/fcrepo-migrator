package org.emile.cirilo;

import java.util.HashMap;

import org.emile.cirilo.models.FedoraEnvironment;
import org.jdom.Namespace;


public class IShared {
		
	public final static int MAX_OBJECTS = 1000;
	
	public final static String QUERY = 
			"select distinct ?pid ?title ?model ?owner ?handle ?modified where {"+
		     "?object <dc:title> ?title . ?object <dc:identifier> ?pid ."+
		     "?object <fedora-view:lastModifiedDate> ?modified ."+
		     "?object <info:fedora/fedora-system:def/model#ownerId> ?owner ."+
		     "?object <info:fedora/fedora-system:def/model#hasModel> ?model  . " +
		     "optional { ?object <http://www.openarchives.org/OAI/2.0/itemID> ?handle } " +
             "filter ( ( !regex(str(?model), '^info:fedora/fedora-system:') $filter ) ) } limit $limit"; 

	public final static String SPARQL = 
			"select distinct ?pid ?model where { ?object <dc:identifier> ?pid . "+
            "?object <info:fedora/fedora-system:def/model#hasModel> ?model  . $owner " +
            "filter ((!regex(str(?model), '^info:fedora/fedora-system:') && regex(?pid,'^(o:|context:|container:|collection:|query:|corpus:|cirilo:)','i') && !regex(?pid,'^o:(yoda)','i') && !regex(?pid,'^cirilo:[a-z]+[.][A-Z]+','i') $exclude) ) }"; 

	public final static String SPARQLALL = 
			"select distinct ?pid where { ?object <dc:identifier> ?pid . "+
            "?object <info:fedora/fedora-system:def/model#hasModel> ?model . " +
            "filter ((!regex(str(?model), '^info:fedora/fedora-system:') && regex(?pid,'^(o:|context:|container:|collection:|query:|corpus:|cirilo:)','i') ) ) }"; 

	public final static String skipDatastreams = "|RELS-INT|RELS-EXT|BIBTEX_MAPPING|METS_REF|SOURCE_REF|KML_REF|METHODS|METADATA|RDF_MAPPING|REPLACEMENT_RULESET|REPOSITORY|MODStoBIBTEX_MAPPING|MODStoDC_MAPPING|PID|"+
            "|SPARQL2JSON|CONTEXTtoMIRADORHTML|METS2JSON|XML2JSON|OBJECTtoMIRADORHTML|EDMtoDC_MAPPING|LIDOtoDC_MAPPING|MEItoDC_MAPPING|OAItoDC_MAPPING|STORYtoDC_MAPPING|TEItoDC_MAPPING|";

	public final static String CREATED = 
			"select ?created where { ?object <dc:identifier> '$pid' . "+
            "?object <info:fedora/fedora-system:def/model#createdDate> ?created . }"; 

	
	public static final HashMap<String,String>bindings = new HashMap<String,String>() { { 
		put("DC_MAPPING"  ,  "hasXsltToDC");
		put("BIBTEX_MAPPING"  ,  "hasXsltToBIBTEX");
		put("FO_STYLESHEET" ,  "hasXsltToFO");
		put("HSSF_STYLESHEET" ,  "hasXsltToHSSF");
		put("LATEX_STYLESHEET"  ,  "hasXsltToLATEX");
		put("SNIPPET_STYLESHEET"  ,  "hasXsltToXML_Snippet");
		put("R_STYLESHEET"  ,  "hasXsltToR_Dataset");
		put("VOYANT"  ,  "hasVoyantRequest");
		put("STYLESHEET" ,  "hasXsltToHTML");
		put("GMLTOJSON" ,  "hasXsltToJSON");
		put("GMLTORDF" ,  "hasXsltToGeoRDF_Triples");
		put("TORDF"  ,  "hasXsltToRDF_Triples");
		put("URL"   ,  "hasResourceLocator");	
        put("TODSPIN"   ,  "hasXsltToDSPIN");   
        put("TOCMDI"   ,  "hasXsltToCMDI");   
        put("TOTCF"   ,  "hasXsltToTCF");   
        put("URL"   ,  "hasResourceLocator");   
		
		//reverse bindings 
		put("XsltToDC"  ,  "hasXsltToDC");
		put("XsltToBIBTEX"  ,  "hasXsltToBIBTEX");
		put("XsltToFO" ,  "hasXsltToFO");
		put("XsltToHSSF" ,  "hasXsltToHSSF");
		put("XsltToLATEX"  ,  "hasXsltToLATEX");
		put("XsltToXML_Snippet"  ,  "hasXsltToXML_Snippet");
		put("XsltToR_Dataset"  ,  "hasXsltToR_Dataset");
		put("XsltToHTML" ,  "hasXsltToHTML");
		put("XsltToJSON" ,  "hasXsltToJSON");
		put("XsltToGeoRDF_Triples" ,  "hasXsltToGeoRDF_Triples");
		put("XsltToRDF_Triples",	 "hasXsltToRDF_Triples");
		put("Customization",	 "hasXsltForCustomization");

	}};
}
