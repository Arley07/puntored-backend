package com.example.puntored.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "supplier_id", nullable = false)
    private String supplierId;

    @Column(name = "cell_phone", nullable = false)
    private String cellPhone;

    @Column(nullable = false)
    private Double value; // cambiado a Double

    @Column(length = 20)
    private String status;

    @Column(name = "transactional_id", length = 100)
    private String transactionalId;

    @Column(name = "transaction_message", columnDefinition = "TEXT")
    private String transactionMessage;

    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "created_by", length = 36)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "active")
    private Boolean active = true;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(ZoneId.of("America/Bogota"));
        }
        if (this.active == null) this.active = true;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getCellPhone() { return cellPhone; }
    public void setCellPhone(String cellPhone) { this.cellPhone = cellPhone; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionalId() { return transactionalId; }
    public void setTransactionalId(String transactionalId) { this.transactionalId = transactionalId; }

    public String getTransactionMessage() { return transactionMessage; }
    public void setTransactionMessage(String transactionMessage) { this.transactionMessage = transactionMessage; }

    public String getRequestPayload() { return requestPayload; }
    public void setRequestPayload(String requestPayload) { this.requestPayload = requestPayload; }

    public String getRawResponse() { return rawResponse; }
    public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
