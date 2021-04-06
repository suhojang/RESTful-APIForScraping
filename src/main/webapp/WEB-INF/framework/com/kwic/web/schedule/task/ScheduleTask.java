package com.kwic.web.schedule.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronExpression;
import org.springframework.web.context.ContextLoader;

import com.kwic.log.Logger;
import com.kwic.log.LoggerFactory;
import com.kwic.util.DateUtil;
import com.kwic.util.PluginUtil;

import project.service.AGT_BAT_Service;
import project.service.AGT_SCH_Service;
import project.service.AGT_TSK_Service;
import project.service.CMN_CMN_Service;

public class ScheduleTask extends Thread {
	private static Logger logger = LoggerFactory.getLogger(ScheduleTask.class);
	
	private boolean isRunning;
	private String seq;
	private String key;
	
	private AGT_SCH_Service schService;
	private CMN_CMN_Service cmnService;
	private AGT_TSK_Service tskService;
	private AGT_BAT_Service batService;
	
	@Override
	public void run() {
		setRunning(true);
		try {
			logger.debug("Task정보를 할당 작업을 시작 합니다. 스케쥴 키 정보 :: seq[" + seq + "], key[" + key + "]");
			
			schService		= (AGT_SCH_Service) getBean("AGT_SCH_Service");
			cmnService		= (CMN_CMN_Service) getBean("CMN_CMN_Service");
			tskService		= (AGT_TSK_Service) getBean("AGT_TSK_Service");
			batService		= (AGT_BAT_Service) getBean("AGT_BAT_Service");
			
			Map<String,Object> params	= new HashMap<String,Object>();
			params.put("kind", "2");
			
			//스케쥴 데이터 가져오기
			List<Map<String,Object>> targetList	= schService.AGT_SCH_S3000J(params);
			
			logger.debug("스케쥴 키 정보 :: " + key + ", 가져온 데이터의 갯수 :: " + targetList.size());

			Map<String, Object> param	= new HashMap<String,Object>();
			param.put("batchId", key);
			
			Map<String,Object> batchInfo	= batService.AGT_BAT_V1000J(param);
			
			logger.info("가장 최근 구동 시간 :: "+batchInfo.get("TSKBAT_UDT"));
			
			for (int i = 0; i < targetList.size(); i++) {
				CronExpression cron	= null;
				try {
					cron		= new CronExpression(String.valueOf(targetList.get(i).get("SCHINF_CRON")));
					java.util.Date nexTime	= cron.getNextValidTimeAfter(new java.util.Date());
					long diff		= Math.abs(nexTime.getTime() - new java.util.Date().getTime()) / 1000;
					if (diff < 0)
						throw new Exception();
					
					//스케쥴 등록일시로 부터 등록 되지 않은 Task정보가 있다면, Task정보가 담기지 않은 시간부터 현재 시간까지 등록한다.
					Date now		= new Date();
					long nextTime	= DateUtil.stringToDate(String.valueOf(batchInfo.get("TSKBAT_UDT")), "yyyyMMddHHmmss").getTime();
					
					while (nextTime <= now.getTime()) {
						Date time	= cron.getNextValidTimeAfter(new Date(nextTime));

						param	= new HashMap<String, Object>();
						param.put("tskinf_seq", 	cmnService.nextval("TSKINF_SEQ"));
						param.put("taskId", 		PluginUtil.getTaskKey());
						param.put("scheduleId", 	String.valueOf(targetList.get(i).get("SCHINF_ID")));
						param.put("excdt", 			DateUtil.dateToString(cron.getNextValidTimeAfter(new java.util.Date(nextTime)), "yyyyMMddHHmmss"));
						param.put("status", 		"1");	//대기-배치job등록
						
						tskService.AGT_TSK_I1000J(param);	//Task정보 등록

						nextTime	= time.getTime();
					}
				} catch (Exception e) {
					//이미 시간이 지난 스케쥴 정보에 대해서는 continue 처리
					continue;
				}
			}
			
			logger.debug("Task정보를 할당 작업을 종료 합니다. 스케쥴 키 정보 :: seq[" + seq + "], key[" + key + "]");
		} catch (Exception e) {
			logger.error(e);
		} finally {
			Map<String,Object> params	= new HashMap<String,Object>();
			params.put("batchId", getKey());
			
			//배치 구동정보 등록
			try { batService.AGT_BAT_U1001J(params); } catch (Exception e) {}
			
			setRunning(false);
		}
	}
	
	public void setRunning(boolean running) {
		isRunning	= running;
	} 
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public void setSeq(String seq) {
		this.seq	= seq;
	}
	
	public String getSeq() {
		return this.seq;
	}
	
	public void setKey(String key) {
		this.key	= key;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public static Object getBean(String paramName){
		return ContextLoader.getCurrentWebApplicationContext().getBean(paramName);
	}
}
