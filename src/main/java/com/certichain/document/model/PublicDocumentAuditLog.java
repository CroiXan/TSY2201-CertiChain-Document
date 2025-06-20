package com.certichain.document.model;

public class PublicDocumentAuditLog {
    
    private String txID;
    private String documentId;
    private String institution;
    private String userId;
    private String operation;
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
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
