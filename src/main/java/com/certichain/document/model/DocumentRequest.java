package com.certichain.document.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;

@Document(collection = "documentrequest")
public class DocumentRequest {

    @Id
    private String id;

    private String requesterID;

    private String issuerID;

    @Field("Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date date;

    private String documentTypeID;

    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    public String getIssuerID() {
        return issuerID;
    }

    public void setIssuerID(String issuerID) {
        this.issuerID = issuerID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDocumentTypeID() {
        return documentTypeID;
    }

    public void setDocumentTypeID(String documentTypeID) {
        this.documentTypeID = documentTypeID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
