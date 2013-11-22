package com.ever365.ecm.repo;

public class RepositoryRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 8786908801290346228L;
	private String msgId;
    
    /**
     * Constructor
     * 
     * @param msgId     the message id
     */
    public RepositoryRuntimeException(String msgId)
    {
        this.msgId = msgId;
    }

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
    
}
