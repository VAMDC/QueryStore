package org.rda.QueryStore.business.pipeline.tasks;

import java.time.LocalDateTime;

public abstract class PipelineTask {

	private PipelineTask followingTask;

	public abstract void runCurrentTask() throws PipelineNodeException;

	protected abstract void instanciateFollowingTask();

	public void setFollowingTask(PipelineTask task) {
		this.followingTask = task;
	}
	
	public String getTime(){
		return LocalDateTime.now().toString();
	}
	
	public void executeTask() throws PipelineNodeException {
		runCurrentTask();
		instanciateFollowingTask();
		if (null != followingTask) {
			followingTask.executeTask();
		}
	}
}
