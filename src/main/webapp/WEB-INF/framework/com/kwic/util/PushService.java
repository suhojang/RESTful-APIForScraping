package com.kwic.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.security.aes.AES;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

import egovframework.rte.fdl.property.EgovPropertyService;

public class PushService {
	private static Logger logger = LoggerFactory.getLogger(PushService.class);
	private static ApnsService service;
	private static EgovPropertyService properties	= (EgovPropertyService) ContextLoader.getCurrentWebApplicationContext().getBean("propertiesService");;
	
	public synchronized static boolean push(String PSHKN,String PSHID,String cmd,String title,String msg) throws Exception{
		if(properties==null)
			properties	= (EgovPropertyService) ContextLoader.getCurrentWebApplicationContext().getBean("propertiesService");
		
		if("1".equals(PSHKN)){//GCM
			return pushGCM(properties.getString("gcmApiKey"),PSHID,cmd,title,msg);
		}else if("2".equals(PSHKN)){//APNS
			return pushAPNS(properties.getString("apnsCertPath"),AES.decode(properties.getString("encryptApnsPwd"), AES.DEFAULT_KEY,AES.TYPE_256),PSHID,cmd,title,msg);
		}
		return false;
	}
	
	public static boolean pushGCM(String apiKey,String PSHID,String cmd,String title,String msg) throws Exception{
    	Sender sender	= new Sender(apiKey);
    	Message message	= new Message.Builder()
    	.addData("P_CMD_ID"	, cmd)
    	.addData("P_SND_TM"	, new SimpleDateFormat("yyyyMMddHHmmss",Locale.KOREA).format(Calendar.getInstance().getTime()))
    	.addData("P_TTL"	, URLEncoder.encode(title,"UTF-8"))
    	.addData("P_MSG"	, URLEncoder.encode(msg,"UTF-8"))
    	.build();
		
    	Result result	= sender.send(message, PSHID, 3);
    	
    	return result.getMessageId()==null?false:true;
	}
	
	public static boolean pushAPNS(String certPath,String pwd,String PSHID,String cmd,String title,String msg) throws Exception{
		synchronized(PushService.class){
				if(service==null)
					service	= APNS.newService()
	                .withCert(certPath, pwd)
	                .withProductionDestination()
//	                .withSandboxDestination()
	                .build();
				
				String payload = APNS.newPayload()
//						.alertBody(URLEncoder.encode(msg,"UTF-8"))
						.alertBody(msg)
						.badge(1)
						.build();
				service.push(PSHID, payload);
				
			return true;
		}
	}
	//firebase fcm message send
	public static boolean firebase(String PSHID,String cmd,String title,String msg){
		URL url = null;
		try{
			/*if(properties==null)
				properties	= (EgovPropertyService) ContextLoader.getCurrentWebApplicationContext().getBean("propertiesService");
			*/
			url	= new URL("https://fcm.googleapis.com/fcm/send");
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Authorization", "key=" + properties.getString("fcmApiKey"));

	        conn.setDoOutput(true);
	        
	        Map<String,Object> info	= new HashMap<String,Object>();
	        info.put("to", PSHID);
	        
	        Map<String,Object> data	= new HashMap<String,Object>();
	        data.put("TITLE", title);
	        data.put("CMDID", cmd);
	        data.put("MSG", msg);
	        data.put("SNDTM", new SimpleDateFormat("yyyyMMddHHmmss",Locale.KOREA).format(Calendar.getInstance().getTime()));
	        info.put("data", data);

	        String json	= new ObjectMapper().writeValueAsString(info);
	        
    		logger.debug("-------- push request -----------");
    		logger.debug(json);
	        OutputStream os	= null;
	        try{
	        	os = conn.getOutputStream();
	        	os.write(json.getBytes("UTF-8"));
	            os.flush();
	        }catch(Exception e){
	        	throw e;
	        }finally{
	        	try{if(os!=null)os.close();}catch(Exception e){os=null;}
	        }
	        int responseCode	= conn.getResponseCode();
        	logger.debug("responseCode : "+responseCode);
	        if(responseCode!=200){
	        	return false;
	        }
	        
	        BufferedReader br = null;
	        String line	= null;
    		logger.debug("-------- push response -----------");
	        try{
	        	br	= new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        	while((line=br.readLine())!=null){
	        		logger.debug(line);
	        	}
	        }catch(Exception e){
	        	throw e;
	        }finally{
	        	try{if(br!=null)br.close();}catch(Exception e){br=null;}
	        }
		}catch(Exception e){
			return false;
		}
		return true;
	}
}
