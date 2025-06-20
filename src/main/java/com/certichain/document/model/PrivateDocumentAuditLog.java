package com.certichain.document.model;

public class PrivateDocumentAuditLog {

    private String txID;
    private String documentId;
    private String institution;
    private String userId;
    private String operation;
    private String oldState;
    private String newState;
    private String timestamp;
    
    public String getTxID() {
        return txID;
    }
    public void setTxID(String txID) {
        this.txID = txID;
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getOldState() {
        return oldState;
    }
    public void setOldState(String oldState) {
        this.oldState = oldState;
    }
    public String getNewState() {
        return newState;
    }
    public void setNewState(String newState) {
        this.newState = newState;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
