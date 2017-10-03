package org.rda.QueryStore.business.pipeline;

import org.rda.QueryStore.business.pipeline.tasks.PipelineNodeException;
import org.rda.QueryStore.business.pipeline.tasks.PipelineTask;

public class Pipeline {
	
	public Pipeline(PipelineTask rootTask) throws PipelineNodeException {
		super();
		this.rootTask = rootTask;
	}

	private PipelineTask rootTask;
	
	public void executePipeline() throws PipelineNodeException{
		rootTask.executeTask();
	}

}
