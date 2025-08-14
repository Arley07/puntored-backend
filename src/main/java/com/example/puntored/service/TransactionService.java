package com.example.puntored.service;

import com.example.puntored.dto.request.BuyRequest;
import com.example.puntored.model.Transaction;
import com.example.puntored.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.*;
import java.util.*;

@Service
public class TransactionService {

    private final TransactionRepository repo;
    private final EntityManager em;

    public TransactionService(TransactionRepository repo, EntityManager em) {
        this.repo = repo;
        this.em = em;
    }

    public Transaction createPending(BuyRequest req, String createdBy) {
        Transaction tx = new Transaction();
        tx.setSupplierId(req.getSupplierId());
        tx.setCellPhone(req.getCellPhone());
        tx.setValue(req.getValue()); // ahora es Double
        tx.setStatus("PENDING");
        tx.setCreatedBy(createdBy);
        return repo.save(tx);
    }

    public Transaction markSuccess(String txId, JsonNode resp) {
        Transaction tx = repo.findById(txId).orElseThrow();
        tx.setStatus("SUCCESS");
        if (resp.has("transactionalID")) {
            tx.setTransactionalId(resp.get("transactionalID").asText());
        }
        tx.setTransactionMessage(resp.toString());
        return repo.save(tx);
    }

    public Transaction markFailed(String txId, String error) {
        Transaction tx = repo.findById(txId).orElseThrow();
        tx.setStatus("FAILED");
        tx.setTransactionMessage(error);
        return repo.save(tx);
    }

    public Page<Transaction> search(Optional<String> cellPhoneOpt,
                                    Optional<LocalDate> fromOpt,
                                    Optional<LocalDate> toOpt,
                                    Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("active"), true));
        cellPhoneOpt.ifPresent(phone -> predicates.add(cb.equal(root.get("cellPhone"), phone)));

        ZoneId zone = ZoneId.of("America/Bogota");

        fromOpt.ifPresent(from -> {
            LocalDateTime start = from.atStartOfDay(zone).toLocalDateTime();
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start));
        });
        toOpt.ifPresent(to -> {
            LocalDateTime end = to.atTime(LocalTime.MAX).atZone(zone).toLocalDateTime();
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end));
        });

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<Transaction> query = em.createQuery(cq);
        int total = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Transaction> results = query.getResultList();

        return new PageImpl<>(results, pageable, total);
    }

    public Transaction softDelete(String id, String deletedBy) {
        Transaction tx = repo.findById(id).orElseThrow();
        tx.setActive(false);
        tx.setTransactionMessage((tx.getTransactionMessage() == null ? "" : tx.getTransactionMessage()) + " [deleted_by=" + deletedBy + "]");
        return repo.save(tx);
    }
}
