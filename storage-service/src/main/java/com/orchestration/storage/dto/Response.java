package com.orchestration.storage.dto;

public abstract class Response {
    private String responseMsg;
    private String responseStatus;

    @Override
    public String toString() {
        return "Response " + responseMsg +" Status "+responseStatus;
    }
}
