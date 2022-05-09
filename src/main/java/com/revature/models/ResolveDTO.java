package com.revature.models;

public class ResolveDTO {

    public String resolveChoice;
    public int requestID;
    public int resolverId;

    public ResolveDTO(String resolveChoice, int requestID, int authorId) {
        this.resolveChoice = resolveChoice;
        this.requestID = requestID;
        this.resolverId = authorId;
    }

    public ResolveDTO() {
    }

    public ResolveDTO(String resolveChoice, int requestID) {
        this.resolveChoice = resolveChoice;
        this.requestID = requestID;
    }
}