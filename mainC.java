package org.dataone.nescent;

/*
 * LOD4DataONE prototype project for extracting data from the three DataONE repositories
 *    ORNL DAAC, KNB and Dryad to make the data linkable and browsable from a
 *    linked open data cloud (e.g.,http://lod-cloud.net).  For demonstration 
 *    purposes all RDF is published on my research portal.
 * In a more general sense this class simulates what a server providing RDF data might do
 *    -- map internal Dryad data, from a database, to RDF
 *    -- return RDF data for Dryad datasets
 * There is additional functionality to make this demo work, like specifically selecting datasets,
 *    linking internal data based on the Dryad naming and linking data to external sources like
 *    dbpedia.  This would be a part of the components that make up Dryad publishing.
 * The javadoc provided for this prototype is for documenting the functionality within the 
 *    prototype, not for documenting an API.  mainC contains the main function for this prototype
 * 
 * @package org.dataone.nescent
 * 
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.xmlrpc.XmlRpcException;
import org.dataone.nescent.DataSets;

import edu.utep.cybershare.ciclient.CIPut;
import edu.utep.cybershare.ciclient.CIReturnObject;
import edu.utep.cybershare.ciclient.CIUtils;
import edu.utep.cybershare.ciclient.ciconnect.CIClient;
import edu.utep.cybershare.ciclient.ciconnect.CIKnownServerTable;

/**
 * Main class that directs the creation of RDF from the three DataONE data repositories
 * 
 * @name mainC
 * @author Aida Gandara, http://trust.cs.utep.edu/members/aida/foaf.rdf
 * 
 */
public class mainC {
	
	// information needed for access to my research server
	private static final String _serverURL = "http://rio.cs.utep.edu/ciserver/";
	private static CIClient _ciClient;
	private static final String _DryadDATARDFFile = "DryadData.rdf";
	private static final String _DryadTYPESRDFFile = "DryadTypes.rdf";
	private static final String _KNBDATARDFFile = "KNBData.rdf";
	private static final String _KNBTYPESRDFFile = "KNBTypes.rdf";
	private static final String _FOAFRDFFile = "samplefoaf.rdf";
	private static final String _PROJECT = "LOD4DataONE";
	private static int _serverId;
	private static CIKnownServerTable _kst;
	
	/**
	 * PRIVATE: default constructor for this class is not available
	 * 
	 * @name mainC
	 * 
	 */
	private mainC(){}

	/** 
	 * sets up server information then makes calls to generate and publish RDF from
	 *   the three DataONE repositories
	 * 
	 * @name main
	 * @param args  an array of strings to pass as arguments to the command line of this program
	 * 
	 */
	public static void main(String[] args) {

		// Server admin to determine file placement and URI naming
		_kst = CIKnownServerTable.getInstance();
		_serverId = _kst.ciGetServerEntryFromURL(_serverURL);
		if(_serverId==-1){
			System.out.println("Server "+_serverURL+" not known to this client");
		}
		try{
			_ciClient = new CIClient(_serverId);
			// gets Dryad RDF and puts on server
			getDryadData();
			// gets KNB RDF and puts on server
			// getKNBData();
			// gets ORNL DAAC RDF and puts on server
			// TBD  getORNLDAACData();
		}
		catch(XmlRpcException xex){
			System.out.println("XML Rpc Exception: "+xex.getMessage());
			xex.printStackTrace();			
		}
		catch(Exception e){
			System.out.println("Connection Failed: "+e.getMessage());
			e.printStackTrace();
		}		
	}
	 
	/** 
	 * handles getting the Dryad data: sets up the datasets that will be translated to RDF, performs the translation to RDF then publishes the RDF to my research server
	 * 
	 * @name getDryadData
	 * 
	 */
	public static void getDryadData() {

		// Setup the mapper class
		D1DryadOAIPMHMapper d1m = new D1DryadOAIPMHMapper();
		// Get the data for the different datasets
		d1m.setupDryadCloudDataSets();
		// Obtain the Dryad RDF string
		String rdfString = d1m.getDryadCloudRDF();
		
		//This code uploads files to the ciserver.  Users need to setup a config file with
		// their user authentication
	    try {
	      // Output to a local file for admin purposes
	      Writer out = new OutputStreamWriter(new FileOutputStream("C:/data/dryad/"+_DryadDATARDFFile));
	      out.write(rdfString);
	      out.close();
	      CIReturnObject ro = new CIReturnObject();

	      // assure the session is authenticated.  If not, try again
	      if(_kst.ciGetServerAuthSession(_serverId)==null){
	    	  ro = _ciClient.ciAuthenticateSession(_kst.ciGetServerUsername(_serverId),_kst.ciGetServerPassword(_serverId));
	      }
	      if(_kst.ciGetServerAuthSession(_serverId)==null){
	    	  System.out.println("Authentication failed: " + ro.gMessage);
	      }else{
	    	  // upload the data file
	    	  System.out.println("Authentication successful for "+_kst.ciGetServerUsername(_serverId)+" on "+_serverURL);
	    	  ro = CIPut.ciUploadFile(_ciClient, _PROJECT, _DryadDATARDFFile,
	    	  		  rdfString, "sdata", true, false);
	    	  if(ro.gStatus.equals("0")){
	    	      System.out.println("Object uploaded, referenced via url : "+ro.gFileURL);
	    	  }else{
	    		  System.out.println("Upload failed: "+ro.gMessage);
	    	  }	 
	    	  // upload the types file
	    	  String tmpStr = CIUtils.ciReadFileAsString("C:/data/dryad/"+_DryadTYPESRDFFile);
	    	  ro = CIPut.ciUploadFile(_ciClient, _PROJECT, _DryadTYPESRDFFile,
	    	  		  tmpStr, "sdata", true, false);
	    	  if(ro.gStatus.equals("0")){
	    	      System.out.println("Object uploaded, referenced via url : "+ro.gFileURL);
	    	  }else{
	    		  System.out.println("Upload failed: "+ro.gMessage);
	    	  }	
	    	  
	    	  
	    	  // KNB and Dryad are using this foaf file - only need to upload it once after it is changed
	    	  // so this might be commented out at times
	  /* KEEP
	    	  tmpStr = CIUtils.ciReadFileAsString("C:/data/"+_FOAFRDFFile);
	    	  ro = CIPut.ciUploadFile(_ciClient, _PROJECT, _FOAFRDFFile,
	    	  		  tmpStr, "sdata", true, false);
	    	  if(ro.gStatus.equals("0")){
	    	      System.out.println("Object uploaded, referenced via url : "+ro.gFileURL);
	    	  }else{
	    		  System.out.println("Upload failed: "+ro.gMessage);
	    	  }
	    */
	      }
	    }
	    catch(FileNotFoundException fex){
	    	System.out.println("File Not Found Exception: "+fex.getMessage());
	    }
	    catch(IOException ioex){
	    	System.out.println("IO Exception: "+ioex.getMessage());
	    }
	}
	
	/** 
	 * handles getting the KNB data: sets up the datasets that will be translated to RDF,
	 *   performs the translation to RDF then publishes the RDF to my research server
	 * 
	 * @name getDryadData
	 * 
	 */
	public static void getKNBData() {
		
		// Setup the mapper class
		D1KNBMetacatMapper d1m = new D1KNBMetacatMapper();
		// Get the data for the different datasets
		d1m.setupMetacatCloudDataSets();
		// Return the rdf string
		String rdfString = d1m.getMetacatCloudRDF();
		System.out.println(rdfString);
		
		//This code uploads files to the ciserver.  Users need to setup a config file with
		// their user authentication
	    try {
	      // for admin purposes make a local copy in c:/data
	      Writer out = new OutputStreamWriter(new FileOutputStream("C:/data/knb/"+_KNBDATARDFFile));
	      out.write(rdfString);
	      out.close();
	      CIReturnObject ro = new CIReturnObject();

	      //assure the session is authenticated - try again if not
	      if(_kst.ciGetServerAuthSession(_serverId)==null){
	    	  ro = _ciClient.ciAuthenticateSession(_kst.ciGetServerUsername(_serverId),_kst.ciGetServerPassword(_serverId));
	      }
	      if(_kst.ciGetServerAuthSession(_serverId)==null){
	    	  System.out.println("Authentication failed: " + ro.gMessage);
	      }else{
	    	  // once authenticated - can upload files
	    	  System.out.println("Authentication successful for "+_kst.ciGetServerUsername(_serverId)+" on "+_serverURL);
	    	  
	    	  // upload the KNB data file
	    	  ro = CIPut.ciUploadFile(_ciClient, _PROJECT, _KNBDATARDFFile,
	    	  		  rdfString, "sdata", true, false);
	    	  if(ro.gStatus.equals("0")){
	    	      System.out.println("Object uploaded, referenced via url : "+ro.gFileURL);
	    	  }else{
	    		  System.out.println("Upload failed: "+ro.gMessage);
	    	  }	 
	    	  
	    	  // upload the KNB types file - this was created manually and is in a local
	    	  // directory so it must be read then uploaded
	    	  String tmpStr = CIUtils.ciReadFileAsString("C:/data/knb/"+_KNBTYPESRDFFile);
	    	  ro = CIPut.ciUploadFile(_ciClient, _PROJECT, _KNBTYPESRDFFile,
	    	  		  tmpStr, "sdata", true, false);
	    	  if(ro.gStatus.equals("0")){
	    	      System.out.println("Object uploaded, referenced via url : "+ro.gFileURL);
	    	  }else{
	    		  System.out.println("Upload failed: "+ro.gMessage);
	    	  }	
	    	  
	    	  // KNB and Dryad are using this foaf file - only need to upload it once after it is changed
	    	  // so this might be commented out at times
	/*  KEEP
	    	  tmpStr = CIUtils.ciReadFileAsString("C:/data/"+_FOAFRDFFile);
	    	  ro = CIPut.ciUploadFile(_ciClient, _PROJECT, _FOAFRDFFile,
	    	  		  tmpStr, "sdata", true, false);
	    	  if(ro.gStatus.equals("0")){
	    	      System.out.println("Object uploaded, referenced via url : "+ro.gFileURL);
	    	  }else{
	    		  System.out.println("Upload failed: "+ro.gMessage);
	    	  }
	*/
	      }
	    }
	    catch(FileNotFoundException fex){
	    	System.out.println("File Not Found Exception: "+fex.getMessage());
	    }
	    catch(IOException ioex){
	    	System.out.println("IO Exception: "+ioex.getMessage());
	    } 
	}

}
