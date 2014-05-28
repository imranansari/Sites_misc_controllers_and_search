package com.nsp.search.utils;


import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class NewsSearcher {

	private static Logger logger = LogManager.getLogger(com.nsp.search.utils.NewsSearcher.class);

	
	/**
	 * Static var that can be overridden
	 */
	public static String _pathname = "E:/Fatwire/Shared/lucene";
	/**
	 * Static var that can be overridden
	 */
	public static String _assetType = "NSP_Content_C";
	/**
	 * Static var that can be overridden
	 */
	public static String _subtype = "News";
	
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		
		
			NewsSearcher ns  = new NewsSearcher();
			ns._pathname = "/Users/jscanlon/Documents/Engagements/OCS/Insperity/indexes";
			
		//	ns.findAllforType("Announcement", _pathname, _assetType);
			
			List<String> roleList = new ArrayList<String>();
				roleList.add("205");
				roleList.add("all");
				
			List<String> clientList = new ArrayList<String>();
		
				clientList.add("1148702");
				clientList.add("1148704");
				clientList.add("2075700");
				clientList.add("all");
				
				
			List<String> regionsList = new ArrayList<String>();
				regionsList.add("_Home");
				regionsList.add("all");
				
			List<String> productsList = new ArrayList<String>();
				productsList.add("2");
				productsList.add("5");
				productsList.add("0");
				
				
//				ns.findAllforType("Announcement", _pathname, _assetType);
				
//			List<DynaBean> bList = 	ns.findAllforTypeAsBean("Announcement", _pathname, _assetType);
//				for (DynaBean b: bList){
//					
//					DynaProperty[] dynaProperties = b.getDynaClass().getDynaProperties();
//								int propSz = b.getDynaClass().getDynaProperties().length;
//								for (int i=0, n=dynaProperties.length; i<n; i++) {
//							        String propertyName = dynaProperties[i].getName();
//							       logger.info(propertyName);
//							    }
//				}
			
			ns.findAllFiltered(null, _pathname, _assetType, null, roleList, 
						clientList, regionsList,
						null);	
			
//			ns.findAllFiltered("News", _pathname, _assetType, null ,ns.convertDashedStringToList("205-206-221"), 
//					clientList, regionsList,
//					productsList);
//
//			
//			ns.findAllFiltered("News", _pathname, _assetType, "1295546968566", roleList, 
//					clientList, regionsList,
//					null);
			
			
		
	}
	
	private String pathToIndex(){
		return _pathname+"/"+_assetType;
	}
/**
 * Finds all Documents (non-filtered) in index
 * @param _subtype Subtype (Defintion) to return
 * @param assettype AssetType to search on
 * @param pathname  Path to main index dir
 * @return List of Document (lucene Document)
 * @throws Exception
 */
public List<Document> findAllforType(String _subtype, String assettype, String pathname) throws Exception{
	List<Document> returnedDocList = new ArrayList<Document>();
		Query  query = new TermQuery(new Term("subtype",_subtype));
		Directory index  = FSDirectory.open(new File(pathToIndex()));
				
				IndexReader reader = IndexReader.open(index,true);
		
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs topDocs = searcher.search(query, 1000);
		logger.debug("number of children: results: " + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			returnedDocList.add(d);
			outputDoc(d);
	
			
		}
		return returnedDocList;
	}



public List<DynaBean> findAllforTypeAsBean(String _subtype, String assettype, String pathname) throws Exception{
	List<DynaBean> returnedDocList = new ArrayList<DynaBean>();
	Query  query = new TermQuery(new Term("subtype",_subtype));
	Directory index  = FSDirectory.open(new File(pathToIndex()));
			
			IndexReader reader = IndexReader.open(index,true);
	
	IndexSearcher searcher = new IndexSearcher(reader);
	TopDocs topDocs = searcher.search(query, 1000);
	logger.debug("number of children: results: " + topDocs.totalHits);
	ScoreDoc[] hits = topDocs.scoreDocs;
	for (int i = 0; i < hits.length; i++) {
		int docId = hits[i].doc;
		Document d = searcher.doc(docId);
		returnedDocList.add(convertToDynaBean(d));
		outputDoc(d);

		
	}
	return returnedDocList;
}

private DynaBean convertToDynaBean(Document d) throws IllegalAccessException, InstantiationException {
	String type  = d.get("subtype");
	List<Field> allFields = d.getFields();
	int fsize = allFields.size();
	 
	 
	 List<DynaProperty> dList = new ArrayList<DynaProperty>();
	 
			 for (Field f: allFields){
				 dList.add(new DynaProperty(type+"_"+ f.name()  ,String.class));
			 }
			
	 DynaProperty[] props  = dList.toArray(new DynaProperty[dList.size()]);
	 BasicDynaClass dynaClass = new BasicDynaClass(type, null, props);
	
	 logger.info(dynaClass.getName());
	 
	return dynaClass.newInstance();
}

/**
 * @param _subtype subtype (definition) to search against
 * @param pathname path to the main index directory
 * @param assettype assettype to search against (will be added to the pathname variable)
 * @param assetid assetid to add to further filter search (used for determining if item should be viewable - if List > 0 results
 * @param rolesList list of Roles to filter on
 * @param clientsList list of Client ID's to be filtered on 
 * @param regionsList list of Regions to be filtered on 
 * @param productlinesList list of Product Lines to be filtered on
 * @return List<Document> where Document is lucene document
 * @throws IOException
 */
public List<Document> findAllFiltered(String _subtype, String pathname, String assettype,String assetid, List<String> rolesList,List<String> 
		clientsList, List<String> regionsList, List<String> productlinesList) throws IOException{
	Long start = System.currentTimeMillis();
	
	List<Document> returnedDocList = new ArrayList<Document>();
				
	BooleanQuery combined = new BooleanQuery();
	if (rolesList != null)   rolesList.add("all");    combined.add(convertListToORQuery(rolesList, "roles"),BooleanClause.Occur.MUST);
	if (clientsList != null) combined.add(convertListToORQuery(clientsList, "clients"),BooleanClause.Occur.MUST);
	if (regionsList != null) combined.add(convertListToORQuery(regionsList, "regions"),BooleanClause.Occur.MUST);
	if (productlinesList != null) combined.add(convertListToORQuery(productlinesList, "product_line"),BooleanClause.Occur.MUST);
	
	logger.info("searching for: "+ "rl:"+rolesList+"cl:"+clientsList+"rl:"+regionsList+"pl:"+productlinesList);
	
	if (assetid !=null) {
		TermQuery t = new TermQuery(new Term("id",assetid));
		combined.add(t,BooleanClause.Occur.MUST);
	}
		
	
			Directory index  = FSDirectory.open(new File(pathToIndex()));
			
			IndexReader reader = IndexReader.open(index,true);
			
			IndexSearcher searcher = new IndexSearcher(reader);
			
			Map terms = getFullTerms(reader, "gsttag", searcher);
			logger.info(terms);
			
			TopDocs topDocs = searcher.search(combined, 1000);
			Long elapsedTime = System.currentTimeMillis() - start;
			logger.info("search took: "+ elapsedTime * .001+" seconds" );
			logger.debug("number of children: results: " + topDocs.totalHits);
			ScoreDoc[] hits = topDocs.scoreDocs;
			for (int i = 0; i < hits.length; i++) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				returnedDocList.add(d);
				
				outputDoc(d);
			}
			
			return returnedDocList;
	}


/**
 * Converts List of Strings to a Query object that uses OR conditions
 * @param inlist List of Strings
 * @param field  Term (field) that will be searched against for this DataSet
 * @return
 */
public Query convertListToORQuery(List<String> inlist, String field){
	BooleanQuery retQuery   = new BooleanQuery();
	for (String s: inlist){
			TermQuery t = new TermQuery(new Term(field,s));
			retQuery.add(t,BooleanClause.Occur.SHOULD);
	}
	return retQuery;
}

public List convertDashedStringToList(String in ){
	List<String> retList = new ArrayList<String>();
	String [] tmp = in.split("-");
		for (String s: tmp){
			retList.add(s);
		}
	return retList;
}

private void outputDoc(Document d){
	List<Field> allFields = d.getFields();
		StringBuilder sb = new StringBuilder();
		sb.append(d.get("subtype")+" "+   d.get("id") + "CONTAINS: ");
	 for (Field f: allFields){
		 if (!f.name().equals("body"))
		 sb.append(f.name() +":"+ d.get(f.name() )) ;
	 }
//	logger.debug(sb.toString());
	
//	logger.debug(  d.get("subtype")+" "+   d.get("id") +" " +  d.get("name") + " " + d.get("updateddate") +" "+ d.get("clients")
//			+" "+d.get("regions") + " "+ d.get("product_lines"));
}
		
public static Map<String, Integer> getFullTerms(IndexReader ir, String fieldName, IndexSearcher is)
		  throws IOException {
		    Map<String, Integer> termMap = new LinkedHashMap<String, Integer>();
		    TermEnum terms = ir.terms(new Term(fieldName, ""));
		    while ( terms.next() && fieldName.equals(terms.term().field())) {
		    	logger.debug(terms.term().text() +":"+ terms.docFreq());
		      termMap.put(terms.term().text(), terms.docFreq());
		    }
		    return termMap;
		  }
}
