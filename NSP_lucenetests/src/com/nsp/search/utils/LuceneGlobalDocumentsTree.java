package com.nsp.search.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.FileSwitchDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * LuceneFundsTree
 * ideally should ONLY search funds and find ones that have parents  - that way you get no dead ends
 * 2 ways to do this - query parents and then see if parents have children (this way you do many child lookups)
 * 
 * or
 * query children and aggregate parents (that way you ONLY do 1 lookup across children
 * 
 * 
 * @author jscanlon
 *
 */
public class LuceneGlobalDocumentsTree {

	public static String pathToIndex = "/Users/jscanlon/Desktop/Natixis_tree/FullIndex/lucene/lucene";
	public static String _childAssets = "GlobalDocumentAsset";
	public static String pathToChildren = pathToIndex + "/" + _childAssets;
	public static String _parentAssets = "GlobalDocumentParent";
	public static String pathToParents = pathToIndex + "/" + _parentAssets;
	public static String _childSubType = "GeneralDocument";
	public static String _parentSubType = "FundDocuments";
	
	 
	private static Logger logger = LogManager.getLogger(com.nsp.search.utils.LuceneGlobalDocumentsTree.class);

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		LuceneGlobalDocumentsTree lgdt  = new LuceneGlobalDocumentsTree();
		
		 long startchild = System.currentTimeMillis();
		 lgdt.findAllChildren("GlobalDocumentAsset",pathToChildren);
		long elapsedchild  = System.currentTimeMillis()  - startchild;
		 logger.info("full children fetch took: "+ elapsedchild);
		
		long start = System.currentTimeMillis();
		List<Document> parList = lgdt.findParents(_parentSubType,pathToParents);
		long elapsed  = System.currentTimeMillis()  - start;
		 logger.info("full parent fetch took: "+ elapsed);
	
		 for (Document d: parList){
			 if(lgdt.findChildrenForAParent( _childSubType,d.get("id"))>0){
				logger.info( "children for this parent:"+  lgdt.getListOfAllChildrenForAParent(_childSubType, d.get("id")).size()  );
			 }
		}
		 long elapsed_fulllookup  = System.currentTimeMillis()  - start;
		 logger.info("full elapsed_fulllookup fetch took: "+ elapsed_fulllookup);

	}
	
	
	public int findChildrenForAParent(String _childType, String parentId) throws Exception{
		return getListOfAllChildrenForAParent(_childType, parentId).size();
	}
	
	public List<Document> getListOfAllChildrenForAParent(String _childType, String parentId) throws Exception{
		
		List<Document> childList = new ArrayList<Document>();
		
		
		BooleanQuery combined = new BooleanQuery();
		Query  query = new TermQuery(new Term("subtype",_childType));
		Query siteQuery  = new TermQuery(new Term("siteid","1228167494391"));
		Query paridQuery = new TermQuery(new Term("ImmediateParents",parentId));
		combined.add(query,BooleanClause.Occur.MUST);
		combined.add(siteQuery,BooleanClause.Occur.MUST);
		combined.add(paridQuery,BooleanClause.Occur.MUST);
		Directory index  = FSDirectory.open(new File(pathToChildren));
		
		IndexReader reader = IndexReader.open(index,true);
		
		IndexSearcher searcher = new IndexSearcher(reader);
		
		TopDocs topDocs = searcher.search(combined, 1000);
		
		logger.debug("-----findChildrenForAParent number of children for Parentid: "+parentId +"  results: " + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
				logger.debug("----------findChildrenForAParent:"+i+" "+   d.get("name"));
				childList.add(d);
	}
		Collections.sort(childList,new NameComparator());
		return childList;
	}
	

	public void findAllChildren(String _subtype, String pathToChildren2) throws Exception{
		
		BooleanQuery combined = new BooleanQuery();
		Query  query = new TermQuery(new Term("subtype",_subtype));
		Query siteQuery  = new TermQuery(new Term("siteid","1228167494391"));
		combined.add(query,BooleanClause.Occur.MUST);
		combined.add(siteQuery,BooleanClause.Occur.MUST);
		Directory index  = FSDirectory.open(new File(pathToChildren2));
				
				IndexReader reader = IndexReader.open(index,true);
		
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs topDocs = searcher.search(combined, 1000);
		logger.debug("number of children: results: " + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			logger.debug(i+" "+   d.get("name"));
		}
	}

	public List<Document> findParents(String _subtype,String pathToParents2) throws Exception {
		List<Document> list  = new ArrayList<Document>();
		
		BooleanQuery combined = new BooleanQuery();
		Query  query = new TermQuery(new Term("subtype",_subtype));
		Query siteQuery  = new TermQuery(new Term("siteid","1228167494391"));
		combined.add(query,BooleanClause.Occur.MUST);
		combined.add(siteQuery,BooleanClause.Occur.MUST);
		Directory index  = FSDirectory.open(new File(pathToParents2));
		IndexReader reader = IndexReader.open(index,true);
		
		IndexSearcher searcher = new IndexSearcher(reader);
 
		TopDocs topDocs = searcher.search(combined, 1000);
		logger.debug("number of results: " + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			list.add(d);
				logger.debug(i+" "+   d.get("name"));
	}
		Collections.sort(list,new NameComparator());
		return list;
	}
	
	public class NameComparator implements Comparator<Document> {
	    public int compare(Document p1, Document p2){
	            return p1.get("name").compareTo(p2.get("name"));
	    }
	}
	

}
