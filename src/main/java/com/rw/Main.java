package com.rw;

import java.io.FileReader;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import au.com.bytecode.opencsv.CSVReader;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.rw.persistence.JedisMT;
import com.rw.API.PartnerMgr;
import com.rw.API.RWReqHandle;
import com.rw.API.SecMgr;
import com.rw.API.UserMgr;
import com.rw.persistence.RWJApplication;
import com.rw.persistence.RWJBusComp;
import com.rw.persistence.RandomDataGenerator;
import com.rw.repository.RWAPI;


/**
 *   rwengine
 */
public class Main {
	static final Logger log = Logger.getLogger(Main.class.getName());

	public static JedisPool pool;
	
	
	static {
	    JedisPoolConfig config = new JedisPoolConfig();
	    config.setMaxActive(10000);
	    config.setMaxIdle(10);
	    config.setMaxWait(10000);
	    pool = new JedisPool(config, System.getProperty("PARAM1") == null ? "localhost" : System.getProperty("PARAM1") );
	}
	
	public static Jedis getJedis(){
	    Jedis jedis = null;
	    jedis = pool.getResource();
	    jedis.connect();
	    return jedis;
	}
	
	public static void main( String[] args ) {

			Jedis j = getJedis();

			final Subscriber subscriber = new Subscriber();
	  
		  	if (System.getProperty("EMAIL") == null )
	      		System.setProperty("EMAIL","local");

			// Main thread is not multi tenant aware.
			// This is global dispatcher for all tenants.
			
            new Thread(new Runnable() {
                public void run() {
                	Jedis j = getJedis();
                	try {
                    	j.subscribe(subscriber, JedisMT.MASTER_CHANNEL_NAME);
                    } catch (Exception e) {
                        log.debug("RWEngine Error: ", e);
                    } finally {
                        subscriber.unsubscribe();
                        pool.returnResource(j);
                    }
                    
                }
            }).start();
     
            new Publisher(JedisMT.MASTER_CHANNEL_NAME).start();   
            subscriber.unsubscribe();
        }
}
