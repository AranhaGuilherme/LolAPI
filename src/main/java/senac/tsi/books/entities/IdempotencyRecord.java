package senac.tsi.books.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;

@Entity
public class IdempotencyRecord {

    @Id
    @Column(length = 120)
    private String idempotencyKey;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String requestUri;

    @Column(nullable = false)
    private int statusCode;

    private String contentType;

    @Lob
    private String responseBody;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public IdempotencyRecord() {}

    public IdempotencyRecord(String idempotencyKey, String method, String requestUri, int statusCode, String contentType, String responseBody) {
        this.idempotencyKey = idempotencyKey;
        this.method = method;
        this.requestUri = requestUri;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public String getIdempotencyKey() { return idempotencyKey; }
    public String getMethod() { return method; }
    public String getRequestUri() { return requestUri; }
    public int getStatusCode() { return statusCode; }
    public String getContentType() { return contentType; }
    public String getResponseBody() { return responseBody; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public void setMethod(String method) { this.method = method; }
    public void setRequestUri(String requestUri) { this.requestUri = requestUri; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
