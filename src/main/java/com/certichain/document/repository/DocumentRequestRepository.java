package com.certichain.document.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.certichain.document.model.DocumentRequest;

@Repository
public interface DocumentRequestRepository extends MongoRepository<DocumentRequest,String> {

    @Query("{ 'RequesterID': ?0 }")
    List<DocumentRequest> findByRequesterID(String requesterID);

    @Query("{ 'IssuerID': ?0 }")
    List<DocumentRequest> findByIssuerID(String issuerID);

    @Query("{ 'Date' : { $gte: ?0, $lte: ?1 } }")
    List<DocumentRequest> findByDateBetween(Date start, Date end);
    
}
