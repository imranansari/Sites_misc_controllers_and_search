package com.jts.search.utils;

import java.beans.FeatureDescriptor;
import java.util.List;

import org.apache.lucene.document.Document;


public class SearchWrapper {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		LuceneGlobalDocumentsTree tree = new LuceneGlobalDocumentsTree();
		// child info
		tree._childAssets="GlobalFundAsset";
		tree._childSubType = "IntlFundChild";
		//parent info
		tree._parentAssets = "GlobalFundParent";
		tree._parentSubType = "IntlFundParent";
		//path info (do not change)
		tree.pathToChildren = tree.pathToIndex +"/"+ tree._childAssets;
		tree.pathToParents = tree.pathToIndex +"/"+ tree._parentAssets;
		
		
		List<Document> parList = tree.findParents(tree._parentSubType,tree.pathToParents);
		
		System.out.println(parList.size());
		System.out.println();
		for (Document d: parList){
			 if(tree.findChildrenForAParent( tree._childSubType,d.get("id"))>0){
				 List<Document> childList = tree.getListOfAllChildrenForAParent(tree._childSubType, d.get("id"));
				System.out.println( "children for this parent ("+d.get("name")+") :"+ childList.size() +"" );
//				fetchDocData(d);
			 }
		}
		
	}
	
	public static void fetchDocData(Document d){
		System.out.println("name:"+d.get("name"));
//		System.out.println("id:"+d.get("id"));
//		System.out.println("AuthorGPS:"+d.get("AuthorGPS"));
	}
	
	

}
