package com.kwic.web.schedule;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.kwic.web.schedule.task.ScheduleTask;


/**
 * 작업 실행 정보
 * @author jsh
 *
 */
public class ScheduleInfo {
	private ThreadPoolTaskScheduler scheduler;
	private ScheduleTask task;
	
	private String cron;
	private String key;
	private String name;
	private String uptTime;
	
	
	private boolean running;
	
	public ScheduleTask getTask() {
		return task;
	}

	public void setTask(ScheduleTask task) {
		this.task = task;
	}

	public ThreadPoolTaskScheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(ThreadPoolTaskScheduler scheduler) {
		this.scheduler = scheduler;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getUptTime() {
		return uptTime;
	}
	
	public void setUptTime(String uptTime) {
		this.uptTime = uptTime;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public void shutdown() {
        scheduler.shutdown();
    }
}
