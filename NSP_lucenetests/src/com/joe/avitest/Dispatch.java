package com.joe.avitest;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IPS;
import COM.FutureTense.Interfaces.Utilities;
import COM.FutureTense.XML.Template.Seed2;

public class Dispatch implements Seed2 {

	private static Logger logger = LogManager.getLogger(Dispatch.class);
	static Set<String> paramStuff ;
	private ICS ics;
    /**
     * PSEUDO CODE ONLY
     * 
     */
	@Override
	public String Execute(FTValList arg0, FTValList arg1) {
		logger.info("in Execute:arg0 "+ arg0.toString());
		logger.info("in Execute:arg1 "+ arg1.toString());

		this.paramStuff  = crapWeCareAbout();
			
			 final FTValList m = Utilities.getParams(ics.pageURL());
			 logger.info(dumpArgs(m));
			 
			
				 
				callPage( ics, getArgList());
			
		return null;
	}

	private FTValList getArgList() {
		FTValList argList = new FTValList();
		argList.setValString("C", ics.GetVar("c"));
		argList.setValString("CID", ics.GetVar("cid"));
		argList.setValString("SITE", ics.GetVar("site"));
		argList.setValString("TID",  ics.GetVar("eid")  );
		argList.setValString("TNAME", getTmplName(  ics.GetVar("childpagename")));
		argList.setValString("SLOTNAME", "xxx");
		return argList;
		
	}

	private boolean handleSSO() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void SetAppLogic(IPS ips) {
			logger.info("Dispatch.SetAppLogic()");
				
			this.ics = ips.GetICSObject();
			dumpICS(ics);
		
	}
	/**
	 * SAMPLE ONLY - NOT TESTED
	 * @param arg1 
	 * @param id
	 * @param pagename
	 * @param packedArgs
	 */
	protected void callPage(ICS ics, FTValList argList) {
		logger.info("Dispatch.callPage()");
   
        logger.info(Arrays.asList(  ics.pageCriteriaKeys(ics.GetVar("childpagename"))));
        String s = ics.runTag("RENDER.CALLTEMPLATE", argList);
        if (s != null) {
            ics.StreamText(s);
        }
        }
	
	public FTValList getTagArgsFromICS(ICS ics, Set<String> args){
		
		FTValList ftvl = new FTValList();
			for (String s: args){
				ftvl.setValString(s.toUpperCase(), ics.GetVar("s"));
			}

		
		
		return null;
		
		
		
	}

	private Set<String> crapWeCareAbout() {
		Set<String> t = new HashSet<String>();
		t.add("pagename");
		t.add("childpagename");
		t.add("cid");
		t.add("c");
		t.add("site");
		return t;
	 };
		
	
	 public String dumpArgs(FTValList vIn){
			StringBuffer bf = new StringBuffer();
			Enumeration<String> theKeys = vIn.keys();
			
			while ( theKeys.hasMoreElements() )
			{
			     String name = theKeys.nextElement();
			     
			     bf.append(name );
			     bf.append("=");
			     bf.append(vIn.getVal(name) +"\n\r");
			    
			}
			return bf.toString();
			
			
		}

	 private void dumpICS(ICS ics){
			Enumeration<String> e = ics.GetVars();
			while(e.hasMoreElements()){
				String key = e.nextElement();
					String value = ics.GetVar(key);
					logger.info(key +" = "+ value);
			}
	 }
	
	 public static void main(String[] args) {
		 
		 String pagename= "avisports/Page/HomeLayout1";
		 
		 logger.info(getTmplName(pagename));
		 
		
	}
	 
	 private static String getTmplName(String in){
		 
		 return in.substring( in.lastIndexOf("/")+1   );
	 }
	
       
}
