package com.tt.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
// import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// import org.apache.log4j.Logger;

// import tt.Constrain;
import com.tt.batch.BatchRunTask;
import com.tt.batch.OrderRunTask;
// import tt.facade.SQLFacade;
// import tt.util.CommonUtils;
// import tt.util.DbUtils;

public class ApplicationListener implements ServletContextListener {
	
	private List<Timer> timerList = new ArrayList<Timer>();
	
	private List<BatchRunTask> taskList = new ArrayList<BatchRunTask>();
	
	// private String className = this.getClass().getSimpleName();
	
	// private Logger log = Logger.getLogger(this.className);
	
	// private Timer healthTimer = null;
	
	// private DbUtils defaultDB = Constrain.DEFAULT_DB;
	
	private Map<String,String> configMap = null;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
//			CommonUtils.loadLog4j();
//			this.log.info(this.className+".contextInitialized() !!");
			if(event!=null) {
				event.getServletContext().setAttribute("ApplicationListener", this);
			}
////			this.configMap = SQLFacade.getInstance(this.defaultDB).findSystemConfigs(null, null, null, true);
////			this.log.info(this.className+".contextInitialized().configMap == " + this.configMap);
//			this.schedule(0);  //OrderQueue
////			this.schedule(1);  //HotfixQueue
//			this.healthTimer = new Timer();
//			TimerTask healthTask = new TimerTask() {			
//				public void run() {
//					autoReloadTimer();
//				}
//			};
//			this.healthTimer.schedule(healthTask, 10*60*1000, 10*60*1000);
			
			System.out.println("test contextInitialized ====");
			
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if(event!=null) {
			event.getServletContext().removeAttribute("ApplicationListener");
		}
		if(this.timerList!=null) {
			for(int i=0; i<this.timerList.size(); i++) {
				this.cancelTimer(i, true);
			}
			this.timerList.clear();
		}
		if(this.taskList!=null) {
			this.taskList.clear();
		}
	}
	
	private BatchRunTask schedule(int index) {
		try {
			boolean isActive = ((this.configMap!=null)?"Y".equals((String)this.configMap.get("IS_APPLICATIONLISTENER_ON")):false);
			if(isActive) {
				if(index==0) {  //OrderQueue
					return this.schedule(index, new OrderRunTask(), 1l, Long.parseLong((String)this.configMap.get("ORDER_BATCH_TIMESTEP")));
				} else if(index==1) {  //HotfixQueue
//					return this.schedule(index, new HotfixRunTask(), 1l, Long.parseLong((String)this.configMap.get("HOTFIX_BATCH_TIMESTEP")));
				} 
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private BatchRunTask schedule(int index, BatchRunTask task, long delay, long period) {
		Timer timer = new Timer();
		if(this.timerList.size()==index) {
			this.timerList.add(timer);
			this.taskList.add(task);
		} else {
			this.timerList.set(index, timer);
			this.taskList.set(index, task);
		}
		timer.schedule(task, delay*1000l, period*1000l);
		return task;
	}
	
	public void autoReloadTimer() {
		if(this.timerList==null || this.timerList.size()==0 
				|| this.taskList==null || this.taskList.size()==0) {
			return;
		}
		for(int i=0; i<this.taskList.size(); i++) {
			try {
				BatchRunTask task = (BatchRunTask) this.taskList.get(i);
				if(task==null || task.isStopTask()) {
					continue;
				}
				long diffTime = task.getDiffPollingTime();
				if((diffTime>10 && !task.isRunning())
						|| (diffTime>20 && task.isRunning())) {
					this.reloadTimer(i);
				}
			} catch(Throwable e) {
			}
		}
	}
		
	public void reloadTimer(int index) {
		if(this.timerList==null || this.timerList.size()<=index) {
			return;
		}
		Timer oldTaskTimer = (Timer) this.timerList.get(index);	
		if(oldTaskTimer!=null) {
			oldTaskTimer.cancel();
		}
		BatchRunTask oldTask = (BatchRunTask) this.taskList.get(index);
		BatchRunTask newTask = this.schedule(index);
		if(oldTask!=null && newTask!=null) {
			newTask.setLastPollingTime(oldTask.getLastPollingTime());
		}
	}
	
	public void cancelTimer(int index, boolean stopTask) {
		try {
			if(this.timerList==null || this.timerList.size()<=index) {
				return;
			}
			BatchRunTask task = (BatchRunTask) this.taskList.get(index);
			if(task!=null) {
				for(int i=0; i<10; i++) {
					if(task.isRunning()) {
						Thread.sleep(1000l);
					}
				}
				if(task.isRunning()) {
					return;
				}
				try {
					Timer taskTimer = (Timer) this.timerList.get(index);
					if(taskTimer!=null) {
						taskTimer.cancel();
					}
				} catch(Throwable e) {
				}
				task.setStopTask(stopTask);
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

	public List<BatchRunTask> getTaskList() {
		return taskList;
	}
	
}

