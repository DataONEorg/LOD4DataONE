package org.dataone.nescent;

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

public class mainC {
	
	private static String _serverURL = "http://rio.cs.utep.edu/ciserver/";
	private static CIClient _ciClient;
	// 06/17/2011 
	//private static String _RDFFile = "dryadRDFWeek2.rdf";
	private static String _RDFFile = "DryadData.owl";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CIKnownServerTable _kst = CIKnownServerTable.getInstance();
		int serverId = _kst.ciGetServerEntryFromURL(_serverURL);
		if(serverId==-1){
			System.out.println("Server "+_serverURL+" not known to this client");
		}
		try{
			_ciClient = new CIClient(serverId);
		}
		catch(XmlRpcException xex){
			System.out.println("XML Rpc Exception: "+xex.getMessage());
			xex.printStackTrace();			
		}
		catch(Exception e){
			System.out.println("Connection Failed: "+e.getMessage());
			e.printStackTrace();
		}
		
		// Setup the data sets to retrieve
		D1DryadOAIPMHMapper d1m = new D1DryadOAIPMHMapper();
		d1m.setupDryadCloudDataSets();
		String rdfString = d1m.getDryadCloudRDF();
	    try {
	      Writer out = new OutputStreamWriter(new FileOutputStream("C:/data/"+_RDFFile));
	      out.write(rdfString);
	      out.close();
	      CIReturnObject ro = new CIReturnObject();
	      // authentication setup in config file, should not need following line
	      // CIReturnObject ro = _ciClient.ciAuthenticateSession(_userName, _password);
	      if(_kst.ciGetServerAuthSession(serverId)==null){
	    	  ro = _ciClient.ciAuthenticateSession(_kst.ciGetServerUsername(serverId),_kst.ciGetServerPassword(serverId));
	      }
	      if(_kst.ciGetServerAuthSession(serverId)==null){
	    	  System.out.println("Authentication failed: " + ro.gMessage);
	      }else{
	    	  System.out.println("Authentication successful for "+_kst.ciGetServerUsername(serverId)+" on "+_serverURL);
	    	  //String tmpStr = CIUtils.ciReadFileAsString("C:/data/DryadData.");
	    	  ro = CIPut.ciUploadFile(_ciClient, "LOD4DataONE", _RDFFile,
	    	  		  rdfString, "sdata", true, false);
	    	  if(ro.gStatus.equals("0")){
	    	      System.out.println("Object uploaded, referenced via url : "+ro.gFileURL);
	    	  }else{
	    		  System.out.println("Upload failed: "+ro.gMessage);
	    	  }	 
	    	  // some auxiliary files - types and foaf
	    	  String tmpStr = CIUtils.ciReadFileAsString("C:/data/DryadTypes.owl");
	    	  ro = CIPut.ciUploadFile(_ciClient, "LOD4DataONE", "DryadTypes.owl",
	    	  		  tmpStr, "sdata", true, false);
	    	  if(ro.gStatus.equals("0")){
	    	      System.out.println("Object uploaded, referenced via url : "+ro.gFileURL);
	    	  }else{
	    		  System.out.println("Upload failed: "+ro.gMessage);
	    	  }	
	    	  tmpStr = CIUtils.ciReadFileAsString("C:/data/samplefoaf.rdf");
	    	  ro = CIPut.ciUploadFile(_ciClient, "LOD4DataONE", "samplefoaf.rdf",
	    	  		  tmpStr, "sdata", true, false);
	    	  if(ro.gStatus.equals("0")){
	    	      System.out.println("Object uploaded, referenced via url : "+ro.gFileURL);
	    	  }else{
	    		  System.out.println("Upload failed: "+ro.gMessage);
	    	  }
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
