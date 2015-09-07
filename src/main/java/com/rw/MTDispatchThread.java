package com.rw;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.rw.API.ContactsMgr;
import com.rw.API.CronJobMgr;
import com.rw.API.Mail;
import com.rw.API.RWReqHandle;
import com.rw.Enricher.EnrichmentFactory;
import com.rw.persistence.JedisMT;
import com.rw.persistence.dataMT;

public class MTDispatchThread extends dataMT {
	static final Logger log = Logger.getLogger(MTDispatchThread.class.getName());

	private String request = null;
	private String payload = null;
	
	public MTDispatchThread(String tenantStr, String requestStr, String payloadStr) {
		super(tenantStr);
		request = requestStr;
		payload = payloadStr;
	}
	
	public void run()
	{
    	try { 		   		
    		
    		if ( request.equals(JedisMT.PARTY_ENRICHMENT_CHANNEL) ) {
				EnrichmentFactory f = new EnrichmentFactory();
				f.EnrichParty(payload);
    		}
    		else if ( request.equals(JedisMT.PARTY_TRIANGULATE_CHANNEL) ) {
    			// TODO : 
    		}
    		else if ( request.equals(JedisMT.CONTACT_DEDUP_CHANNEL) ) {
    			// TODO : 
    		}
    		else if ( request.equals(JedisMT.ASYNC_EMAIL_CHANNEL) ) {
    			JedisMT jedisMt = new JedisMT(); 			
    			String payloadStr = jedisMt.get(payload);
    			JSONObject emailRquest = new JSONObject(payloadStr);
    			try {
    				Mail m = new Mail();
    				m.setUserId(emailRquest.getString("userid"));
    				m.setTenantKey(tenant.get());
    				m.SendSES(emailRquest.getJSONObject("header"), emailRquest.getJSONObject("body"), null);
    				jedisMt.del(payload);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    			}
    		}
    		else if ( request.equals(JedisMT.ASYNC_API_CHANNEL) ) {   			
				JedisMT jedisMt = new JedisMT(); 			
    			String payloadStr = jedisMt.get(payload);
    			JSONObject apiRquest = new JSONObject(payloadStr);
    			try {
    				String className = new String (apiRquest.getString("module"));
    				Class cls = Class.forName(className); 
    				RWReqHandle obj = (RWReqHandle) cls.newInstance();    
    				obj.setUserId(apiRquest.getString("userid"));
    				obj.setTenantKey(tenant.get());
    				
    				apiRquest.remove("module");
    				obj.handleRequest(apiRquest);
    				// This payload has been consumed
    				jedisMt.del(payload);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    			}
    		}
    		else if ( request.equals(JedisMT.CRON) ) {
    			CronJobMgr obj = new CronJobMgr();
				obj.setTenantKey(tenant.get());
				obj.run();
    			// TODO : CRON jobs
    		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.debug("RWEngine Error: ", e);
		}       
	}
}
