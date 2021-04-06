package com.kwic.web.schedule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.context.ContextLoader;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.web.schedule.task.ScheduleTask;

import project.service.AGT_BAT_Service;

public class ScheduleDemon {
	private static Logger log = LoggerFactory.getLogger(ScheduleDemon.class);
	
	private static ScheduleDemon instance;
	
	private ThreadPoolTaskScheduler scheduler;
	
	private AGT_BAT_Service batService;
	
	private static Map<String, ScheduleInfo> contextMap = new HashMap<String, ScheduleInfo>();
	
	public static ScheduleDemon getInstance() {
		synchronized (ScheduleDemon.class) {
			if (instance == null) {
				instance = new ScheduleDemon();
			}
			return instance;
		}
	}
	
	public synchronized void startup() throws Exception {
		Map<String, Object> scheduleInfoMap		= new HashMap<String, Object>();
		try {
			batService	= (AGT_BAT_Service) getBean("AGT_BAT_Service");
			
			List<Map<String,Object>> targetList	= batService.AGT_BAT_S1000J(new HashMap<String,Object>());
			for (int i = 0; i < targetList.size(); i++) {
				scheduleInfoMap.put("seq", 			targetList.get(i).get("TSKBAT_SEQ"));
				scheduleInfoMap.put("key",	 		targetList.get(i).get("TSKBAT_ID"));
				scheduleInfoMap.put("name", 		targetList.get(i).get("TSKBAT_NM"));
				scheduleInfoMap.put("cron", 		targetList.get(i).get("TSKBAT_CRON"));
				
				add(scheduleInfoMap);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * Task 추가
	 */
	public synchronized void add(Map<String, Object> scheduleInfoMap) {
		ScheduleTask task = null;
		try {
			task	= new ScheduleTask();
			
			String seq		= String.valueOf(scheduleInfoMap.get("seq"));
			String key		= String.valueOf(scheduleInfoMap.get("key"));
			String name		= String.valueOf(scheduleInfoMap.get("name"));
			String cron		= String.valueOf(scheduleInfoMap.get("cron"));
			
			ScheduleInfo info	= new ScheduleInfo();
			info.setKey(key);
			info.setName(name);
			info.setCron(cron);
			info.setCron(cron);
			
			remove(key);
			
			scheduler = new ThreadPoolTaskScheduler();
			info.setTask(task);
			info.setScheduler(scheduler);
			
			contextMap.put(key, info);
			
			//key값 저장
			contextMap.get(key).getTask().setKey(key);
			contextMap.get(key).getTask().setSeq(seq);
			//스케쥴 구동 시작
			contextMap.get(key).getScheduler().initialize();
			contextMap.get(key).getScheduler().schedule(task, new CronTrigger(cron));
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public synchronized void remove(String key) {
		if (contextMap.get(key) != null) {
			contextMap.get(key).getScheduler().shutdown();
			contextMap.remove(key);
		}
	}
	
	public synchronized void shutdown() throws Exception {
		try {
			Iterator<String> iter	= contextMap.keySet().iterator();
			while (iter.hasNext()) {
				String key	= iter.next();
				if (contextMap.get(key) != null) {
					contextMap.get(key).getScheduler().shutdown();
					contextMap.remove(key);
				}
			}
			
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	
	
	public static Object getBean(String paramName){
		return ContextLoader.getCurrentWebApplicationContext().getBean(paramName);
	}
	
	public static void main(String[] args) {
	}
}
