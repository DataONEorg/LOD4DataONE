package org.dataone.nescent;

import org.dataone.nescent.DataSets;
import org.dataone.nescent.DataSet;

import com.hp.hpl.jena.rdf.model.Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 



/// Maybe I will be able to create a generic OAI-PMH interface
public class D1DryadOAIPMHMapper{  // nothing gained here yet extends D1DataXRDFMapper{
	
	// namespaces affect the naming - what should it be for dryad
	// in addition to affect source

	/*** this was the namespace for oai - I have changed it for rdf 
	 *   to _DRYAD_NS
	 *   public final String _OAI_NS = "xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\"";
	 */
	public final String _RDF_HEADER = "<rdf:RDF \n"+
    "\txmlns:dryadt=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadTypes.owl#\" \n"+
    "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"+
    "\txmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"+
    "\txmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"+
    "\txmlns:dcterms=\"http://purl.org/dc/terms/\"\n"+
    "\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"+
    "\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"+
    "\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" > \n";
	/*** do I need these, from the meta harvest **/
		// the xml schema namespace - not adding it yet, I think I need rdfschema?? - already added??
	public final String _XML_SCHEMA_NS = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";
	public final String _OAI_DC_NS = "xmlns:oai_dc=\"http:/rio.cs.utep.edu/ciserver/ciprojects//\"";
	
	//public final String _DRYAD_D1_TREEBASE = "http://datadryad.org/handle/10255/dryad.82";
	public final String _DRYAD_D1_TREEBASE = "oai:datadryad.org:10255/dryad.82";
	public final String _DRYAD_D1_TREEBASE_F1 = "oai:datadryad.org:10255/dryad.83";
	public final String _DRYAD_D2_GENBANK = "oai:datadryad.org:10255/dryad.8437";
	public final String _DRYAD_D3_POPULAR = "oai:datadryad.org:10255/dryad.234";
	public final String _DRYAD_D4_CSV = "oai:datadryad.org:10255/dryad.1252";
	public final String _DRYAD_D5_CSV = "oai:datadryad.org:10255/dryad.1295";
	public final String _DRYAD_D6_CSV = "oai:datadryad.org:10255/dryad.2016";
	
	public final String _DRYAD_HDL_STR = "http://hdl.handle.net/";

	public final String[] _DRYAD_OAI_REPLACE_STRINGS = {"oai:datadryad.org:10255/", "http://hdl.handle.net/", "http://hdl.handle.net/10255/","http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DataDryad.owl#"};
	public final String _DRYAD_DC_IDENTIFIER = "identifier";
	public final String _DRYAD_XLINK_REF = "xlink:href";
	public final String _DRYAD_GETRECORD = "http://www.datadryad.org/oai/request?verb=GetRecord&identifier=<r_id>&metadataPrefix=oai_dc";


	public final String _DRYAD_METS_GET_DATA = "http://datadryad.org/metadata/handle/<short_id>/mets.xml";
	public final String _DRYAD_METS_BITSTREAM_LOCATE = "mets:FLocat";
	public final String _DRYAD_METS_FILE = "mets:file";
	public final String _DRYAD_EXCEL_MIMETYPE_ATTRIBUTE = "application/vnd.ms-excel";
	
	public final String _DRYAD_SHORT_ID_REPLACEMENT_STRING = "<short_id>";
	
	private ArrayList<DryadDataSet> _datasets;
	
	public D1DryadOAIPMHMapper(){
		_datasets = new ArrayList<DryadDataSet>();
	}
	
	// TODO: still need to determine what mappings there might be from data types and rdf structures
	// TODO: still need to determine where I start filtering, by dataset ids for particular groups
	//       or file types
	
	/*** 
	 * getDataSetIds
	 *   setting 
	 */
	public void setupDryadCloudDataSets(){
		try{
			
			// this should be a query - returning a list of DataSet IDs
			
			DryadDataSet ds = new DryadDataSet(_DRYAD_D1_TREEBASE);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			System.out.println("========= RDF =========");
			System.out.println(ds.getRDFString());
			System.out.println("========= END RDF =========");
			ds = new DryadDataSet(_DRYAD_D1_TREEBASE_F1);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			System.out.println("========= RDF =========");
			System.out.println(ds.getRDFString());
			System.out.println("========= END RDF =========");
/*  ERROR in data
			ds = new DryadDataSet(_DRYAD_D2_GENBANK);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			System.out.println("========= RDF =========");
			System.out.println(ds.getRDFString());
			System.out.println("========= END RDF =========");
*/
			/** temp out
			ds = new DryadDataSet(_DRYAD_D3_POPULAR);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			System.out.println("========= RDF =========");
			System.out.println(ds.getRDFString());
			System.out.println("========= END RDF =========");
			ds = new DryadDataSet(_DRYAD_D4_CSV);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			System.out.println("========= RDF =========");
			System.out.println(ds.getRDFString());
			System.out.println("========= END RDF =========");
			**/
/*  ERROR in data
			ds = new DryadDataSet(_DRYAD_D5_CSV);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			System.out.println("========= RDF =========");
			System.out.println(ds.getRDFString());
			System.out.println("========= END RDF =========");
*/
			/* temp out
			ds = new DryadDataSet(_DRYAD_D6_CSV);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			System.out.println("========= RDF =========");
			System.out.println(ds.getRDFString());
			System.out.println("========= END RDF =========");
			*/
		}
		catch(Exception ex){
			System.out.println("ERROR: Exception "+ex.getMessage());
		}
	}
	
	public String getDryadCloudRDF(){
		String rdfString = _RDF_HEADER + "\n";

		// should be getting this from headerStrings ... but would
		// have to consolidate dups
		/////////////////////////////////////////////////////////////
		for(Iterator<DryadDataSet> i = _datasets.iterator(); i.hasNext();){
			DryadDataSet d = (DryadDataSet)i.next();
			rdfString+= d.getRDFString();
		}
		rdfString+="</rdf:RDF>\n";
		
		return rdfString;
	}
	
	/***
	 * 
	 * @param ids
	 * @return
	 */
	
	// 
	public String getRDFFromDataSet(DataSets ids){
		String rdfString = "";
		
		try{

			for(Iterator<DataSet> i = ids.iterator(); i.hasNext();){
				DataSet ds = (DataSet)i.next();
				
				
			// for each dataset, get it from the server
			// convert it to rdf
			/*
			System.out.println("Get TREEBASE");
			BufferedReader dd = this.readURL(new URL(_DRYAD_GET_D1_TREEBASE));
			
			String inputLine;
			while ((inputLine = dd.readLine()) != null)
			    System.out.println(inputLine);
			System.out.println("Get GENBANK");
			dd = this.readURL(new URL(_DRYAD_GET_D2_GENBANK));
			while ((inputLine = dd.readLine()) != null)
			    System.out.println(inputLine);
			System.out.println("Get POPULAR");
			dd = this.readURL(new URL(_DRYAD_GET_D3_POPULAR));
			while ((inputLine = dd.readLine()) != null)
			    System.out.println(inputLine);
			System.out.println("Get D4 CSV");
			dd = this.readURL(new URL(_DRYAD_GET_D4_CSV));
			while ((inputLine = dd.readLine()) != null)
			    System.out.println(inputLine);
			System.out.println("Get D5 CSV");
			dd = this.readURL(new URL(_DRYAD_GET_D5_CSV));
			while ((inputLine = dd.readLine()) != null)
			    System.out.println(inputLine);
			System.out.println("Get D6 CSV");
			dd = this.readURL(new URL(_DRYAD_GET_D6_CSV));
			while ((inputLine = dd.readLine()) != null)
			    System.out.println(inputLine);
			*/
			}

		}
		catch(Exception ex){
			System.out.println("EXCEPTION: "+ex.getMessage());
			
		// for every bistreamURL
		//  assure we know the type (we only handle the ones we know)
		//  load the metadata definition (e.g., the ontology or rdf structure)
		//		?? dryad-dev how could I know this, by MIMETYPE and Group ID?
		//  extract the data from the given URL 
		//			(e.g. http://datadryad.org/bitstream/handle/10255/dryad.57/Sidlauskas%202007%20Landmark%20Consensuses.tps?sequence=1)
		//		determine object type
		//		get fields, determine field names
		//		build rdf objects
		//  open the rdf file - if multiple this will be the filename_bitstreamID??
		//  add to rdf file
		//  close rdf file
		}
		
		return rdfString;
	}
	
	private class DryadDataSet {
		private String _ds_shortid;
		private URL _ds_url;
		private String _ds_metadata_rdf = null;
		private String _ds_data_rdf = null;
		private URL _bitstream_url;
		private byte[] _bitstream;
		private String _dsType;
		private ArrayList<DryadDataSet> _hasParts = null;
		
		// nice if I were consolidating rdf strings
		// for now, not doing it
		// private ArrayList<String> _headerStrings;
		
		public DryadDataSet( String short_id){
			_ds_shortid = short_id;
		}
		
		/* very specific code - given an ID - return data that would be browsable
		 *   on LOD : linkable content
		 *   ASSUMES: classes defined rdf:type - I created a DataTypes.owl for now
		 */
		public void harvestMetadataToRDF(){
			String harvestString = new String(_DRYAD_GETRECORD);
			// _ds_shortid is the id that will result in linkable content
			harvestString = harvestString.replace("<r_id>", _ds_shortid);
			String rdfString = "";
			String dataRDFString = "";
			String dataAboutName = "";
			_ds_metadata_rdf = "";
			_ds_data_rdf = "";

		    try {

	            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            Document doc = docBuilder.parse(harvestString);

	            // normalize text representation
	            doc.getDocumentElement().normalize ();
	            System.out.println ("Root element of the doc is " + 
	                 doc.getDocumentElement().getNodeName());
       		
	            NodeList mns = doc.getElementsByTagName("metadata");
	            String metaRDFString = "";
	            // should be only 1, just ignore others in case
	            ///////////////////////////////
	            if(mns.getLength()>0){
	            	Node metadataNode = mns.item(0);
	            	System.out.println(metadataNode.getNodeType());
	            	// just checking ... could probably skip
	            	if(metadataNode.getNodeType() == Node.ELEMENT_NODE){
	            		Element melement = (Element)metadataNode;
	            		// get all child nodes - I may need them
	            		// NOTICE: I tried to get all children but only found this
	            		//   is there a structure I can read to get this ?
	            		NodeList oai_dcl = melement.getElementsByTagName("oai_dc:dc");
	            		for(int i = 0; i<oai_dcl.getLength(); i++){
		            		Node oaidc_node = oai_dcl.item(0);
		            		System.out.println("the node type"+oaidc_node.getNodeType());
		            		System.out.println("to string: "+oaidc_node.toString());
		            		Element oai_elem = (Element)oaidc_node;
		            		NodeList dcterms = oai_elem.getChildNodes();
		            		int dcterm_count = dcterms.getLength();
		            		// could possibly have parts now
		            		_hasParts = new ArrayList<DryadDataSet>();
		            		for(int j = 0; j<dcterm_count; j++){
		            			Node dcterm = dcterms.item(j);
		            			/* do I need to handle this like this - I don't think so
		            			if(dcterm.getNodeName().equals("dc:relation") && dcterm.getTextContent().startsWith(_DRYAD_HDL_STR)){
		            				String oaiString = dcterm.getTextContent();
		            				oaiString = oaiString.replace(_DRYAD_OAI_REPLACE_STRINGS[1], _DRYAD_OAI_REPLACE_STRINGS[0]);
		            				DryadDataSet aPart = new DryadDataSet(oaiString);
			            			aPart.harvestMetadataToRDF();
			            			if(!aPart.getDatasetRDF().isEmpty()){
			            				_hasParts.add(aPart);
			            			}
		            			}
		            			*/
		            			if( dcterm.getNodeName().equals("dc:date") ||
		            				dcterm.getNodeName().equals("dc:subject") ||
		            				dcterm.getNodeName().equals("dc:title") ||
		            				dcterm.getNodeName().equals("dc:description") ||
		            				dcterm.getNodeName().equals("dc:coverage") ||
		            				dcterm.getNodeName().equals("dc:rights") ||
		            				dcterm.getNodeName().equals("dc:language") ||
		            				dcterm.getNodeName().equals("dc:type") ||
		            				dcterm.getNodeName().equals("dc:contributor") ||
		            				dcterm.getNodeName().equals("dc:identifier") ||
		            				dcterm.getNodeName().equals("dc:source") ||
		            				dcterm.getNodeName().equals("dc:format") ||
		            				dcterm.getNodeName().equals("dc:publisher") ){
		            				metaRDFString+="\t<"+dcterm.getNodeName()+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+dcterm.getTextContent()+
		            					"</"+dcterm.getNodeName()+">\n";
		            				// here I would handle the others
		            			}else if(dcterm.getNodeName().equals("dc:creator") && dcterm.getTextContent().equals("Gittleman, John L.") ){
		            				metaRDFString+="\t<dc:creator rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#JohnLGittleman\"/>\n";
		            			}else if(dcterm.getNodeName().equals("dc:creator") && dcterm.getTextContent().equals("Price, Samantha A.") ){
		            				metaRDFString+="\t<dc:creator rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#SamanthaAPrice\"/>\n";
		            			}else if(dcterm.getNodeName().equals("dc:relation") && dcterm.getTextContent().startsWith(_DRYAD_OAI_REPLACE_STRINGS[2]) ){
		            				String resourceString = dcterm.getTextContent().replace(_DRYAD_OAI_REPLACE_STRINGS[2], _DRYAD_OAI_REPLACE_STRINGS[3]);
		            				metaRDFString+="\t<dc:relation rdf:resource=\""+resourceString+"\"/>\n";
		            			}else{
		            				metaRDFString+="\t<"+dcterm.getNodeName()+">"+
		            					dcterm.getTextContent()+"</"+
		            					dcterm.getNodeName()+">\n";
		            			// tried to force data in some rdfs
		            			// metaRDFString+="\t<rdfs:label>"+dcterm.getTextContent()+"</rdfs:label>\n";
		            			}
		            			
		            			System.out.println("term "+dcterm.getNodeName()+" value "+dcterm.getNodeValue()+" text "+dcterm.getTextContent());
		            		}
		            		metaRDFString+="\t<rdf:type rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadTypes.owl#DCType\"/>\n";
	            		}
	            		
	            	}
	            }
	            
	            // I already had the following code before I found dc:indentifier.uri ... I am keeping it for now
	            //  in case dc:identifier.uri is not set ... I don't think it will be needed
	            if(dataAboutName.isEmpty()){
		            NodeList headerL = doc.getElementsByTagName("header");
		            boolean found = false;
		            // should be 1 but not checking
		            ///////////////////////////////
		            if(headerL.getLength()>0){
		            	Node headerNode = headerL.item(0);
		            	if(headerNode.getNodeType()==Node.ELEMENT_NODE){
		            		Element helement = (Element)headerNode;
		            		NodeList childL = helement.getChildNodes();
		            		int child_count = childL.getLength();
		            		for(int i = 0; i<child_count; i++){
		            			Node cterm = childL.item(i);
		            			if(cterm.getNodeName().equals("identifier")){
		            				found = true;
		            				// parse out the term name and add the dryad handle prefix http://datadryad.org/handle/
		            				// set dataAboutName to this ... or should the about be about something else? like the data file?
		            				// need the handle ... or the data files - but this could be many so point to this record
		            				dataAboutName = cterm.getTextContent();
		            				dataAboutName = dataAboutName.replace(_DRYAD_OAI_REPLACE_STRINGS[0], _DRYAD_OAI_REPLACE_STRINGS[3]);
		            				System.out.println("term "+dataAboutName+" value "+cterm.getNodeValue()+" text "+cterm.getTextContent());
		            				// this code should probably check the type and then get the data from mets
		            				// I am getting specific ones
		            				if(dataAboutName.endsWith("dryad.83")){
		            					// I know this has a dataset so add the link to get browsable data
		            					metaRDFString+="\t<dryadt:datafile "+
		            					"rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.owl#PriceGittleman_2007_append.xls\"/>\n";
		            					// build the data RDF for the xls file
		            					dataRDFString="<rdf:Description rdf:about=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.owl#PriceGittleman_2007_append.xls\">\n";
		            					dataRDFString+="\t<dryadt:filename "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">PriceGittleman_2007_appendix.xls"+
		            					"</dryadt:filename>\n";
		            					dataRDFString+="\t<dryadt:filesize "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">94.72Kb"+
		            					"</dryadt:filesize>\n";
		            					dataRDFString+="\t<dc:format "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Microsoft Excel"+
		            					"</dc:format>\n";
		            					dataRDFString+="\t<dryadt:species "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Addax nasomaculatus"+
		            					"</dryadt:species>\n";
		            					dataRDFString+="\t<dryadt:species "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Axis axis"+
		            					"</dryadt:species>\n";
		            					dataRDFString+="\t<dryadt:species "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Capra hircus"+
		            					"</dryadt:species>\n";
		            					dataRDFString+="\t<dryadt:species "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Kobus leche"+
		            					"</dryadt:species>\n";
		            					dataRDFString+="\t<dryadt:species "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Ovis aries"+
		            					"</dryadt:species>\n";
		            					dataRDFString+="\t<dryadt:species "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Pacari tajacu"+
		            					"</dryadt:species>\n";
		            					dataRDFString+="\t<dryadt:species "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Tayassu pecari"+
		            					"</dryadt:species>\n";
		            					dataRDFString+="\t<dryadt:species "+
		            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Vicugna vicugna"+
		            					"</dryadt:species>\n";
		            					dataRDFString+="\t<rdf:type rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadTypes.owl#DatafileType\"/>\n";
		            					dataRDFString+="</rdf:Description>\n";
		            				}
		            				break;
		            			}
		            		}
		            	}
		            }
		            if(dataAboutName.isEmpty())
		            	return;
		    	}
	            
	            // build the rdf for this resource
	            rdfString+="<rdf:Description rdf:about=\""+dataAboutName+"\">\n";
	            rdfString+=metaRDFString;
   			 	rdfString += "</rdf:Description>\n";
	        }catch (SAXParseException err) {
	        	System.out.println ("** Parsing error" + ", line " 
	             + err.getLineNumber () + ", uri " + err.getSystemId ());
	        	System.out.println(" " + err.getMessage ());

	        }catch (SAXException e) {
	        	Exception x = e.getException ();
	        	((x == null) ? e : x).printStackTrace ();

	        }catch (Throwable t) {
	        	t.printStackTrace ();
	        }
	  
	        _ds_metadata_rdf = rdfString;
	        _ds_data_rdf = dataRDFString;
		}
		
		public String getRDFString(){
			String rdfString = _ds_metadata_rdf + _ds_data_rdf;
		 	if(_hasParts!=null && !_hasParts.isEmpty()){
   			 	for(Iterator<DryadDataSet> h=_hasParts.iterator(); h.hasNext();){
   			 		DryadDataSet d = (DryadDataSet)h.next();
   			 		rdfString+=d.getRDFString();
   			 	}
		 	}
			return rdfString;
		}
		
		public void getDataFields(){
			//public final String _DRYAD_METS_GET_DATA = "http://datadryad.org/metadata/handle/<short_id>/mets.xml";
		}
		
		public void readDataSet(){
			// read the contents of the bitstream and
			// 
		}
		
		public String getId(){
			return _ds_shortid;
		}
		
		public URL _getBitstreamURL(){
			return _bitstream_url;
		}
		
		public String getDatasetRDF(){
			return "";
		}
		
		// this call would be nice if I were consolidating header
		// strings.  For now, all rdf comes together
		/*
		public ArrayList<String> getDatasetHeadersInRDF(){
			return null;
		}
		*/
	}

	
	/**
	 * 	public BitStreamURLs getBitstreamURLsFromDataSetIds(DataSetIds ids){
		BitStreamURLs bsu = null;
		
		// ex: record looks like:
		//  <mets:file ... MIMETYPE="text/plain" ...>
		//  <mets:FLocat 
		//    ...
		//  xlink:href="/bitstream/handle/10255/dryad.57/Sidlauskas%202007%20Landmark%20Consensuses.tps?sequence=1"
		//  ... /> </mets:file>
		
		// for every dataset identifier
		//    e.g. 10255/dryad.57 ; replace <short_id> with this
		//  get the metadata bitstream file
		//    parse data for file types we can handle (e.g. file MIMETYPE)
		//    parse data and get bitstream URLs (e.g. xlink:href)
		//    append the dryad url prefilx

		return bsu;		
	}
	**/
	/*	
    rdfString+=_DOCTYPE + "\n";
    rdfString+=_DCTERMS_NS + "\n";
	rdfString+=_DRI_NS + "\n";
	rdfString+=_I18N_2_1_NS + "\n";
	rdfString+=_EXSLT + "\n";
	rdfString+=_MODS_NS + "\n";
	rdfString+=_XLINK_NS + "\n";
	rdfString+=_DIM_NS + "\n";
	rdfString+=_METS_NS + "\n";
	rdfString+=_OREATOM_NS + "\n";
	rdfString+=_ORE_NS + "\n";
	rdfString+=_ATOM_NS + "\n";
	rdfString+=_XML_SCHEMA_NS + "\n";
	rdfString+=_OAI_DC_NS + ">\n";  // the last one should look like this
 */
	/*
	 * 	            		if(oai_dcl.getLength()>0){
		            		Node oaidc_node = oai_dcl.item(0);
		            		System.out.println("the node type"+oaidc_node.getNodeType());
		            		System.out.println("to string: "+oaidc_node.toString());
		            		Element oai_elem = (Element)oaidc_node;
		            		NodeList dcterms = oai_elem.getChildNodes();
		            		int dcterm_count = dcterms.getLength();
		            		// could possibly have parts now
		            		_hasParts = new ArrayList<DryadDataSet>();
		            		for(int i = 0; i<dcterm_count; i++){
		            			Node dcterm = dcterms.item(i);
		            			// this id is important to match records
		            			// no such record - this was on the screen

		            			// relation could be an internal link
		            			// this is the same relation back so we get stuck here :-(
		            		
		            			if(dcterm.getNodeName().equals("dc:relation") && dcterm.getTextContent().startsWith(_DRYAD_HDL_STR)){
		            				String oaiString = dcterm.getTextContent();
		            				oaiString = oaiString.replace(_DRYAD_OAI_REPLACE_STRINGS[1], _DRYAD_OAI_REPLACE_STRINGS[0]);
		            				DryadDataSet aPart = new DryadDataSet(oaiString);
			            			aPart.harvestMetadataToRDF();
			            			if(!aPart.getDatasetRDF().isEmpty()){
			            				_hasParts.add(aPart);
			            			}
		            			}
		            	
		            			metaRDFString+="<"+dcterm.getNodeName()+">"+dcterm.getTextContent()+"</"+dcterm.getNodeName()+">\n";
		            			// tried to force data in some rdfs
		            			// metaRDFString+="\t<rdfs:label>"+dcterm.getTextContent()+"</rdfs:label>\n";
		            			System.out.println("term "+dcterm.getNodeName()+" value "+dcterm.getNodeValue()+" text "+dcterm.getTextContent());
		            		}
	            		}
	          */
}
