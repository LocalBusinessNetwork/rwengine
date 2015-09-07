package com.rw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.rw.persistence.JedisMT;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Publisher {
	private static final Logger logger = Logger.getLogger(Publisher.class);
 
    private final String channel;
 
    public Publisher(String channel) {
        this.channel = channel;
    }
 
    public void start() {
        logger.info("Type your message (quit for terminate)");
        while (true) {
        	try {
        		TimeUnit.HOURS.sleep(1);
        		// wake up once every hr
        		// Kick off cron jobs
        		JedisMT jedisMt = new JedisMT(); 	
    			jedisMt.publish(JedisMT.CRON, "cron");       		
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
