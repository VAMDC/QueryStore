package org.rda.QueryStore.business.pipeline.tasks;

public class PipelineNodeException extends Exception{
	private static final long serialVersionUID = -5315405384001086653L;

	public PipelineNodeException(String message) {
        super(message);
    }

    public PipelineNodeException(String message, Throwable cuase) {
        super(message, cuase);
    }
}
