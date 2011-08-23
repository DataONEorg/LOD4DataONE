package org.dataone.nescent;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * collects data set information for one Dryad data set
 * 
 * @class DryadDataSet 
 * @author Aida Gandara, http://trust.cs.utep.edu/members/aida/foaf.rdf
 * 
 */
public class DryadDataSet {
	public final String[] _DRYAD_OAI_REPLACE_STRINGS = {"oai:datadryad.org:10255/", "http://hdl.handle.net/", "http://hdl.handle.net/10255/","http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#"};
	public final String _DRYAD_DC_IDENTIFIER = "identifier";
	public final String _DRYAD_XLINK_REF = "xlink:href";
	public final String _DRYAD_GETRECORD = "http://www.datadryad.org/oai/request?verb=GetRecord&identifier=<r_id>&metadataPrefix=oai_dc";

	public final String _DRYAD_METS_GET_DATA = "http://datadryad.org/metadata/handle/<short_id>/mets.xml";
	public final String _DRYAD_METS_BITSTREAM_LOCATE = "mets:FLocat";
	public final String _DRYAD_METS_FILE = "mets:file";
	public final String _DRYAD_EXCEL_MIMETYPE_ATTRIBUTE = "application/vnd.ms-excel";
	
	private String _ds_shortid;
	private String _ds_metadata_rdf = null;
	private String _ds_data_rdf = null;
	
	/**
	 * PRIVATE: default constructor for this class is not available
	 * 
	 * @name DryadDataSet 
	 * 
	 */
	private DryadDataSet(){}
	
	/**
	 * constructor for DryadDataSet
	 * 
	 * @name DryadDataSet 
	 * @param short_id the short name given by Dryad and used to access objects using the OAI_PMH interface
	 * 
	 */
	public DryadDataSet( String short_id){
		_ds_shortid = short_id;
		_ds_metadata_rdf = "";
		_ds_data_rdf = "";
	}
	
	/** 
	 * get a record from Dryad using the OAI-PMH interface to retrieve Dublin-Core structured metadata for a Dryad publication create RDF from the Dryad information
	 * Notice: this is for the top level Dryad objects - describing the article
	 * Here there is an error in the logic: I should be checking the dc:type and working with the object that way
	 * 
	 * @name harvestMetadataToRDF 
	 * 
	 */
	public void harvestMetadataToRDF(){
		String harvestString = new String(_DRYAD_GETRECORD);
		// _ds_shortid is the id that will result in linkable content
		harvestString = harvestString.replace("<r_id>", _ds_shortid);
		String rdfString = "";
		String dataAboutName = "";

	    try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(harvestString);

            // normalize text representation
            doc.getDocumentElement().normalize ();
            
            // generate an about string
            NodeList headerL = doc.getElementsByTagName("header");
            // just gets the first one
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
            				// parse out the term name and add the dryad handle prefix http://datadryad.org/handle/
            				// set dataAboutName to this ...
            				// ... should the about be about something else? like the data file?
            				dataAboutName = cterm.getTextContent();
            				dataAboutName = dataAboutName.replace(_DRYAD_OAI_REPLACE_STRINGS[0], _DRYAD_OAI_REPLACE_STRINGS[3]);
            				break;
            			}
            		}
            	}
            }
            // just an extra check although this should never end up empty
            if(dataAboutName.isEmpty())
            	return;
   		
            // get the metadata
            NodeList mns = doc.getElementsByTagName("metadata");
            String metaRDFString = "";
        	// traversing to the nodes in case need to handle
        	//   nodes separately (e.g., pass to a function)	 
            if(mns.getLength()>0){
            	Node metadataNode = mns.item(0);
            	if(metadataNode.getNodeType() == Node.ELEMENT_NODE){
            		Element melement = (Element)metadataNode;
            		// get all dc terms
            		NodeList oai_dcl = melement.getElementsByTagName("oai_dc:dc");
            		// traverse each and map to an RDF string
            		// Notice, rules about where these are Data Properties or Object Properties
            		//  is defined in the DryadTypes.rdf file.  This file also defines the DCType
            		//  object that identifies the Dryad objects
            		for(int i = 0; i<oai_dcl.getLength(); i++){
	            		Node oaidc_node = oai_dcl.item(0);
	            		Element oai_elem = (Element)oaidc_node;
	            		NodeList dcterms = oai_elem.getChildNodes();
	            		int dcterm_count = dcterms.getLength();
	            		for(int j = 0; j<dcterm_count; j++){
	            			Node dcterm = dcterms.item(j);
	            			if( dcterm.getNodeName().equals("dc:type") ){
	            				String textContent = dcterm.getTextContent();
	            				if(dcterm.getTextContent().equalsIgnoreCase("Article")){
	            		            metaRDFString+="\t<rdf:type rdf:resource=\"http://purl.org/dc/dcmitype/Text\"/>\n";	            					
	            				}else if(dcterm.getTextContent().equalsIgnoreCase("Dataset")){
	            		            metaRDFString+="\t<rdf:type rdf:resource=\"http://purl.org/dc/dcmitype/Dataset\"/>\n";  
	        	            		metaRDFString+="\t<dcterms:publisher rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#Dryad\"/>";
	            				}else{
		            				metaRDFString+="\t<"+dcterm.getNodeName()+
	            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+textContent+
	            					"</"+dcterm.getNodeName()+">\n";
	            				}
	            			}else if( dcterm.getNodeName().equals("dc:date") ||
	            				dcterm.getNodeName().equals("dc:title") ||
	            				dcterm.getNodeName().equals("dc:description") ||
	            				dcterm.getNodeName().equals("dc:coverage") ||
	            				dcterm.getNodeName().equals("dc:language") ||
	            				dcterm.getNodeName().equals("dc:contributor") ||
	            				dcterm.getNodeName().equals("dc:identifier") ||
	            				dcterm.getNodeName().equals("dc:source") ||
	            				dcterm.getNodeName().equals("dc:format") ||
	            				dcterm.getNodeName().equals("dc:publisher") ){
	            				String textContent = cleanUpRDFText(dcterm.getTextContent());
	            				metaRDFString+="\t<"+dcterm.getNodeName()+
	            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+textContent+
	            					"</"+dcterm.getNodeName()+">\n";
	            				// here I would handle the others
	            			}else if(dcterm.getNodeName().equals("dc:rights")){
	            				if(dcterm.getTextContent().contains("CreativeCommons")){
		            				metaRDFString+="\t<dc:rights rdf:resource=\"http://dbpedia.org/page/Creative_Commons\"/>\n";
	            				}else{
	            					String textContent = cleanUpRDFText(dcterm.getTextContent());
		            				metaRDFString+="\t<"+dcterm.getNodeName()+
	            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+textContent+
	            					"</"+dcterm.getNodeName()+">\n";
	            				}
	            			}else if(dcterm.getNodeName().equals("dc:subject")){
	            				String subRDF = "";
	            				if((subRDF = getDBPediaRDF(dcterm.getTextContent())).isEmpty()){
	            					String textContent = cleanUpRDFText(dcterm.getTextContent());
		            				metaRDFString+="\t<"+dcterm.getNodeName()+
	            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+textContent+
	            					"</"+dcterm.getNodeName()+">\n";
	            				}else{
		            				metaRDFString+="\t<dc:subject rdf:resource=\""+subRDF+"\"/>\n";
	            				}
	            			}else if(dcterm.getNodeName().equals("dc:creator") && dcterm.getTextContent().equals("Gittleman, John L.") ){
	            				metaRDFString+="\t<dc:creator rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf#JohnLGittleman\"/>\n";
	            			}else if(dcterm.getNodeName().equals("dc:creator") && dcterm.getTextContent().equals("Price, Samantha A.") ){
	            				metaRDFString+="\t<dc:creator rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf#SamanthaAPrice\"/>\n";
	            			}else if(dcterm.getNodeName().equals("dc:relation") && dcterm.getTextContent().startsWith(_DRYAD_OAI_REPLACE_STRINGS[2]) ){
	            				// does not work because Dryad XML changed mid-project
	            				String resourceString = dcterm.getTextContent().replace(_DRYAD_OAI_REPLACE_STRINGS[2], _DRYAD_OAI_REPLACE_STRINGS[3]);
	            				metaRDFString+="\t<dc:hasPart rdf:resource=\""+resourceString+"\"/>\n";
	            				String oaiString = dcterm.getTextContent().replace(_DRYAD_OAI_REPLACE_STRINGS[2], _DRYAD_OAI_REPLACE_STRINGS[0]);
	            				harvestDataToRDF(oaiString);
	            			}else if(dcterm.getNodeName().equals("dc:relation") && dcterm.getTextContent().contains("12:")){
	            				String textContent = cleanUpRDFText(dcterm.getTextContent());
	            				metaRDFString+="\t<dc:relation rdf:resource=\""+textContent+"\"/>\n";
	            			}else if(dcterm.getNodeName().equals("dc:relation")){
	            				metaRDFString+="\t<dc:relation rdf:resource=\""+dcterm.getTextContent()+"\"/>\n";
	            			}else{
	            				String textContent = cleanUpRDFText(dcterm.getTextContent());
	            				metaRDFString+="\t<"+dcterm.getNodeName()+">"+
	            					textContent+"</"+
	            					dcterm.getNodeName()+">\n";
	            			}
	            		}
	            		metaRDFString+=addSomeRDF(dataAboutName);
	            		metaRDFString+="\t<rdf:type rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadTypes.rdf#DCType\"/>\n";
            		}	
            	}
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
  
        _ds_metadata_rdf +=rdfString;
	}
	
	/**
	 * get a record from Dryad using the OAI-PMH interface to retrieve Dublin-Core structured metadata for the data related to a Dryad publication. Create RDF from the Dryad information
	 * 
	 * @name harvestDataToRDF 
	 * @param dataOAIId the Dryad id for the data
	 * 
	 */
	public void harvestDataToRDF(String dataOAIId){
		String harvestString = new String(_DRYAD_GETRECORD);
		harvestString = harvestString.replace("<r_id>", dataOAIId);
		
		String rdfString = "";
		String dataRDFString = "";
		String dataAboutName = "";

	    try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(harvestString);

            // normalize text representation
            doc.getDocumentElement().normalize ();
            
            // need to get the about string
            NodeList headerL = doc.getElementsByTagName("header");
            boolean found = false;
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
            				dataAboutName = cterm.getTextContent();
            				dataAboutName = dataAboutName.replace(_DRYAD_OAI_REPLACE_STRINGS[0], _DRYAD_OAI_REPLACE_STRINGS[3]);
            				break;
            			}
            		}
            	}
            }
            if(dataAboutName.isEmpty())
            	return;
   		
            // Dryad stores metadata about data, in addition to the metadata about the publication
            NodeList mns = doc.getElementsByTagName("metadata");
            if(mns.getLength()>0){
            	Node metadataNode = mns.item(0);
            	if(metadataNode.getNodeType() == Node.ELEMENT_NODE){
            		Element melement = (Element)metadataNode;
            		// get all dc terms
            		NodeList oai_dcl = melement.getElementsByTagName("oai_dc:dc");
            		for(int i = 0; i<oai_dcl.getLength(); i++){
	            		Node oaidc_node = oai_dcl.item(0);
	            		Element oai_elem = (Element)oaidc_node;
	            		NodeList dcterms = oai_elem.getChildNodes();
	            		int dcterm_count = dcterms.getLength();
	            		for(int j = 0; j<dcterm_count; j++){
	            			Node dcterm = dcterms.item(j);
	            			if( dcterm.getNodeName().equals("dc:type") ){
	            				String textContent = dcterm.getTextContent();
	            				if(dcterm.getTextContent().equalsIgnoreCase("Article")){
	            		            dataRDFString+="\t<rdf:type rdf:resource=\"http://purl.org/dc/dcmitype/Text\"/>\n";	            					
	            				}else if(dcterm.getTextContent().equalsIgnoreCase("dataset")){
	            		            dataRDFString+="\t<rdf:type rdf:resource=\"http://purl.org/dc/dcmitype/Dataset\"/>\n";    
	        	            		dataRDFString+="\t<dcterms:publisher rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#Dryad\"/>";
	            				}else{
		            				dataRDFString+="\t<"+dcterm.getNodeName()+
	            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+textContent+
	            					"</"+dcterm.getNodeName()+">\n";
	            				}
	            			}else if( dcterm.getNodeName().equals("dc:date") ||
	            				dcterm.getNodeName().equals("dc:title") ||
	            				dcterm.getNodeName().equals("dc:description") ||
	            				dcterm.getNodeName().equals("dc:coverage") ||
	            				dcterm.getNodeName().equals("dc:language") ||
	            				dcterm.getNodeName().equals("dc:contributor") ||
	            				dcterm.getNodeName().equals("dc:identifier") ||
	            				dcterm.getNodeName().equals("dc:source") ||
	            				dcterm.getNodeName().equals("dc:format") ||
	            				dcterm.getNodeName().equals("dc:publisher") ){
	            				String textContent = cleanUpRDFText(dcterm.getTextContent());
	            				dataRDFString+="\t<"+dcterm.getNodeName()+
	            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+textContent+
	            					"</"+dcterm.getNodeName()+">\n";
	            				// here I would handle the others
	            			}else if(dcterm.getNodeName().equals("dc:rights")){
	            				if(dcterm.getTextContent().contains("creativecommons")){
		            				dataRDFString+="\t<dc:rights rdf:resource=\"http://dbpedia.org/page/Creative_Commons\"/>\n";
	            				}else{
	            					String textContent = cleanUpRDFText(dcterm.getTextContent());
		            				dataRDFString+="\t<"+dcterm.getNodeName()+
	            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+textContent+
	            					"</"+dcterm.getNodeName()+">\n";
	            				}
	            			}else if(dcterm.getNodeName().equals("dc:subject")){
	            				String subRDF = "";
	            				if((subRDF = getDBPediaRDF(dcterm.getTextContent())).isEmpty()){
	            					String textContent = cleanUpRDFText(dcterm.getTextContent());
		            				dataRDFString+="\t<"+dcterm.getNodeName()+
	            					" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+textContent+
	            					"</"+dcterm.getNodeName()+">\n";
	            				}else{
		            				dataRDFString+="\t<dc:subject rdf:resource=\""+subRDF+"\"/>\n";
	            				}
	            			}else if(dcterm.getNodeName().equals("dc:creator") && dcterm.getTextContent().equals("Gittleman, John L.") ){
	            				dataRDFString+="\t<dc:creator rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#JohnLGittleman\"/>\n";
	            			}else if(dcterm.getNodeName().equals("dc:creator") && dcterm.getTextContent().equals("Price, Samantha A.") ){
	            				dataRDFString+="\t<dc:creator rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#SamanthaAPrice\"/>\n";
	            			}else if(dcterm.getNodeName().equals("dc:relation") && dcterm.getTextContent().startsWith(_DRYAD_OAI_REPLACE_STRINGS[2]) ){
	            				// this should be the publication - if not then this line could result in an error in RDF links
	            				String resourceString = dcterm.getTextContent().replace(_DRYAD_OAI_REPLACE_STRINGS[2], _DRYAD_OAI_REPLACE_STRINGS[3]);
	            				dataRDFString+="\t<dc:isPartOf rdf:resource=\""+resourceString+"\"/>\n";
	            				/*** a hack, the following string is breaking the RDF ***/
	            			}else if(dcterm.getNodeName().equals("dc:relation") && dcterm.getTextContent().contains("12:")){
	            				String textContent = cleanUpRDFText(dcterm.getTextContent());
	            				dataRDFString+="\t<dc:relation rdf:resource=\""+textContent+"\"/>\n";
	            			}else if(dcterm.getNodeName().equals("dc:relation")){
	            				dataRDFString+="\t<dc:relation rdf:resource=\""+dcterm.getTextContent()+"\"/>\n";
	            			}else{
	            				String textContent = cleanUpRDFText(dcterm.getTextContent());
	            				dataRDFString+="\t<"+dcterm.getNodeName()+">"+
	            					textContent+"</"+
	            					dcterm.getNodeName()+">\n";
	            			}
	            		}
	            		dataRDFString+=addSomeRDF(dataAboutName);
	            		dataRDFString+="\t<rdf:type rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadTypes.rdf#DCType\"/>\n";
            		}
            	}
            }
            
			// A Dryad Data server would grab this data from the METS data to build searchable content
            // The type information is defined in DryadTypes.rdf
            // This is the level that was obtained from DAAC
            //  hardcoded
            String dString = "";
			if(dataAboutName.contains("dryad.83")){
				dataRDFString+="\t<dryadt:datafile "+
				"rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#PriceGittleman_2007_append.xls\"/>\n";
				// build the data RDF for the xls file
				dString="<rdf:Description rdf:about=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#PriceGittleman_2007_append.xls\">\n";
				dString+="\t<dryadt:filename "+
				" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">PriceGittleman_2007_appendix.xls"+
				"</dryadt:filename>\n";
				dString+="\t<dryadt:filesize "+
				" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">94.72Kb"+
				"</dryadt:filesize>\n";
				dString+="\t<dc:format "+
				" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Microsoft Excel"+
				"</dc:format>\n";
				dString+="\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Addax_nasomaculatus\" />";
				dString+="\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Axis_axis\" />";
				dString+="\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Capra_hircus\" />";
				dString+="\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Kobus_leche\" />";
				dString+="\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Ovis aries\" />";
				dString+="\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Pacari_tajacu\" />";
				dString+="\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Tayassu_pecari\" />";
				dString+="\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Vicugna_vicugna\" />";
				dString+="\t<dc:isPartOf rdf:resource=\""+dataAboutName+"\"/>\n";
				dString+="\t<rdf:type rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadTypes.rdf#DatafileType\"/>\n";
				dString+="</rdf:Description>\n";
			}
			
            // build the rdf for this resource
            rdfString+="<rdf:Description rdf:about=\""+dataAboutName+"\">\n"+
                dataRDFString+
		        "</rdf:Description>\n" + dString;
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
        
        _ds_data_rdf += rdfString;

	}
	
	/** 
	 * returns an RDF string with all content collected for this data set.
	 * 
	 * @name getRDFString
	 * @return a RDF string with metadata for the publication and any related data
	 * 
	 */
	public String getRDFString(){
		String rdfString = _ds_metadata_rdf + _ds_data_rdf;

		return rdfString;
	}
	
	/**
	 * performs some very static mapping of a string to a DBPedia RDF string. This required a lot of manual lookups, from the datasets chosen to entries in dbpedia.  If no mapping is found, an empty string is returned
	 * 
	 * @name getDBPediaRDF 
	 * @param inputString a string to search for in dbpedia
	 * @return a URI to the entry in DBPedia.  If the string was not found in DBPedia then an empty string is found
	 */
	public String getDBPediaRDF(String inputString){
		String rdfString = "";
		
		// should search dbpedia - if found - then use resource link, otherwise use literal
		
		// from dryad.82
		if(inputString.equals("phylogenetic")){

		}else if(inputString.equals("comparative methods")){
			
		}else if(inputString.equals("bushmeat")){
			rdfString="http://dbpedia.org/resource/Bushmeat";
		}else if(inputString.equals("hunting")){
			rdfString="http://dbpedia.org/resource/Hunting";	
		}else if(inputString.equals("extinction")){
			rdfString="http://dbpedia.org/resource/Extinction";
		}else if(inputString.equals("economic development")){
			rdfString="http://dbpedia.org/resource/Economic_development";
			
		// from dryad.234
		}else if(inputString.equals("plant economics")){

		}else if(inputString.equals("trade-offs")){
			
		}else if(inputString.equals("wood")){
			rdfString="http://dbpedia.org/resource/Wood";
		}else if(inputString.equals("functional ecology")){
			rdfString="http://dbpedia.org/resource/Functional_ecology";
		}else if(inputString.equals("evolution")){
			rdfString="http://dbpedia.org/resource/Evolution";
		// from dryad.1252
		}else if(inputString.equals("Evolutionary Theory")){
			rdfString="http://dbpedia.org/resource/Evolution";
		}else if(inputString.equals("Phylogeography")){
			rdfString="http://dbpedia.org/resource/Phylogeography";
		}else if(inputString.equals("Reptiles")){
			rdfString="http://dbpedia.org/resource/Reptile";
		}else if(inputString.equals("Speciation")){
			rdfString="http://dbpedia.org/resource/Speciation";

		// from dryad.2016
		}else if(inputString.equals("parental investment")){
			rdfString="http://dbpedia.org/resource/Parental_investment";
		}else if(inputString.equals("immunity")){
			rdfString="http://dbpedia.org/resource/Immunity";
		}else if(inputString.equals("female signalling")){

		}else if(inputString.equals("differential allocation")){

		}else if(inputString.equals("egg colour")){

		}else if(inputString.equals("cross-festering")){

		}else if(inputString.equals("nest defence")){

		}else if(inputString.equals("feeding frequency")){
			
		}else if(inputString.equals("laying order")){
			
		}
		
		return rdfString;
	}
	
	
    /**
     * a hack because there are some things that were not obtained from the Dryad data
	 *  this is the RDF that was injected to link and relate more data
	 * 
	 * @name addSomeRDF 
	 * @param inputString a uri for the RDF to be injected
	 * @return a string that can be added to the RDF dataset
	 */
	private String addSomeRDF(String uri){
		String rdfString = "";
		if(uri.endsWith("2016")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Ficedula_albicollis\"/>\n";
			// doi:10.5061/dryad.2016
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.2016</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.2016</datacite:hasAlternateIdentifier>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.2017\"/>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.2018\"/>\n";
		} else if(uri.endsWith("2017")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Ficedula_albicollis\"/>\n";
			// doi:10.5061/dryad.2016/1
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.2016/1</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.2017</datacite:hasAlternateIdentifier>\n";
		} else if(uri.endsWith("2018")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Ficedula_albicollis\"/>\n";
			// doi:10.5061/dryad.2016/2
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.2016/2</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.2018</datacite:hasAlternateIdentifier>\n";
		} else if(uri.endsWith("1253")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Lampropeltis_getula\"/>\n";
			// doi:10.5061/dryad.1252/1
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.1252/1</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.1253</datacite:hasAlternateIdentifier>\n";
		} else if(uri.endsWith("1254")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Lampropeltis_getula\"/>\n";
			// doi:10.5061/dryad.1252/2
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.1252/2</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.1254</datacite:hasAlternateIdentifier>\n";
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:resource=\"http://datadryad.org/handle/10255/dryad.1254\"/>\n";
		} else if(uri.endsWith("1252")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Lampropeltis_getula\"/>\n";
			// doi:10.5061/dryad.1252
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.1252</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.1252</datacite:hasAlternateIdentifier>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.1253\"/>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.1254\"/>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">42.2833</geo:lat>\n";
		} else if(uri.endsWith("82")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Artiodactyla\"/>\n";
			// treebase : http://purl.org/phylo/treebase/phylows/study/TB2:S1271?format=html
			rdfString+="\t<rdfs:seeAlso rdf:resource=\"http://purl.org/phylo/treebase/phylows/study/TB2:S1271?format=rdf\"/>\n";
			// doi:10.5061/dryad.82
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.82</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.82</datacite:hasAlternateIdentifier>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.83\"/>\n";
		} else if(uri.endsWith("83")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Artiodactyla\"/>\n";
			// doi:10.5061/dryad.82/1
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.82/1</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.83</datacite:hasAlternateIdentifier>\n";
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:resource=\"http://datadryad.org/handle/10255/dryad.83\"/>\n";
		} else if(uri.endsWith("8437")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Rhinoptera_steindachneri\"/>\n";
			// doi:10.5061/dryad.8437
			// genbank 
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.8437</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.8437</datacite:hasAlternateIdentifier>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.8438\"/>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.8439\"/>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.8440\"/>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.8441\"/>\n";
		} else if(uri.endsWith("8438")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Rhinoptera_steindachneri\"/>\n";
			// doi:10.5061/dryad.8437/1
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.8437/1</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.8438</datacite:hasAlternateIdentifier>\n";
			// genbank HQ540559
			rdfString += "\t<rdfs:seeAlso rdf:resource=\"http://bio2rdf.org/rdf/genbank/HQ540559\"/>\n";
		} else if(uri.endsWith("8439")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Rhinoptera_steindachneri\"/>\n";
			// genbank HQ540560
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.8437/2</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.8439</datacite:hasAlternateIdentifier>\n";
			rdfString += "\t<rdfs:seeAlso rdf:resource=\"http://bio2rdf.org/rdf/genbank/HQ540559\"/>\n";
		} else if(uri.endsWith("8440")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Rhinoptera_steindachneri\"/>\n";
			// genbank HQ540561
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.8437/3</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.8440</datacite:hasAlternateIdentifier>\n";
			rdfString += "\t<rdfs:seeAlso rdf:resource=\"http://bio2rdf.org/rdf/genbank/HQ540559\"/>\n";
		} else if(uri.endsWith("8441")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Rhinoptera_steindachneri\"/>\n";
			// genbank HQ540562
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.8437/4</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.8441</datacite:hasAlternateIdentifier>\n";
			rdfString += "\t<rdfs:seeAlso rdf:resource=\"http://bio2rdf.org/rdf/genbank/HQ540559\"/>\n";
		} else if(uri.endsWith("234")){
			// doi:10.5061/dryad.234
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.234</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.234</datacite:hasAlternateIdentifier>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.235\"/>\n";
		} else if(uri.endsWith("235")){
			// doi:10.5061/dryad.234/1
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.234/1</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.235</datacite:hasAlternateIdentifier>\n";
		}else if(uri.endsWith("1295")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Lythrum_salicaria\"/>\n";
			// doi:10.5061/dryad.1295
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.1295/1</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.1295</datacite:hasAlternateIdentifier>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.1296\"/>\n";
			rdfString+="\t<dc:hasPart rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf#dryad.1300\"/>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">-93.9333</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">42.6333</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">-95.2333</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">42.2667</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">-93.1167</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">42.35</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">-85.35</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">52.4167</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">12.95</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">52.45</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">12.95</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">48.5333</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">9.0167</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">48.55</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">8.96167</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">48.5167</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">8.9667</geo:long>\n";
		}else if(uri.endsWith("1296")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Lythrum_salicaria\"/>\n";
			// doi:10.5061/dryad.1295/1
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.1295/1</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.1296</datacite:hasAlternateIdentifier>\n";;
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">42.2833</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">-93.9333</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">42.6222</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">-95.2333</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">43.2667</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">-93.1167</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">52.4167</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">12.95</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">48.5333</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">9.0167</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">47.6</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">8.2167</geo:long>\n";
			rdfString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">48.5167</geo:lat>\n";
			rdfString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">8.9667</geo:long>\n";
		}else if(uri.endsWith("1300")){
			rdfString += "\t<dbpedia:species rdf:resource=\"http://dbpedia.org/resource/Lythrum_salicaria\"/>\n";
			// doi:10.5061/dryad.1295/2
			rdfString += "\t<datacite:hasPrimaryIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10.5061/dryad.1295/2</datacite:hasPrimaryIdentifier>\n";
			rdfString += "\t<datacite:hasAlternateIdentifier rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">10255/dryad.1300</datacite:hasAlternateIdentifier>\n";
		}
		
		return rdfString;
	}
	
	/*
	 * PRIVATE FUNCTION - not documented
	 * 
	 */
	private String cleanUpRDFText(String inputString){
		String textString = "";
		
		textString = inputString.replace('@',' '); 
		textString = textString.replace('<',' ');
		textString = textString.replace('>',' ');
		textString = textString.replace('>',' ');
		textString = textString.replace(':',' ');	
		return textString;
	}
}
