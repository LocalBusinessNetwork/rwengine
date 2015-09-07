package com.rw;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.rw.API.ContactsMgr;
import com.rw.persistence.JedisMT;

import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {
	private static Logger logger = Logger.getLogger(Subscriber.class);
	public static final String CHANNEL1_NAME = "RWENGINE1.0-PartyEnrichent";
	 
    @Override
    public void onMessage(String channel, String message) {
    	
        logger.trace("Message received. Channel: " + channel +  " Msg: " + message);
        
        if ( channel.equals(JedisMT.MASTER_CHANNEL_NAME)) {
        	String [] msgParts = message.split(":");
        	String tenantStr = msgParts[0];
        	
        	tenantStr = tenantStr.substring(0, tenantStr.length()-1); //remove the dot
            
        	String subChannelStr = msgParts[1];
        	String payload = msgParts[2];
        	new MTDispatchThread(tenantStr,subChannelStr, payload ).start();
        }  
        
        /* 
        	if ( channel.equals(Main.MASTER_CHANNEL_NAME)) {
        
        	String [] msgParts = message.split(":");
    		try {
        		JSONObject data = new JSONObject();
        		
        		data.put("module", msgParts[0]);
				data.put("act", msgParts[1]);
				data.put("userid", msgParts[2]);

				String params = msgParts.length > 3 ? msgParts[3] : null;
				if ( params != null ) {
					String [] paramPairs = params.split("&");
					
					for ( int i = 0; i < paramPairs.length; i++) {
						String [] paramPair = paramPairs[i].split("=");
						data.put(paramPair[0],paramPair[1]);
					}
				}
				new ApiThread(data).start();
	        } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }  	*/
       
    }
 
    @Override
    public void onPMessage(String pattern, String channel, String message) {
    	
 
    }
 
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
 
    }
 
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
 
    }
 
    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
 
    }
 
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
 
    }
}
