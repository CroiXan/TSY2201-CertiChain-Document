package com.certichain.document.model;

public class SearchDocumentRequestInfo {

    private DocumentRequest documentRequest;
    private PrivateDocument privateDocument;

    public SearchDocumentRequestInfo() {
    }

    public SearchDocumentRequestInfo(DocumentRequest documentRequest, PrivateDocument privateDocument) {
        this.documentRequest = documentRequest;
        this.privateDocument = privateDocument;
    }

    public DocumentRequest getDocumentRequest() {
        return documentRequest;
    }

    public void setDocumentRequest(DocumentRequest documentRequest) {
        this.documentRequest = documentRequest;
    }

    public PrivateDocument getPrivateDocument() {
        return privateDocument;
    }

    public void setPrivateDocument(PrivateDocument privateDocument) {
        this.privateDocument = privateDocument;
    }
}
