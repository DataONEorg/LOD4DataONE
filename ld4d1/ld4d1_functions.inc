<?php
/***************************************************************************
  lod4d1_functions.inc contains the main functions for the module.
	Including menu display, queries and triplestore reset
****************************************************************************/

// change this path to the appropriate ARC2.php library path
include_once("libs/semsol-arc2-495d10b/ARC2.php");


/***************************************************************************
	ld4d1_main_page - displays the main menu for the query page
****************************************************************************/
function ld4d1_main_page(){
   global $base_url;
   $result = "<br/><hr/>"; 
   $result .= "<h2>Linked Open Data For DataONE (LOD4DataONE) Internship Query Page</h2>";
   $result .= "Details of this project can be found on the <a href=\"http://notebooks.dataone.org/lod4dataone/\">LOD4DataONE notebook</a><br/>";
   $result .= "<br/><hr/><br/>"; 
   $result .= "<h2><strong>Use Case 1: Make data browsable as RDF</strong></h2> <p>This use case focused on creating RDF triples from the data at the three DataONE data repositories (KNB, ORNL DAAC and Dryad). The initial step was to understand how to access and retrieve DataONE data from the repositories and consider obvious RDF mappings. For both KNB and Dryad, I was able to leverage the existing metadata to build RDF from about 26 records.  Using metadata made the process of building RDF easier but it shielded me from issues that exist when RDF is to be extracted from the data itself.  Since ORNL-DAAC data provided no such accessible metadata, creating the corresponding RDF exposed additional issues.  Next, it was important to consider how each RDF triple related to other data, i.e., what the object of the RDF triple should be. In some cases links were made to objects internal to the repositories describing the relationship between articles, datasets and individual files, and in others links were made to outside entities like Genbank, Treebase or DBpedia. Finally, it was important to see how this data could be viewed by users.  This was done by viewing the data in RDF browsers like Openlink Data Explorer (ODE) and Zitgist.  In order to make use of the browsers, it was necessary to consider certain vocabulary for the DataONE RDF triples, for example FOAF, location and dates.  Throughout this research effort, I used Zitgist to demonstrate the RDF that I created. It has a nice interface with templates to show the RDF data with some context, e.g., a map view showing spatial data.  Today, 08/19/2011, the zitgist RDF browser produces errors for all RDF, not just the LOD4DataONE RDF.  ODE will sometimes fail with the data and other times not.  All browsers that I tried, including Tabulator, Marbles and Disco, seem to possess that unpredictability.  I selected Zitgist and ODE because, in the event that they work, their views seemed more useful to me.";
   $result .= "<p>";
   $result .= "The following items execute SPARQL queries over the DataONE RDF data that is stored in an RDF triplestore. From an LOD perspective, someone would encounter the result records, perhaps, from searching a DataONE server, and use tools to see what they relate to in the LOD.  In order to exhibit this, all http-based uri values returned in the queries have correspoinding ODE and Zitgist links.  Select one to see the corresponding browser view, then browse through the data.";
   $result .= "</p>";
   $result .= "<ul>";
   $result .= "<li><a href=".$base_url."/lodqv/alld1ds1> Get all DataONE member node datasets (ORNL DAAC, KNB, Dryad)</a>.  Keep in mind, the results only consider the datasets that were created for the LOD4DataONE research project, that is those that were identified through RDF as datasets - 15 total.</li>";
   $result .= "<li><a href=".$base_url."/lodqv/alld1ds2> Get the DataONE objects that link to outside things and show what they link to. </a>  This performs a rather brute force search for qualities in DataONE objects that might be links, e.g., dbpedia:specis, dc:relation.</li>";
   $result .= "<li><a href=".$base_url."/lodqv/alld1ds3> Get the types of objects the DataONE data repositories manage.</a></li>";
   $result .= "<li><a href=".$base_url."/lodqv/alld1ds4> Give the locations associated with DataONE data packages</a></li>";
   $result .= "<li><a href=".$base_url."/lodqv/alld1ds5> List the authors that have published data on the DataONE data repositories and list the title and related data in the DataONE repository.</a></li>";
   $result .= "</ul>";
   $result .= "These examples focus mainly on browsing through DataONE data.  A software agent could further evaluate the results and perform other operations like computing how common things are, comparing objects or displaying content in a context, e.g., a map.  Furthermore, more semantic information could be added to the RDF, e.g., using OWL in order to express more meaning in the data.";
   $result .= "<br/><hr/><br/>"; 
   $result .= "<h2><strong>Use Case 2: Find DataONE data from a cloud of data</strong></h2> This use case focused on finding DataONE data from a cloud of knowledge.  To exhibit this, queries were performed to access RDF data from data.gov and dbpedia.org sparql endpoints.  Notice that the actual queries can be found at the bottom of this page.  A mashup was built from the result data and the DataONE data, where users can see a cloud of knowledge based on a specific query.  Users would then select objects for browsing, within those objects are related DataONE data.  This is a more subtle relationship, but requires qualities in the RDF vocabulary to match up.";
   $result .= "<p>";
   $result .= "The following items execute SPARQL queries over the multiple datasources (DataONE, DBpedia and Data.gov) to retrieve RDF data and display the mashup.  From an LOD perspective, a user would be able to select the datasources for an LOD search, query for information then browse the results.  In order to exhibit this, different queries are listed below that result in RDF data.  The content can then be browsed using an RDF browser.  Optimally, the data would be viewed in a more appropriate context, e.g., if the query has to do with taxonomy, the result might show a taxonomic hierarchy of the results.  Select one of the queries below to see the results, then browse with one of the two provided browser links";
   $result .= "</p>";
   $result .= "<ul>";
   $result .= "<li><a href=".$base_url."/lodqv/dbped_spec> Find all known species objects in the DBpedia, DataONE data cloud.</a> </li>";
   $result .= "<li><a href=".$base_url."/lodqv/dgov_loc> Find all known locations associated with objects in the data.gov, DataONE data cloud </a></li> ";
   $result .= "</ul>";
   $result .= "<p>";
   $result .= "Using remote data has its limitations.  For example, there is a lot of data available at DBpedia and Data.gov.  Despite the fact that we have limited our search to species and location, the data from each was limited to the first 1000.  The following two queries are examples of filtering data at the remote stores before obtaining triples for the list of results, the list of data is no longer limited to a random number, e.g., 1000.  Select one of the queries below to see the results, then browse with one of the two provided browser links";
   $result .= "</p>";
   $result .= "<ul>";
   $result .= "<li><a href=".$base_url."/lodqv/dbped_spec2> Filter the DBpedia and DataONE data to objects with the string Axis in the species type</a> </li>";
   $result .= "<li><a href=".$base_url."/lodqv/dgov_loc2> Filter the location results to only include data.gov objects that are within the same vicinity as the DataONE objects from the LOD4DataONE repository, i.e., within certain latitude and longitude boundaries</a></li>";
   $result .= "</ul>";
   $result .= "These examples focus mainly on obtaining data about a specific subject from distributed data sources and building RDF mashups, then allowing users to traverse the results using an RDF browser.  Alternatively, a software agent could further evaluate the results and perform other operations like further categorization of RDF objects, computing how common things are, comparing objects or displaying content in a context, e.g., showing the locations on a map would be useful when the proximity of information is important.  Furthermore, more semantic information could be added to the RDF, e.g., using OWL in order to express more meaning in the data.";
   $result .= "<br/><hr/><br/>"; 
   $result .= "<h2><strong>Additional DataONE questions</strong></h2>"; 
   $result .= "<ul>";
   $result .= "\t<li><strong>Questions answered from the ORNL DAAC RDF data:</strong>";
   $result .= "<ul>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/daac1> Give me only ORNL DAAC datasets </a></li>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/daac2> Give me the species studied in the soil respiration studies </a></li>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/daac3> Give me the authors for soil respiration studies </a></li>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/daac4> Give me the sites involved in ORNL DAAC research</a></li>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/daac5> Give me PIs for the Tumbarumba project</a></li>";
   $result .= "</ul>";
   $result .= "</li>";
   $result .= "\t<li><strong>Questions answered from the Dryad RDF data:</strong>";
   $result .= "<ul>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/dryad1> Give me only Dryad datasets </a></li>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/dryad2> Give me the topics (subject) covered in Dryad datasets </a></li>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/dryad3> Give me the species studied in the Dryad datasets</a></li>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/dryad4> Give me the authors for the Dryad datasets </a></li>";
   $result .= "</ul>";
   $result .= "</li>";
   $result .= "\t<li><strong>Questions answered from the KNB RDF data:</strong>";
   $result .= "<ul>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/knb1> Give me only KNB datasets </a></li>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/knb2> Give me the creators and titles of all KNB datasets </a></li>";
   $result .= "\t\t<li><a href=".$base_url."/lodqv/knb3> Give me the parts to all KNB objects (datasets)</a></li>";
   $result .= "</ul>";
   $result .= "</li>";
   $result .= "<br/><hr/><br/>"; 
   $result .= "<h2>DataONE Triples</h2>"; 
   $result .= "<p>The following query shows the dataset that was created for DataONE.";
   $result .= "<a href=".$base_url."/lodqv/d1ds> Get all DataONE triples </a>.<br/>";
   $result .= "</p>";
   $result .= "<br/><hr/><br/>"; 
   $result .= "<h2>DBpedia Triples</h2>"; 
   $result .= "<p>The following query shows the dataset that was retrieved from DBpedia.  This data was retrieved as RDF triples from the Dbpedia <a href=\"http://dbpedia.org/sparql\">sparql endpoint</a>.  The fact that I could easily make the query and merge or mash the data together is a good indication as to the power behind accessing distributed data over the semantic web.";
   $result .= "<a href=".$base_url."/lodqv/dbped> Get the first 1000 dbpedia records of type species </a>.<br/>";
   $result .= "</p>";
   $result .= "<hr/><br/>"; 
   $result .= "<h2>Data.gov Triples</h2>"; 
   $result .= "<p>The following query shows the dataset that was retrieved from Data.gov.  This data was retrieved as RDF triples from the data.gov <a href=\"http://data-gov.tw.rpi.edu/sparql\">sparql endpoint</a>.  The fact that I could easily make the query and merge or mash the data together is a good indication as to the power behind accessing distributed data over the semantic web.";
   $result .= "<a href=".$base_url."/lodqv/dgov> Get the first 1000 data.gov records with lat and long</a>.<br/>";
   $result .= "</p>";
   // the following would have been nice but I could not format it in time - had to start cutting bells and whistles to finish this
   // If it was necessary I would have built the appropriate graph to house all of these records but it was not.  I was able to
   // query repositories separately and then mashup results
   //$result .= "<hr/><br/>"; 
   //$result .= "<h2>All LOD4DataONE Triples</h2>"; 
   //$result .= "<p>The following link obtains all the triples currently found in the LOD4DataONE mashup datasets.";
   //$result .= "<a href=".$base_url."/lodqv/allrdf> Get all RDF triples from this RDF mashup</a>.";
   //$result .= "</p>";
   return $result;
}

/***********************************************************************************
	ld4d1_queryview - accepts a view name, executes a query  and displays the
 	appropriate RDF results
************************************************************************************/
function ld4d1_queryview($view_name){
 global $base_url;
 $result = "";
 $config = array (
   /* db */
   'db_name' => 'ciserver_arc2',
   'db_user' => 'manausci',
   'db_pwd' => 'ciserver11',
   /*store */
   'store_name' => 'arc_tests',
   /* stop after 100 error */
   'max_errors' => 100,
  );
 $config_db = array (
   /* stop after 100 error */
   'remote_store_endpoint' => 'http://dbpedia.org/sparql',
  );
 $config_gov = array (
   /* stop after 100 error */
   'remote_store_endpoint' => 'http://data-gov.tw.rpi.edu/sparql',
 );
   // the following would have been nice but I could not format it in time - had to start cutting bells and whistles to finish this
   // If it was necessary I would have built the appropriate graph to house all of these records but it was not.  I was able to
   // query repositories separately and then mashup results
 /* if($view_name=='allrdf'){
	// dbpedia data
    $store_db = ARC2::getRemoteStore($config_db);
    $query = "PREFIX dbpedia: <http://dbpedia.org/ontology/>. SELECT ?subject ?predicate ?object WHERE { ?subject dbpedia:species ?object . } LIMIT 200";
    // can't just merge rows because the two rdf query tools return different order of attributes
    $rows_db = $store_db->query($query);
    $result .= 'THE RESULTS db: '.count($rows_db).' '.print_r($rows_db,true); 

    $store_gov = ARC2::getRemoteStore($config_gov);
    $query = "PREFIX dgp32: <http://data-gov.tw.rpi.edu/vocab/p/32/> SELECT * WHERE { ?subject dgp32:lat ?object. ?subject dgp32:lon ?object}";
    $rows_gov = $store_gov->query($query,'rows');
    $result .= 'THE RESULTS gov: '.count($rows_gov).' '.print_r($rows_gov,true).'<br/>'; 

    $store = ARC2::getStore($config);
    $query = "SELECT * WHERE { ?subject ?predicate ?object . }";
    $rows_lod = $store->query($query,'rows');
    $result .= 'THE RESULTS lod: '.count($rows_lod).' '.print_r($rows_lod,true).'<br/>'; 

    $rows = array_merge($rows_lod,$rows_gov);
    $rows2 = array_merge($rows, $rows_db);
    $result .= 'THE RESULTS all: '.count($rows2).' '.print_r($rows,true).'<br/>'; 

    $keys = array( 'k1'=>'subject','k2'=>'predicate','k3'=>'object' );
    $result.=ld4d1_get_row_data($rows2, "Get ALL RDF", $keys);
 }else */

 if($view_name=='dbped'){
    $store = ARC2::getRemoteStore($config_db);
    $query = "PREFIX dbpedia: <http://dbpedia.org/ontology/> SELECT ?subject ?species WHERE { ?subject dbpedia:species ?species . } LIMIT 1000";
    $keys = array( 'k1'=>'subject','k2'=>'species' );
    $result.=ld4d1_get_data($query, "Get species objects from the dbpedia data", $store, $keys);
 }else if($view_name=='dbped_spec'){
    $store_db = ARC2::getRemoteStore($config_db);
    $query = "PREFIX dbpedia: <http://dbpedia.org/ontology/> SELECT ?subject ?species WHERE { ?subject dbpedia:species ?species . } LIMIT 1000";
    // can't just merge rows because the two rdf query tools return different order of attributes
    $rows_db = $store_db->query($query,'rows');
    //$result .= '<h2>dbped data</h2>'.print_r($rows_db,true);
    $store = ARC2::getStore($config);
    // $query = "PREFIX dbpedia: <http://dbpedia.org/ontology/> SELECT ?subject ?species WHERE { ?subject dbpedia:species ?species . } LIMIT 1000";
    $rows_d1 = $store->query($query,'rows');
    //$result .= '<h2>d1 data</h2>'.print_r($rows_d1,true);
	// have two sets of rows but they are formatted differently - use the column names
    $rows = array_merge($rows_db,$rows_d1);
    $keys = array( 'k1'=>'subject','k2'=>'species' );
    //$result .= '<h2>all data</h2>'.print_r($rows,true);
    $result.=ld4d1_get_row_data($rows, "Get all species objects from the DataONE/DBpedia cloud", $keys);
 }else if($view_name=='dbped_spec2'){
    $store_db = ARC2::getRemoteStore($config_db);
    $query = 'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX dbpedia: <http://dbpedia.org/ontology/> SELECT ?subject ?species WHERE { ?subject dbpedia:species ?species . FILTER REGEX(xsd:string(?species),"Axis" )}';
    // can't just merge rows because the two rdf query tools return different order of attributes
    $rows_db = $store_db->query($query,'rows');
    //$result .= '<h2>dbped data</h2>'.print_r($rows_db,true);
    $store = ARC2::getStore($config);
    // $query = 'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>. PREFIX dbpedia: <http://dbpedia.org/ontology/>. SELECT ?subject ?species WHERE { ?subject dbpedia:species ?species . FILTER REGEX(xsd:string(?species),"Axis" )}';
    $rows_d1 = $store->query($query,'rows');
    //$result .= '<h2>d1 data</h2>'.print_r($rows_d1,true);
	// have two sets of rows but they are formatted differently - use the column names
    $rows = array_merge($rows_db,$rows_d1);
    $keys = array( 'k1'=>'subject','k2'=>'species' );
    //$result .= '<h2>all data</h2>'.print_r($rows,true);
    $result.=ld4d1_get_row_data($rows, "Get all species objects from the DataONE/DBpedia cloud with the string Axis in the species type", $keys);
 }else if($view_name=='dgov'){
    $store = ARC2::getRemoteStore($config_gov);
    $query = "PREFIX dgp32: <http://data-gov.tw.rpi.edu/vocab/p/32/> SELECT ?subject ?latitude ?longitude WHERE { ?subject dgp32:lat ?latitude; dgp32:lon ?longitude} LIMIT 1000";
    $keys = array( 'k1'=>'subject','k2'=>'latitude', 'k3'=>'longitude' );
    $result.=ld4d1_get_data($query, "Get first 1000 data items with longitude and latidude, from data.gov",$store, $keys);
 }else if($view_name=='dgov_loc'){
    $store_gov = ARC2::getRemoteStore($config_gov);
    $query = "PREFIX dgp32: <http://data-gov.tw.rpi.edu/vocab/p/32/> SELECT ?subject ?latitude ?longitude WHERE { ?subject dgp32:lat ?latitude; dgp32:lon ?longitude} LIMIT 1000";
    $rows_gov = $store_gov->query($query,'rows');
    //$result.=print_r($rows_gov,true);
    //$result .= '<h4>gov rowset '.count($rows_gov).'</h4>';
    $store = ARC2::getStore($config);
	/** notice that this query is different because a different vocabulary is used at data.gov **/
    $query ='PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>. SELECT ?subject ?latitude ?longitude WHERE { ?subject geo:lat ?latitude; geo:long ?longitude}';
    $rows_d1 = $store->query($query,'rows');
    //$result.=print_r($rows_d1,true);
    //$result .= '<h4>d1 rowsets '.count($rows_d1).'</h4>';
	// have two sets of rows but they are formatted differently - use the column names
    $rows = array_merge($rows_gov,$rows_d1);
    //$result .= '<h4>count both rowsets '.count($rows).'</h4>';
    $keys = array( 'k1'=>'subject','k2'=>'latitude', 'k3'=>'longitude' );
    $result.=ld4d1_get_row_data($rows, "Get location objects from the cloud data", $keys);
 }else if($view_name=='dgov_loc2'){
    $store_gov = ARC2::getRemoteStore($config_gov);
    /** this is a proof of concept.  The values could have been extracted programmatically from the results of a dataone query ***/
    /** personally I ran out of time to implement this, but the idea is there ***/
    $query = 'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX dgp32: <http://data-gov.tw.rpi.edu/vocab/p/32/> SELECT ?subject ?latitude ?longitude WHERE { ?subject dgp32:lat ?latitude.  ?subject dgp32:lon ?longitude.  FILTER (xsd:decimal(?latitude) >= xsd:decimal("-9") && xsd:decimal(?latitude) <= xsd:decimal("54") && xsd:decimal(?longitude) >= xsd:decimal("-100") && xsd:decimal(?longitude) <= xsd:decimal("149") ) }';
    $rows_gov = $store_gov->query($query,'rows');
    //$result.=print_r($rows_gov,true);
    //$result .= '<h4>gov rowset '.count($rows_gov).'</h4>';
    $store = ARC2::getStore($config);
	/** notice that this query is different because a different vocabulary is used at data.gov **/
    $query ='PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>. SELECT ?subject ?latitude ?longitude WHERE { ?subject geo:lat ?latitude; geo:long ?longitude}';
    $rows_d1 = $store->query($query,'rows');
    //$result.=print_r($rows_d1,true);
    //$result .= '<h4>d1 rowsets '.count($rows_d1).'</h4>';
	// have two sets of rows but they are formatted differently - use the column names
    $rows = array_merge($rows_gov,$rows_d1);
    //$result .= '<h4>count both rowsets '.count($rows).'</h4>';
    $keys = array( 'k1'=>'subject','k2'=>'latitude', 'k3'=>'longitude' );
    $result.=ld4d1_get_row_data($rows, " Filter the location results to only include data.gov objects that are within the same vicinity as the DataONE objects from the LOD4DataONE repository, i.e., within certain latitude and longitude boundaries", $keys);
 }else if($view_name=='d1ds'){
    $store = ARC2::getStore($config);
    $query = 'SELECT * WHERE {?subject ?predicate ?object .}';
    $keys = array( 'k1'=>'subject','k2'=>'predicate','k3'=>'object' );
    $result.=ld4d1_get_data($query, "Get all DataONE member node dataset objects (ORNL DAAC, KNB, Dryad)",$store, $keys);
 }else if($view_name=='alld1ds1'){
    $store = ARC2::getStore($config);
    $query = 'PREFIX d1foaf: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#>. PREFIX dcmi: <http://purl.org/dc/dcmitype/>. PREFIX dcterms: <http://purl.org/dc/terms/>. SELECT ?dataset WHERE { {?dataset rdf:type dcmi:Dataset. ?dataset dcterms:publisher d1foaf:ORNL-DAAC .} UNION {?dataset rdf:type dcmi:Dataset. $dataset dcterms:publisher d1foaf:KNB.} UNION {?dataset rdf:type dcmi:Dataset. ?dataset dcterms:publisher d1foaf:Dryad .} }';
    $keys = array( 'k1'=>'dataset' );
    $result.=ld4d1_get_data($query, "Get all DataONE member node dataset objects (ORNL DAAC, KNB, Dryad)",$store, $keys);
 }else if($view_name=='alld1ds2'){
    $store = ARC2::getStore($config);
    $query ='PREFIX dbpedia: <http://dbpedia.org/ontology/> .  SELECT DISTINCT * WHERE { ?subject dbpedia:species ?object.}';
    $rows1 = $store->query($query,'rows');
    $rows1 = ld4d1_get_triples($rows1,'object');
    $query ='PREFIX dcterms: <http://purl.org/dc/terms/> .  SELECT DISTINCT * WHERE { ?subject dcterms:publisher ?object.}';
    $rows2 = $store->query($query,'rows');
    $rows2 = ld4d1_get_triples($rows2,'object');
    $query ='PREFIX dc: <http://purl.org/dc/elements/1.1/> .  SELECT DISTINCT * WHERE { ?subject dc:rights ?object.}';
    $rows3 = $store->query($query,'rows');
    $rows3 = ld4d1_get_triples($rows3,'object');
    $query ='PREFIX dc: <http://purl.org/dc/elements/1.1/> .  SELECT DISTINCT * WHERE { ?subject dc:creator ?object.}';
    $rows4 = $store->query($query,'rows');
    $rows4 = ld4d1_get_triples($rows4,'object');
    $query ='PREFIX dc: <http://purl.org/dc/elements/1.1/> .  SELECT DISTINCT * WHERE { ?subject rdfs:seeAlso ?object.}';
    $rows5 = $store->query($query,'rows');
    $query ='PREFIX dc: <http://purl.org/dc/elements/1.1/> .  SELECT DISTINCT * WHERE { ?subject dc:subject ?object.}';
    $rows6 = $store->query($query,'rows');
    $rows6 = ld4d1_get_triples($rows6,'object');
    $query ='PREFIX dc: <http://purl.org/dc/elements/1.1/> .  SELECT DISTINCT * WHERE { ?subject dc:relation ?object.}';
    $rows7 = $store->query($query,'rows');
    $rows = array_merge($rows1, $rows2, $rows3, $rows4, $rows5, $rows6, $rows7);
    $keys = array( 'k1'=>'subject','k2'=>'object');
    $result.=ld4d1_get_row_data($rows,"Get the DataONE objects that link to outside things and show what they link to. ",$keys);
 }else if($view_name=='alld1ds3'){
    $query ='SELECT DISTINCT ?type  WHERE { ?data rdf:type ?type. } ORDER BY ?type';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'type' );
    $result.=ld4d1_get_data($query,"Get the types of objects the DataONE data repositories manage",$store, $keys);
 }else if($view_name=='alld1ds4'){
    $query ='PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>. SELECT DISTINCT ?object ?latitude ?longitude  WHERE { ?object geo:lat ?latitude. ?object geo:long ?longitude . }';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'object','k2'=>'latitude', 'k3'=>'longitude' );
    $result.=ld4d1_get_data($query,"Get the locations associated with DataONE objects",$store, $keys);
 }else if($view_name=='alld1ds5'){
    $query ='PREFIX dc: <http://purl.org/dc/elements/1.1/>. SELECT ?author ?title ?data WHERE { ?data dc:title ?title. ?data dc:creator ?author.}';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'author', 'k2'=>'title', 'k3' =>'data' );
    $result.=ld4d1_get_data($query, "Get the authors that have published data on the DataONE data repositories and list the title and related data in the DataONE repository.",$store, $keys);
 }else if($view_name=='alld1ds6'){
    $query =' PREFIX dbpedia: <http://dbpedia.org/ontology/> .  SELECT DISTINCT ?subject ?species WHERE { ?subject dbpedia:species ?species .  }';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'subject', 'k2' => 'species');
    $result.=ld4d1_get_data($query,"Get known species associated with DataONE data sets",$store, $keys);
 }else if($view_name=='daac1'){
    $store = ARC2::getStore($config);
    $query = 'PREFIX d1foaf: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#>. PREFIX dcmi: <http://purl.org/dc/dcmitype/>. PREFIX dcterms: <http://purl.org/dc/terms/>. SELECT ?dataset WHERE { ?dataset rdf:type dcmi:Dataset. ?dataset dcterms:publisher d1foaf:ORNL-DAAC .}';
    $keys = array( 'k1'=>'dataset' );
    $result.=ld4d1_get_data($query,"Get ORNL DAAC datasets only",$store, $keys);
 }else if($view_name=='daac2'){
    $query =' PREFIX dcterms: <http://purl.org/dc/terms/>.  PREFIX dbpedia: <http://dbpedia.org/ontology/>.     PREFIX   daac:  <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DAACData.rdf#> .  SELECT ?species WHERE { ?dataset dcterms:isPartOf daac:srdb_data_v1.  ?dataset dbpedia:species ?species .  }';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'species');
    $result.=ld4d1_get_data($query,"Get all species from the soil respiration database",$store, $keys);
 }else if($view_name=='daac3'){
    $query ='PREFIX dcterms: <http://purl.org/dc/terms/>. PREFIX daact: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DAACTypes.rdf#> .  PREFIX daac: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DAACData.rdf#> . SELECT ?author WHERE { ?study dcterms:isPartOf daac:srdb_studies_v1. ?study daact:author ?author.}';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'author' );
    $result.=ld4d1_get_data($query, "Get the authors for the soil respiration studies",$store, $keys);
 }else if($view_name=='daac4'){
    $query ='PREFIX  daact:  <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DAACTypes.rdf#> .  SELECT ?sitename WHERE { ?study daact:siteName ?sitename.  }';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'sitename' );
    $result.=ld4d1_get_data($query,"Get the sitenames for all ORNL-DAAC studies (from RDF in this store)." ,$store, $keys);
 }else if($view_name=='daac5'){
    $query ='PREFIX daact: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DAACTypes.rdf#> .  PREFIX daac: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DAACData.rdf#> . SELECT ?pi WHERE { daac:tumbarumba_data_010222_081231 daact:PI ?pi.}';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'pi' );
    $result.=ld4d1_get_data($query,"Get the PIs for the Tumbarumba project",$store, $keys);
 }else if($view_name=='dryad1'){
    $query = 'PREFIX d1foaf: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#>. PREFIX dcmi: <http://purl.org/dc/dcmitype/>. PREFIX dcterms: <http://purl.org/dc/terms/>. SELECT ?dataset WHERE { ?dataset rdf:type dcmi:Dataset. ?dataset dcterms:publisher d1foaf:Dryad .}';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'dataset' );
    $result.=ld4d1_get_data($query,"Get Dryad datasets only",$store, $keys);
 }else if($view_name=='dryad2'){
    $query ='PREFIX dcterms: <http://purl.org/dc/terms/>. PREFIX dc: <http://purl.org/dc/elements/1.1/>. PREFIX d1foaf: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#>. SELECT ?topic  WHERE { ?ds dc:subject ?topic.  ?ds dcterms:publisher d1foaf:Dryad . }';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'topic' );
    $result.=ld4d1_get_data($query,"Get topics(Subjects) that are mentioned in the Dryad RDF data.",$store, $keys);
 }else if($view_name=='dryad3'){
    $query =' PREFIX dcterms: <http://purl.org/dc/terms/>.  PREFIX dbpedia: <http://dbpedia.org/ontology/>.     PREFIX d1foaf: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#> .  SELECT DISTINCT ?species WHERE { ?dataset dcterms:publisher d1foaf:Dryad . ?dataset dbpedia:species ?species .  }';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'species' );
    $result.=ld4d1_get_data($query, "Get the species studied in the Dryad Datasets",$store, $keys);
 }else if($view_name=='dryad4'){
    $query =' PREFIX dcterms: <http://purl.org/dc/terms/>.  PREFIX dc: <http://purl.org/dc/elements/1.1/> . PREFIX d1foaf: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#> .  SELECT DISTINCT ?author WHERE { ?dataset dcterms:publisher d1foaf:Dryad . ?dataset dc:creator ?author .  }';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'author' );
    $result.=ld4d1_get_data($query,"Get the authors of a  Dryad dataset." ,$store, $keys);
 }else if($view_name=='knb1'){
    $store = ARC2::getStore($config);
    $query = 'PREFIX d1foaf: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#>. PREFIX dcmi: <http://purl.org/dc/dcmitype/>. PREFIX dcterms: <http://purl.org/dc/terms/>. SELECT ?dataset WHERE { ?dataset rdf:type dcmi:Dataset. ?dataset dcterms:publisher d1foaf:KNB .}';
    $keys = array( 'k1'=>'dataset' );
    $result.=ld4d1_get_data($query,"Get KNB datasets only",$store, $keys);
 }else if($view_name=='knb2'){
    $query =' PREFIX dc: <http://purl.org/dc/elements/1.1/>. PREFIX dcterms: <http://purl.org/dc/terms/>. PREFIX dbpedia: <http://dbpedia.org/ontology/>. PREFIX  d1foaf: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/sampefoaf.rdf#> .  SELECT ?creator ?title ?dataset WHERE { ?dataset dc:creator $creator. ?dataset dc:title ?title . }';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'creator','k2'=>'title', 'k3'=>'dataset' );
    $result.=ld4d1_get_data($query,"Get creators and titles of KNB datasets.",$store, $keys);
 }else if($view_name=='knb3'){
    $query ='PREFIX dcterms: <http://purl.org/dc/terms/>. PREFIX dc: <http://purl.org/dc/elements/1.1/>. PREFIX d1foaf: <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf.rdf#> . SELECT DISTINCT ?data ?file  WHERE { ?data dcterms:publisher d1foaf:KNB. ?data dc:hasPart ?file }';
    $store = ARC2::getStore($config);
    $keys = array( 'k1'=>'data','k2'=>'file' );
    $result.=ld4d1_get_data($query, "Get the parts of a KNB object",$store, $keys);
 }
 $result .= "&nbsp;&nbsp;&nbsp;&nbsp;<br/><br/><a href=".$base_url."/lod4d1>Back</a><br/>";
 return $result; 
} 

/***********************************************************************************
	ld4d1_get_data - displays single query results, i.e., those into one
	  store only.
	this function handles three result fields as a max
************************************************************************************/
/** this function handles three result fields as a max **/
function ld4d1_get_data($query, $label, $store, $keys ){
 $result .= "<br/><hr/>"; 
 $out = array(">","<");
 $in = array("&gt;","&lt;");
 $qstr = str_replace($out, $in, $query);
 $result.= "<strong>SPARQL QUERY:</strong>".$qstr;
 $result .= "<br/><hr/><br/>"; 
 if($rs = $store->query($query,'rows')){
	$result .= ld4d1_get_row_data($rs, $label, $keys);
 }
 return $result; 
} 

/***********************************************************************************
	ld4d1_get_row_data - displays results given the row data of
	a set of queries.  The queries can be different.  The keys are
	used to extract the same data and create a table (html)
	this function handles three result fields as a max
************************************************************************************/
function ld4d1_get_row_data($rs, $label,$keys){
 $result = "";
 $result .= '<p>The results from a sparql query are displayed on this page.  All URIs are displayed with three links: in RDF-a format that a software agent would use to read and undertand the content; a view with the <a href="http://dataviewer.zitgist.com/">Zitgist RDF Browser</a>- a method that people see the RDF data; and a view with the <a href="http://demo.openlinksw.com/ode/">Openlink Data Explorer (ODE) RDF Browser</a>- another method that people see the RDF data.  Throughout this research effort, I used Zitgist to demonstrate the RDF that I created. It has a nice interface with templates to show the RDF data with some context, e.g., a map view showing spatial data.  Today, 08/19/2011, the Zitgist RDF browser produces errors for all RDF, not just the LOD4DataONE RDF.  ODE will sometimes fail with the data and other times not.  All browsers that I tried, including Tabulator, Marbles and Disco, seem to possess that unpredictability.  I selected Zitgist and ODE because, in the event that they work, their views seemed more useful to me</p>';
 if($rs){
    // format the query to show it
    $result .= "<br><h3><strong>".$label." (".count($rs)." triples in this dataset) </strong></h3>";
    //$result .= print_r($rs, true);
    $result .= '<table>';

    $count = count($keys); 
    //$result .= "<h4>".print_r($keys, true)."</h4>";
    //$result .= "<h4> the count was ".$count."</h4>";
    $result .= '<tr><td><h3><strong>'.strtoupper($keys['k1']).'</strong></h3></td>';
    if($count >= 2){
    	$result .= '<td><h3><strong>'.strtoupper($keys['k2']).'</strong></h3></td>';
	if($count >= 3 )
    	    $result .= '<td><h3><strong>'.strtoupper($keys['k3']).'</strong></h3></td>';
    }
    $result .= '</tr>';

    // these two variables setup dataviewer lines.  Unfortunately the RDF browsers are unreliable
    // Throughout this internship, Zitgist was used.  At the moment I can not open any RDF through the
    // zitgist browser.  Early on, I could not use the ODE RDF browser with any of the RDF that
    // was created.  Now it works.   --  but not consistently, sometimes it will timeout
    // Either remove them or set a variable to $dv = "" to clear this if the dataviewer fails
    $zdv = "http://dataviewer.zitgist.com/?uri=";
    $odv = "http://demo.openlinksw.com/ode/?uri=";

    foreach($rs as $row){
        $result .= '<tr>';
        $ktype = $keys['k1'].' type';
        // is this an http link?
	$pos = strpos($row[$keys['k1']],'http');
        if($row[$ktype] == 'uri' && $pos===0){
		$result.='<td>'.$row[$keys['k1']].'<a href='.$row[$keys['k1']].'> RDF&nbsp;</a>';
		$result.='<a href='.$zdv.$row[$keys['k1']].'> Zitgist&nbsp;</a>';
		$result.='<a href='.$odv.$row[$keys['k1']].'> ODE&nbsp</a></td>';
	}else{
		$result.='<td>'.$row[$keys['k1']].'</td>';
	}
        if($count>=2){
        	$ktype = $keys['k2'].' type';
		$pos = strpos($row[$keys['k2']],'http');
        	if($row[$ktype] == 'uri' && $pos===0){
			$result.='<td>'.$row[$keys['k2']].'<a href='.$row[$keys['k2']].'> RDF</a>';
			$result.='<a href='.$zdv.$row['k2'].'> Zitgist&nbsp;</a>';
			$result.='<a href='.$odv.$row['k2'].'> ODE&nbsp;</a></td>';
		}else{
			$result.='<td>'.$row[$keys['k2']].'</td>';
		}
		if($count>=3){
        		$ktype = $keys['k3'].' type';
			$pos = strpos($row[$keys['k3']],'http');
        		if($row[$ktype] == 'uri' && $pos===0){
				$result.='<td>'.$row[$keys['k3']].'<a href='.$row[$keys['k3']].'> RDF</a>';
				$result.='<a href='.$zdv.$row[$keys['k3']].'> Zitgist&nbsp;</a>';
				$result.='<a href='.$odv.$row[$keys['k3']].'> ODE&nbsp;</a></td>';
			}else{
				$result.='<td>'.$row[$keys['k3']].'</td>';
			}
		}
        }
	$result .= '</tr>';
    }
    $result .= '</table>';
 }else{
    $result .= 'empty result set';
 }
 return $result; 
} 

/***********************************************************************************
	ld4d1_get_triples - given a row of data, this returns the triples
        that are related to non DataONE content.  They are filtered based
        on the DataONE repository namespaces
************************************************************************************/
function ld4d1_get_triples($rs,$key){
    $newrs = array();
    $dryad = "http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf";
    $knb = "http://rio.cs.utep.edu/ciserver/ciprojects/sdata/KNBData.rdf";
    $daac = "http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DAACData.rdf";
    foreach($rs as $row){
	// if the object is a link, then it matters
	$ktype = $key.' type';
	$pos = strpos($row[$key],'http');
	$posdryad = strpos($row[$key],$dryad);
	$posknb = strpos($row[$key],$knb);
	$posdaac = strpos($row[$key],$daac);

	if($row[$ktype] == 'uri' && $pos===0 ){
		// filter the three internal namespaces
		if($posdryad==false && $posknb==false && $posdaac==false){
		    $newrs[] = $row;
		}
	}
    }
    return $newrs; 
} 


/***********************************************************************************
	ld4d1_rest_page - recreates the triplestore.  Needed when data changes.
************************************************************************************/
function ld4d1_reset_page(){

	/* create the connection object */
	$config = array(
	  /* db */
	  'db_name' => 'ciserver_arc2',
	  'db_user' => 'manausci',
	  'db_pwd' => 'ciserver11',
	  /* store */
	  'store_name' => 'arc_tests',
	  /* stop after 100 errors */
	  'max_errors' => 100,
	);
	/* instantiate the store */
	$store = ARC2::getStore($config);

	$r = '';

	/* setup mysql tables if not setup already */
	if (!$store->isSetUp()) {
	  $store->setUp();
	  $r .= 'the store was not setup, it is now. <br/>';
	}else{
	   /* reset the store - clear all the tables */
	   $store->reset();
	   $r .= 'The store is reset now. <br/>';
	}

	$e = $store->getErrors();
	if($e){
	   $r .= 'ERRORS in resetting store '.$print_r($e,true).'<br/>';
	}else{
	   /* LOAD will call the Web reader, which will call the
	  format detector, which in turn triggers the inclusion of an
	  appropriate parser, etc. until the triples end up in the store. */
	   /* loading each dataset individually */
	   $store->query('LOAD <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/KNBData.rdf>');
	   $store->query('LOAD <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DryadData.rdf>');
	   $store->query('LOAD <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/DAACData.rdf>');
	   $store->query('LOAD <http://rio.cs.utep.edu/ciserver/ciprojects/sdata/samplefoaf>');
	   $e = $store->getErrors();
	   if($e){
		$r .= 'ERRORS in loading to store '.$print_r($e,true).'<br/>';
	   } else {
	       $r .= 'Loaded DataONE data in store';
	   }
	}
	return $r;
}
