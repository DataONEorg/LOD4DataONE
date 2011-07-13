package org.dataone.nescent;
/**
 * Dryad specific classes for creating RDF from the Dryad Data Repository
 * Contains D1DryadOAIPMHMapper and DryadDataSet class definitions
 * In a more general sense this class simulates what a server providing RDF data might do
 *    -- map internal Dryad data, from a database, to RDF
 *    -- return RDF data for Dryad datasets
 * There is additional functionality to make this demo work, like specifically selecting datasets,
 *    linking internal data based on the Dryad naming and linking data to external sources like
 *    dbpedia.  This would be a part of the components that make up Dryad publishing.
 * 
 * @name org.dataone.nescent.D1DryadOAIPMHMapper.java
 * @author Aida Gandara, http://trust.cs.utep.edu/members/aida/foaf.rdf
 * 
 */
import java.util.ArrayList;
import java.util.Iterator;

/**
 * generates RDF for Dryad (http://datadryad.org)
 * @class D1DryadOAIPMHMapper 
 * @author Aida Gandara, http://trust.cs.utep.edu/members/aida/foaf.rdf
 * 
 */
public class D1DryadOAIPMHMapper{  // nothing gained here yet extends D1DataXRDFMapper{
	
	public final String _RDF_HEADER = "<rdf:RDF \n"+
    "\txmlns:dryadt=\"http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadTypes.rdf#\" \n"+
    "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"+
    "\txmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"+
    "\txmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"+
    "\txmlns:dcterms=\"http://purl.org/dc/terms/\"\n"+
    "\txmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n"+
    "\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"+
    "\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" > \n";
	
	public final String _DRYAD_D1_TREEBASE = "oai:datadryad.org:10255/dryad.82";
	public final String _DRYAD_D1_TREEBASE_F1 = "oai:datadryad.org:10255/dryad.83";
	public final String _DRYAD_D2_GENBANK = "oai:datadryad.org:10255/dryad.8437";
	public final String _DRYAD_D3_POPULAR = "oai:datadryad.org:10255/dryad.234";
	public final String _DRYAD_D3_POPULAR_F1 = "oai:datadryad.org:10255/dryad.235";
	public final String _DRYAD_D4_CSV = "oai:datadryad.org:10255/dryad.1252";
	public final String _DRYAD_D4_CSV_F1 = "oai:datadryad.org:10255/dryad.1253";
	public final String _DRYAD_D4_CSV_F2 = "oai:datadryad.org:10255/dryad.1254";
	public final String _DRYAD_D5_CSV = "oai:datadryad.org:10255/dryad.1295";
	public final String _DRYAD_D6_CSV = "oai:datadryad.org:10255/dryad.2016";
	public final String _DRYAD_D6_CSV_F1 = "oai:datadryad.org:10255/dryad.2017";
	public final String _DRYAD_D6_CSV_F2 = "oai:datadryad.org:10255/dryad.2018";
	
	public final String _DRYAD_HDL_STR = "http://hdl.handle.net/";

	public final String _DRYAD_SHORT_ID_REPLACEMENT_STRING = "<short_id>";
	
	/* this list is filled in the setup stage and all related RDF
	 * is included in the get stage
	 */
	private ArrayList<DryadDataSet> _datasets;
	
	/**
	 * the default class constructor for D1DryadOAIPMHMapper
	 * 
	 * @name D1DryadOAIPMHMapper
	 * 
	 */
	public D1DryadOAIPMHMapper(){
		_datasets = new ArrayList<DryadDataSet>();
	}
	
	/**
	 * identifies datasets to convert to RDF then generates the RDF for each of them, including the RDF header, the dataset metadata and the hasPart data RDF. A Dryad server would either store this information statically upon data insert or update or generate this information dynamically as a search is made, e.g., call from getDryadCloudRDF
	 * 
	 * @name setupDryadCloudDataSets 
	 * @return An rdf string
	 * 
	 */
	public void setupDryadCloudDataSets(){
		try{
			
			// A Dryad 
			DryadDataSet ds = new DryadDataSet(_DRYAD_D1_TREEBASE);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			ds = new DryadDataSet(_DRYAD_D3_POPULAR);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			ds = new DryadDataSet(_DRYAD_D4_CSV);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
			ds = new DryadDataSet(_DRYAD_D6_CSV);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
/*  ERROR in data*/
			ds = new DryadDataSet(_DRYAD_D2_GENBANK);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
/*  ERROR in data*/
			ds = new DryadDataSet(_DRYAD_D5_CSV);
			ds.harvestMetadataToRDF();
			_datasets.add(ds);
		}
		catch(Exception ex){
			System.out.println("ERROR: Exception "+ex.getMessage());
		}
	}
	
	/**
	 * 
	 * creates the RDF string for all the datasets, including the RDF header, the dataset metadata and the hasPart data RDF.  A Dryad server would negotiate this content based on some query for data and an application/rdf+xml request, or similar
	 * 
	 * @name getDryadCloudRDF 	
	 * @return An rdf string
	 * 
	 */
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
}
