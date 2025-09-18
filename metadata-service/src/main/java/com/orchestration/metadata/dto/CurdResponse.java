package com.orchestration.metadata.dto;

public class CurdResponse extends Response{
  private  String curdOperation;

    private String responseMsg;
    private String responseStatus;

    public CurdResponse(String curdOperation, String responseMsg, String responseStatus) {
        this.curdOperation = curdOperation;
        this.responseMsg = responseMsg;
        this.responseStatus = responseStatus;
    }

    @Override
    public String getResponseMsg() {
        return this.curdOperation+" completed with "+this.responseStatus+" : "+this.responseMsg;
    }
}

