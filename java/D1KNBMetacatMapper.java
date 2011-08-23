package org.dataone.nescent;

/**
 * KNB specific class for creating RDF from the KNB Data Repository
 * Contains D1KNBMetacatMapper and KNBDataSet class definitions
 * In a more general sense this class simulates what a server providing RDF data might do
 *    -- map internal KNB data, from a database, to RDF
 *    -- return RDF data for KNB datasets
 * There is additional functionality to make this demo work, like specifically selecting datasets,
 *    linking internal data based on the KNB naming and linking data to external sources like
 *    dbpedia.  This would be a part of the components that make up KNB publishing.
 * 
 * @name org.dataone.nescent.KNBMetacatMapper.java
 * @author Aida Gandara, http://trust.cs.utep.edu/members/aida/foaf.rdf
 * 
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

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
import edu.ucsb.nceas.metacat.client.MetacatFactory;
import edu.ucsb.nceas.metacat.client.MetacatInaccessibleException;

/**
 * 
 * generates RDF for KNB (http://knb.ecoinformatics.org)
 * 
 * @class D1KNBMetacatMapper 
 * @author Aida Gandara, http://trust.cs.utep.edu/members/aida/foaf.rdf
 * 
 */
public class D1KNBMetacatMapper{

	
	private Metacat _metacatConnection = null;
	
	private static final String _metacatURL = "http://knb.ecoinformatics.org/knb/metacat";
	
	private static final String _KNB_DS1 = "connolly.116.10";
	private static final String _KNB_DS2 = "connolly.277.3";
	private static final String _KNB_DS3 = "connolly.272.3";
	
	private static final String _XML_VERSION_STRING = "<?xml version=\"1.0\"?>\n";
	
	public final String _RDF_HEADER = "<rdf:RDF \n"+
    "\txmlns:knbdt=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/KNBTypes.rdf#\" \n"+
    "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"+
    "\txmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"+
    "\txmlns:dcterms=\"http://purl.org/dc/terms/\"\n"+
    "\txmlns:dcmitype=\"http://purl.org/dc/dcmitype/\"\n"+
    "\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"+
    "\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"+
    "\txmlns:time=\"http://www.w3.org/2006/time#\"\n"+
    "\txmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\"\n"+
    "\txmlns:geonames=\"http://www.geonames.org/ontology#\"\n"+
    "\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" > \n";
	

	/* this list is filled in the setup stage and all related RDF
	 * is included in the get stage
	 */
	private ArrayList<KNBDataSet> _datasets;
	
	/**
	 * the default class constructor for D1KNBMetacatMapper, sets up
	 *  Metacat connection
	 * 
	 * @name D1KNBMetacatMapper
	 * 
	 */
	D1KNBMetacatMapper(){
		_datasets = new ArrayList<KNBDataSet>();
		// create the connection
		try {
			_metacatConnection = MetacatFactory.createMetacatConnection(_metacatURL);
		} catch (MetacatInaccessibleException mie) {
			System.out.println("Metacat Inaccessible:\n" + mie.getMessage());
		} catch (Exception e) {
			System.out.println("General exception:\n" + e.getMessage());
		}
	}
	
	/**
	 * 
	 * identifies datasets to convert to RDF then generates the RDF for each of them, including the RDF header, the dataset metadata and the hasPart data RDF.  A KNB server would either store this information statically upon data insert or update or generate this information dynamically as a search is made, e.g., call from getKNBCloudRDF
	 * 
	 * @name getKNBCloudRDF 
	 * @return An rdf string
	 * 
	 */
	public void setupMetacatCloudDataSets(){
		// this should be a query  but this demo is static
		KNBDataSet ds = new KNBDataSet(_metacatConnection, _KNB_DS1);
		ds.harvestMetadataToRDF();
		_datasets.add(ds);
		ds = new KNBDataSet(_metacatConnection, _KNB_DS2);
		ds.harvestMetadataToRDF();
		_datasets.add(ds);
		ds = new KNBDataSet(_metacatConnection, _KNB_DS3);
		ds.harvestMetadataToRDF();
		_datasets.add(ds);
	}

	/**
	 * creates the RDF string for all the datasets, including the RDF header, the dataset metadata and the hasPart data RDF.  A KNB server would negotiate this content based on some query for data and an application/rdf+xml request, or similar.
	 * 
	 * @name getKNBCloudRDF 
	 * @return An rdf string
	 * 
	 */
	public String getMetacatCloudRDF(){
		String rdfString = _XML_VERSION_STRING+_RDF_HEADER + "\n";

		for(Iterator<KNBDataSet> i = _datasets.iterator(); i.hasNext();){
			KNBDataSet d = (KNBDataSet)i.next();
			rdfString+= d.getRDFString();
		}
		rdfString+="</rdf:RDF>\n";
		
		return rdfString;
	}
	
	/**
	 * creates the RDF string for all the datasets, including the RDF header, the dataset metadata and the hasPart data RDF.  A KNB server would negotiate this content based on some query for data and an application/rdf+xml request, or similar.
	 * 
	 * @name getDataONERDF 
	 * @return An rdf string
	 * 
	 */
	public String getDataONERDF(){
		String rdfString = "";

		for(Iterator<KNBDataSet> i = _datasets.iterator(); i.hasNext();){
			KNBDataSet d = (KNBDataSet)i.next();
			rdfString+= d.getRDFString();
		}
		
		return rdfString;
	}
}
