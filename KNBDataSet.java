package org.dataone.nescent;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.ucsb.nceas.metacat.client.DocumentNotFoundException;
import edu.ucsb.nceas.metacat.client.InsufficientKarmaException;
import edu.ucsb.nceas.metacat.client.Metacat;
import edu.ucsb.nceas.metacat.client.MetacatException;
import edu.ucsb.nceas.metacat.client.MetacatInaccessibleException;

/**
 * collects data set information for one KNB data set
 * 
 * @class KNBDataSet 
 * 
 */
public class KNBDataSet {
	
	private String _ds_id = null;
	private String _metadata_rdf = null;
	private String _data_rdf = null;
	private Metacat _metacatConnection = null;
	
	private static final String _KNB_DATA_RDF_URL = "http://rio.cs.utep.edu/ciserver/ciprojects/sdata/KNBData.rdf";
	
	private DocumentBuilderFactory _docBuilderFactory = null;
	private DocumentBuilder _docBuilder = null;
	private Document _doc = null;
	
	/**
	 * PRIVATE: default constructor for this class is not available
	 * 
	 * @name KNBDataSet 
	 * 
	 */
	private KNBDataSet(){}
	
	/**
	 * 
	 * constructor for KNBDataSet
	 * 
	 * @name KNBDataSet 
	 * @param dataset_id the name given by KNB and used to access objects using the Metacat interface
	 * 
	 */
	public KNBDataSet( Metacat mc, String dataset_id){
		_metacatConnection = mc;
		_ds_id = dataset_id;
		_metadata_rdf = "";
		_data_rdf = "";
	}
	
	/** 
	 * 
	 * get a record from KNB using the Metacat interface to retrieve EML structured metadata for KNB data create RDF from the KNB information
	 * 
	 * @name harvestMetadataToRDF 
	 * 
	 */
	public void harvestMetadataToRDF(){
		InputStream data_file = null;   // this will maintain the file connection with Metacat		
		String rdfString = "";          // this will hold the rdf for all parts of the object including data and header
		
		try{
			
			// make a local copy - for me
            data_file = _metacatConnection.read(_ds_id);
  	        Writer out = new OutputStreamWriter(new FileOutputStream("C:/data/knb/metacat_"+_ds_id));
  	        int ch;
  	        while((ch=data_file.read())!=-1){
  	        	out.write(ch);
  	        }
  	        out.close();
  	        data_file.close();
  	        
			// hold the string here
            String metaRDFString = "";      // just the metadata rdf - not the data or header
			String dataAboutName = "";
            data_file = _metacatConnection.read(_ds_id);
		
            // Use the DocumentBuilderFactory to read the xml
			_docBuilderFactory = DocumentBuilderFactory.newInstance();
            _docBuilder = _docBuilderFactory.newDocumentBuilder();
            _doc = _docBuilder.parse(data_file);

            // normalize text representation
            _doc.getDocumentElement().normalize();
            
            // this is how we will identify the data set in the RDF (about = )
            dataAboutName=_KNB_DATA_RDF_URL+"#"+_ds_id;
            
            // types that we care about - RDF browsers look for this in some way
            metaRDFString+="\t<rdf:type rdf:resource=\"http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing\"/>\n";
            metaRDFString+="\t<rdf:type rdf:resource=\"http://purl.org/dc/dcmitype/Dataset\"/>\n";
            metaRDFString+="\t<rdf:type rdf:resource=\"http://www.w3.org/2006/time/TemporalEntity\"/>\n";
            
            // handle title
            NodeList mns = _doc.getElementsByTagName("title");
            if(mns.getLength()>0){
            	Node metadataNode = mns.item(0);
            	if(metadataNode.getNodeType() == Node.ELEMENT_NODE){
    				metaRDFString+="\t<dc:title rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+metadataNode.getTextContent()+
					"</dc:title>\n";
            	}
            }
            
            //handle the creator 
            mns = _doc.getElementsByTagName("creator");
            // assume a foaf exists for this person - created in samplefoaf.rdf
            for(int mns_i = 0; mns_i < mns.getLength(); mns_i++){	
            	Node metadataNode = mns.item(mns_i);
            	if(metadataNode.getNodeType() == Node.ELEMENT_NODE){
            		Element melement = (Element)metadataNode;
            		NodeList indivList = melement.getElementsByTagName("individualName");
            		if(indivList.getLength()>0){
            			Node indivNode = indivList.item(0);
            			if(indivNode.getNodeType() == Node.ELEMENT_NODE){
            				Element nelement = (Element)indivNode;
            				String gName = nelement.getElementsByTagName("givenName").item(0).getTextContent();
            				String sName = nelement.getElementsByTagName("surName").item(0).getTextContent();
            				metaRDFString+="\t<dc:creator rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#"+gName+sName+"\"/>\n";
            			}
            		}
            	}
            }
            
            //handle the creator 
            mns = _doc.getElementsByTagName("abstract");
            if(mns.getLength()>0){	
            	Node metadataNode = mns.item(0);
            	if(metadataNode.getNodeType() == Node.ELEMENT_NODE){;
    				metaRDFString+="\t<dc:abstract rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+metadataNode.getTextContent()+"</dc:abstract>\n";
            	}
            }
            
            //handle the creator 
            mns = _doc.getElementsByTagName("keywordSet");
            // assume a foaf exists for this person - created in samplefoaf.rdf
            if(mns.getLength()>0){	
            	Node metadataNode = mns.item(0);
            	if(metadataNode.getNodeType() == Node.ELEMENT_NODE){
            		Element melement = (Element)metadataNode;
            		NodeList keyList = melement.getElementsByTagName("keyword");
            		for(int key_i = 0; key_i < keyList.getLength(); key_i++){
            			Node keyNode = keyList.item(key_i);
            			if(keyNode.getNodeType() == Node.ELEMENT_NODE){
            				String freebaseRDF=getDBPediaRDF(keyNode.getTextContent());
            				if(freebaseRDF.isEmpty())
            					metaRDFString+="\t<dc:subject rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+keyNode.getTextContent()+"</dc:subject>\n";
            				else
            					metaRDFString+="\t<dc:subject rdf:resource=\""+freebaseRDF+"\"/>\n";
            			}
            		}
            	}
            }
            
            //handle the coverage (geographic and temporal)
            mns = _doc.getElementsByTagName("coverage");
            // geographic and temporal
            if(mns.getLength()>0){	
            	Node metadataNode = mns.item(0);
            	if(metadataNode.getNodeType() == Node.ELEMENT_NODE){
            		Element melement = (Element)metadataNode;
            		NodeList gcList = melement.getElementsByTagName("geographicCoverage");
            		if(gcList.getLength()>0){
            			Element gcelement = (Element)gcList.item(0);  // the first one (and only)
	            		NodeList gdList = gcelement.getElementsByTagName("geographicDescription");
	            		if(gdList.getLength()>0){
	            			String gdesc = gdList.item(0).getTextContent();
	            			// ?? geoname or freebase?
	            		}
	            		NodeList bcList = gcelement.getElementsByTagName("boundingCoordinates");
	            		if(bcList.getLength()>0){
	            			Element bcelement = (Element)bcList.item(0);
	            			String ebc="";
	            			String nbc="";
	            			String wbc="";
	            			String sbc="";
	            			NodeList coordList = bcelement.getElementsByTagName("eastBoundingCoordinate");
	            			if(coordList.getLength()>0){
	            				ebc = coordList.item(0).getTextContent();
	            			}
	            			coordList = bcelement.getElementsByTagName("westBoundingCoordinate");
	            			if(coordList.getLength()>0){
	            				wbc = coordList.item(0).getTextContent();
	            			}
	            			coordList = bcelement.getElementsByTagName("northBoundingCoordinate");
	            			if(coordList.getLength()>0){
	            				nbc = coordList.item(0).getTextContent();
	            			}
	            			coordList = bcelement.getElementsByTagName("southBoundingCoordinate");
	            			if(coordList.getLength()>0){
	            				sbc = coordList.item(0).getTextContent();
	            			}
	            			// although get all four points, not sure what vocabulary to use with the 
	            			// browsers.  For now, just consider south and west
	            		//	metaRDFString+="\t<geo:lat>"+nbc+"</geo:lat>\n";
	            			metaRDFString+="\t<geo:lat rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">"+sbc+"</geo:lat>\n";
	            		//	metaRDFString+="\t<geo:long>"+ebc+"</geo:long>\n";
	            			metaRDFString+="\t<geo:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">"+wbc+"</geo:long>\n";
	            		}
            		}
            		NodeList tcList = melement.getElementsByTagName("temporalCoverage");
            		if(tcList.getLength()>0){
            			Element tcelement = (Element)tcList.item(0); // the first one (and only)
	            		NodeList rodList = tcelement.getElementsByTagName("rangeOfDates");
	            		if(rodList.getLength()>0){
	            			Element rodelement = (Element)rodList.item(0);
	            			String beginDate="";
	            			String endDate="";
	            			NodeList bdList = rodelement.getElementsByTagName("beginDate");
	            			if(bdList.getLength()>0){
	            				Element bdelement = (Element)bdList.item(0);
	            				NodeList cdList = bdelement.getElementsByTagName("calendarDate");
	            				beginDate = cdList.item(0).getTextContent();
	            			}
	            			NodeList edList = rodelement.getElementsByTagName("endDate");
	            			if(edList.getLength()>0){
	            				Element edelement = (Element)edList.item(0);
	            				NodeList cdList = edelement.getElementsByTagName("calendarDate");
	            				endDate = cdList.item(0).getTextContent();
	            			}
	            			// stuff to consider with data, like begin and end - interval
	            			// not sure what browsers will use
	            			metaRDFString+="\t<dc:date rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+beginDate+"</dc:date>\n";
	            			if(!beginDate.equals(endDate)){
	            				/* Using a range of dates would be useful but I could not get the RDF browsers to see this
	            				 * so for now just focused on dc:date */
	            				metaRDFString+="\t<dc:date rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"+endDate+"</dc:date>\n";
	            			}
	            		}	  
            		}
            	}
            }
            
            //handle the creator 
            mns = _doc.getElementsByTagName("contact");
            // assume a foaf exists for this person - created in samplefoaf.rdf
            for(int mns_i = 0; mns_i < mns.getLength(); mns_i++){	
            	Node metadataNode = mns.item(mns_i);
            	if(metadataNode.getNodeType() == Node.ELEMENT_NODE){
            		Element melement = (Element)metadataNode;
            		NodeList indivList = melement.getElementsByTagName("individualName");
            		if(indivList.getLength()>0){
            			Node indivNode = indivList.item(0);
            			if(indivNode.getNodeType() == Node.ELEMENT_NODE){
            				Element nelement = (Element)indivNode;
            				String gName = nelement.getElementsByTagName("givenName").item(0).getTextContent();
            				String sName = nelement.getElementsByTagName("surName").item(0).getTextContent();
            				metaRDFString+="\t<dc:creator rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#"+gName+sName+"\"/>\n";
            			}
            		}
            	}
            }
            
            //handle data - this is done for each data object
            mns = _doc.getElementsByTagName("dataTable");
            String rdfURL = "";
            // get all data tables
            for(int mns_i = 0; mns_i < mns.getLength(); mns_i++){	
            	Node metadataNode = mns.item(mns_i);
            	rdfURL = harvestDataToRDF(metadataNode, dataAboutName);
            	metaRDFString+="\t<dc:hasPart rdf:resource=\""+rdfURL+"\"/>\n";
            }
            
            // build the rdf for this resource
            _metadata_rdf+="<rdf:Description rdf:about=\""+dataAboutName+"\">\n";
            _metadata_rdf+=metaRDFString;
            _metadata_rdf += "</rdf:Description>\n";
			data_file.close();
		}
		catch(ParserConfigurationException pc_ex){
			System.out.println("Parser Configuration Exception "+pc_ex.getMessage());
		}
		catch(SAXException s_ex){
			System.out.println("SAX Exception "+s_ex.getMessage());
		}
		catch(InsufficientKarmaException ike_ex){
			System.out.println("Insufficient Karma: "+ ike_ex.getMessage());
		}
		catch(MetacatInaccessibleException mie_ex){
			System.out.println("Metacat is inaccessible: "+ mie_ex.getMessage());
		}
		catch(DocumentNotFoundException dnfe_ex){
			System.out.println("Document not found: "+ dnfe_ex.getMessage());
		}
		catch(MetacatException m_ex){
			System.out.println("Metacat exception: "+ m_ex.getMessage());
		}
		catch(FileNotFoundException fnf_ex){
			System.out.println("File not found: " + fnf_ex.getMessage());
			
		}
		catch(IOException io_ex){
			System.out.println("IO Exception: " + io_ex.getMessage());
		}
	}
	
	/**
	 * 
	 * get a record from KNB using the Metacat interface to data content for KNB data.  This is for data that makes up a dataset create RDF from the KNB information
	 * 
	 * @name harvestDataToRDF 
	 * @param dataNode the XML node where this data starts.
	 * @param partOfURI the URI to use in the hasPart relation
	 * 
	 */
	public String harvestDataToRDF(Node dataNode, String partOfURI){
		
		String rdfURL = _KNB_DATA_RDF_URL+"#";
		String dataRDFString = "";
		boolean textFile = false;
		String dataAboutName = "";
		
    	if(dataNode.getNodeType() == Node.ELEMENT_NODE){
    		dataRDFString+="\t<dc:isPartOf rdf:resource=\""+partOfURI+"\"/>\n";
    		// DCMI does not let me say that this is describing a file
    		dataRDFString+="\t<rdf:type rdf:resource=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/KNBTypes.rdf#datafile\"/>\n";
    		Element delement = (Element)dataNode;
    		
    		// get the data name in KNB
    		NodeList eList = delement.getElementsByTagName("entityName");
    		if(eList.getLength()>0){
    			String ename = eList.item(0).getTextContent();
    			dataRDFString+="\t<dc:title rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"
    							+ename+
	            				"</dc:title>\n";
    		}
    		
    		// get the description
    		NodeList descList = delement.getElementsByTagName("entityDescription");
    		if(descList.getLength()>0){
    			String desc = descList.item(0).getTextContent();
    			dataRDFString+="\t<dc:description rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"
    				+desc+
    				"</dc:description>\n";
    		}
    		
    		// get physical information - data format, file name
    		NodeList pList = delement.getElementsByTagName("physical");
    		if(pList.getLength()>0){
    			Element pelement = (Element)pList.item(0);
        		NodeList formatList = delement.getElementsByTagName("dataFormat");
        		if(formatList.getLength()>0){
        			Element felement = (Element)formatList.item(0);
        			NodeList ftypeList = felement.getElementsByTagName("textFormat");
        			if(ftypeList.getLength()>0){
        				textFile = true;
        				// I wanted to use something better to describe this but text was really the only reasonable one
                		dataRDFString+="\t<rdf:type rdf:resource=\"http://purl.org/dc/dcmitype/Text\"/>\n";
        			}

        			NodeList itypeList = felement.getElementsByTagName("externallyDefinedFormat");
        			if(itypeList.getLength()>0){
        				Element ielement = (Element)itypeList.item(0);
        				NodeList ftList = ielement.getElementsByTagName("formatName");
        				String format = ftList.item(0).getTextContent();
	        			// I could identify images
        				if(format.equals("jpeg") || format.equals("jpg") || format.equals("png"))
        					dataRDFString+="\t<rdf:type rdf:resource=\"http://purl.org/dc/dcmitype/Image\"/>\n";
        			}
        		}
        		// get KNB's URL for this object
        		NodeList urlList = delement.getElementsByTagName("url");
        		if(urlList.getLength()>0){
        			String longId = urlList.item(0).getTextContent();
        			dataRDFString+="\t<rdfs:seeAlso rdf:resource=\""+longId+"\"/>\n";
        			String[] strs = longId.split("/");
        			if(strs.length>0)
        				dataAboutName = strs[strs.length-1];
        			else
        				dataAboutName = "UNKNOWN_ERROR";
        		}
        		rdfURL+=dataAboutName;
        		
        		// get the files name - somewhere on disk
        		NodeList objList = delement.getElementsByTagName("objectName");
        		if(objList.getLength()>0){
        			String filename = objList.item(0).getTextContent();
        			dataRDFString+="\t<knbdt:filename rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"
        				+filename+"</knbdt:filename>\n";
        		}
        		
        		// get the file size - only for Text files
        		NodeList szList = delement.getElementsByTagName("size");
        		if(szList.getLength()>0){
        			String size = szList.item(0).getTextContent();
        			dataRDFString+="\t<knbdt:filesize rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"
        				+size+"</knbdt:filesize>\n";
        		}
    		}
    		
    		
    		try{
        		// make a local copy - for me
	            InputStream data_file = _metacatConnection.read(dataAboutName);
	  	        Writer out = new OutputStreamWriter(new FileOutputStream("C:/data/knb/metacat_"+dataAboutName));
	  	        int ch;
	  	        while((ch=data_file.read())!=-1){
	  	        	out.write(ch);
	  	        }
	  	        out.close();
	  	        data_file.close();
        		
        		// now try to get a bit more specific
        		if(dataAboutName.equals("connolly.105.1")){
        			
        		}else if(dataAboutName.equals("connolly.106.1")){
        			// this one has accession numbers
        			
        		}else if(dataAboutName.equals("connolly.104.1")){
        			
        		}else if(dataAboutName.equals("connolly.102.1")){
        			// this one has species
        			
        		}
    		}
    		catch(MetacatInaccessibleException mi_ex){
    			System.out.println("Metacat Inaccessible Exception: "+mi_ex.getMessage());
    		}
    		catch(InsufficientKarmaException ic_ex){
    			System.out.println("Insufficient Karma Exception: "+ic_ex.getMessage());
    		}
    		catch(DocumentNotFoundException d_ex){
    			System.out.println("Document Not Found Exception: "+d_ex.getMessage());
    		}
    		catch(MetacatException m_ex){
    			System.out.println("Metacat Exception: "+m_ex.getMessage());
    		}
    		catch(FileNotFoundException fnf_ex){
    			System.out.println("File Not Found Exception: "+fnf_ex.getMessage());
    		}
    		catch(IOException io_ex){
    			System.out.println("IO Exception: "+io_ex.getMessage());
    		}

            _data_rdf+="<rdf:Description rdf:about=\""+rdfURL+"\">\n";
            _data_rdf+=dataRDFString;
		 	_data_rdf+= "</rdf:Description>\n";
    	}
    	return rdfURL;
	}
	
	/** 
	 * 
	 * returns an RDF string with all content collected for this data set
	 * 
	 * @name getRDFString 
	 * @return a RDF string with metadata for the publication and any related data
	 * 
	 */
	public String getRDFString(){
		String rdfString = _metadata_rdf + _data_rdf;
		return rdfString;
	}
	
	/**
	 * 
	 * performs some very static mapping of a string to a DBPedia RDF string. This required a lot of manual lookups, from the datasets chosen to entries in dbpedia.  If no mapping is found, an empty string is returned
	 * 
	 * @name getDBPediaRDF
	 * @param inputString a string to search for in dbpedia
	 * @return a URI to the entry in DBPedia.  If the string was not found in DBPedia then an empty string is found
	 */
	public String getDBPediaRDF(String inputString){
		String rdfString = "";
		
		if(inputString.equals("forest herbs")){

		}else if(inputString.equals("genetic diversity")){
			rdfString="http://dbpedia.org/page/Genetic_diversity";
		}else if(inputString.equals("land-use history")){
			rdfString="http://dbpedia.org/page/Land_use";
		}else if(inputString.equals("species diversity")){
			rdfString="http://dbpedia.org/page/Species_diversity";
		}else if(inputString.equals("Tompkins County, New York (USA)")){
			rdfString="http://dbpedia.org/page/Tompkins_County,_New_York";
		}else if(inputString.equals("forest stands")){

		}else if(inputString.equals("Trillium grandiflorum")){
			rdfString="http://dbpedia.org/page/Trillium_grandiflorum";
		}else if(inputString.equals("Wisconsin")){
			rdfString="http://dbpedia.org/page/Wisconsin";
		}else if(inputString.equals("termocline depth")){

		}else if(inputString.equals("secchi depht")){

		}else if(inputString.equals("chemical data")){

		}else if(inputString.equals("lakes")){

		}
		
		return rdfString;
	}
	
	/* not currently using
    private long getByteCount(InputStream is) throws IOException {
    	
    	int ch;
        long length = 0;
        byte[] bytes = null;

        // wasted but I don't know another way to do this with a stream
        // and that is what I get back from Metacat
        while((ch = is.read()) != -1){
        	length++;
        }

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length == 0 || length > Integer.MAX_VALUE) {
            // File is too large
        	return -1;
        }

        return length;
    }
    */
    
    /* not currently using
    private boolean isMetadataFile(byte[] byteString){
    	boolean isIt = true;
    	
    	int length = _XML_VERSION_STRING.length();
    	
    	for(int i = 0; i<length && isIt; i++){
    		if(_XML_VERSION_STRING.charAt(i) != (char)byteString[i])
    			isIt=false;
    	}
    	return isIt;
    }
    */
}
