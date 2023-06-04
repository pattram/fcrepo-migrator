package org.emile.cirilo;

import org.apache.log4j.Logger;
import org.emile.cirilo.fedora.Fedora3Connector;
import org.emile.cirilo.fedora.FedoraConnector;

public class Migrator {
  
     private static Logger  log = Logger.getLogger("org.emile.cirilo.Migrator");
     
     private static String f3_repo;
     private static String f3_username;
     private static String f3_passwd;
     private static String f3_owner;
     
     private static String fcrepo_host;
     private static String fcrepo_username;
     private static String fcrepo_passwd;
     
     private static String exclude = null; 
     
     private static boolean stats;
     
     private static boolean rebuilder;
     
	 private static Fedora3Connector source = new Fedora3Connector();
	 private static FedoraConnector target = new FedoraConnector();
	 
	 
     public static void main (String args[]) {

    	 int i = 0;
    	 f3_owner = null;
    	 stats = false;
    	 rebuilder = false;

    	 try {	 
         
      		 while (i < args.length) {
     			 if (args[i].equals("-f3")) f3_repo = args[i+1].trim();
     			 if (args[i].equals("-u3")) f3_username = args[i+1].trim();
     			 if (args[i].equals("-p3")) f3_passwd = args[i+1].trim();
     			 if (args[i].equals("-o")) f3_owner = args[i+1].trim();
     			 if (args[i].equals("-e")) exclude = args[i+1].trim();
    			 if (args[i].equals("-f4")) fcrepo_host = args[i+1].trim();
     			 if (args[i].equals("-u4")) fcrepo_username = args[i+1].trim();
     			 if (args[i].equals("-p4")) fcrepo_passwd = args[i+1].trim();
    			 if (args[i].equals("-s")) stats = true;
    			 if (args[i].equals("-r")) rebuilder = true;
    			 i += 2;
     		 }
    		  
      		 
      		 if (rebuilder) {
    		    
      			 System.out.println("Rebuilder: Fedora 3.x (c) 2023 Johannes Stigler, Version 1.0.1");
        	     source.stubOpenConnection("https", f3_repo, f3_username, f3_passwd);
         	     log.info("Connection to https://"+ f3_repo+ " ok");
         	     RebuilderFactory rf = new RebuilderFactory(source);
         	     rf.replace();
         	    
      		 } else if (args.length > 11) {
    		     
      			 System.out.println("Migrator: Fedora 3.x to 6.0.0 (c) 2020 Johannes Stigler, Version 1.0.1");
       		    		 
         	     source.stubOpenConnection("https", f3_repo, f3_username, f3_passwd);
         	     log.info("Connection to https://"+ f3_repo+ " ok");
 			     target.stubOpenConnection("https", fcrepo_host, fcrepo_username, fcrepo_passwd );	 
 			     log.info("Connection to https://"+ fcrepo_host + " ok");
 			 
         	     MigrationFactory mf = new MigrationFactory(source, target, f3_owner, f3_repo, f3_username, f3_passwd, exclude);
         	 
         	     for (int j = 0; j <mf.size(); j++) {
         		    if (stats) mf.analyseObject(j); else mf.createObject(j);
         	     }
         	 
                 if (stats) {
            	    mf.printStats();
                 } else {        	
            	   int[] counter = mf.getStat();
     	   		   System.out.println(counter[0]+ " objects in source repository found.");
     	   		   System.out.println(counter[1] + " objects of source repository could migrated successfully");
     	   		   System.out.println("In "+ counter[2] + " objects an error occurred");
                 }
                
      		 } else {
      			 
         		 System.out.println("Usage : Migrator options");
           		 System.out.println("          -f3 <hostname/fedora-context of source repository>");
        		 System.out.println("          -u3 <username>");
        		 System.out.println("          -p3 <password>");
           		 System.out.println("          -o  <select only objects of the given owner>");
           		 System.out.println("          -e  <exclude objects of this comma separated owner list>");
          		 System.out.println("          -f4 <hostname of target repository>");
        		 System.out.println("          -u4 <username>");
        		 System.out.println("          -p4 <password>");
        		 
           		 System.exit(1);
			 
      		 }
    		   
         	System.out.println("Program terminated normally");
         	System.exit(0);
         	 
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 log.error(e);
    	     System.exit(1);
    	 }
    	 
     } 
    
}