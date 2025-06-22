package com.certichain.document.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.certichain.document.model.DocumentRequest;
import com.certichain.document.repository.DocumentRequestRepository;

@Service
public class DocumentRequestService {

    private final DocumentRequestRepository docRepo;
    private final MongoTemplate template;

    public DocumentRequestService(DocumentRequestRepository docRepo, MongoTemplate template) {
        this.docRepo = docRepo;
        this.template = template;
    }

    public List<DocumentRequest> getAll(){
        return docRepo.findAll();
    }

    public DocumentRequest createDocumentRequest(DocumentRequest docRequest){
        docRequest.setDate(new Date());
        return docRepo.save(docRequest);
    }

    public Optional<DocumentRequest> getDocumentRequestById(String Id){
        return docRepo.findById(Id);
    }

    public Optional<DocumentRequest> updateDocumentRequest(DocumentRequest docRequestUpdate){

        Optional<DocumentRequest> findedRequest = docRepo.findById(docRequestUpdate.getId());

        if(findedRequest.isPresent()){
            return Optional.of(docRepo.save(docRequestUpdate));
        }else{
            return Optional.empty();
        }
        
    }

    public Optional<DocumentRequest> updateDocumentRequestStatus(String Id, String status){

        Optional<DocumentRequest> findedRequest = docRepo.findById(Id);

        if(findedRequest.isPresent()){
            DocumentRequest request = findedRequest.get();
            request.setState(status);
            return Optional.of(docRepo.save(request));
        }else{
            return Optional.empty();
        }
        
    }

    public boolean deleteDocumentRequestById(String Id){
        docRepo.deleteById(Id);

        Optional<DocumentRequest> findedRequest = docRepo.findById(Id);

        if(findedRequest.isPresent()){
            docRepo.deleteById(Id);
            return true;
        }else{
            return false;
        }
    }

    public List<DocumentRequest> getByRequester(String requesterID) {
        return docRepo.findByRequesterID(requesterID);
    }

    public List<DocumentRequest> getByIssuer(String issuerID) {
        return docRepo.findByIssuerID(issuerID);
    }

    public List<DocumentRequest> getByDate(Date startDate, Date endDate) {
        return docRepo.findByDateBetween(startDate, endDate);
    }

    public List<DocumentRequest> getByFilters(
        String requesterID,
        String issuerID,
        Date from,
        Date to
    ) {
        Query q = new Query();
        if (requesterID != null && !requesterID.isBlank()) {
            q.addCriteria(Criteria.where("RequesterID").is(requesterID));
        }
        if (issuerID != null && !issuerID.isBlank()) {
            q.addCriteria(Criteria.where("IssuerID").is(issuerID));
        }
        if (from != null) {
            q.addCriteria(Criteria.where("Date").gte(from));
        }
        if (to != null) {
            q.addCriteria(Criteria.where("Date").lte(to));
        }
        return template.find(q, DocumentRequest.class);
    }

}
