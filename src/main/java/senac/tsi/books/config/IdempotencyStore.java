package senac.tsi.books.config;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class IdempotencyStore {

    private final ConcurrentMap<String, IdempotencyData> records = new ConcurrentHashMap<>();

    public Optional<IdempotencyData> find(String key) {
        return Optional.ofNullable(records.get(key));
    }

    public void save(IdempotencyData data) {
        records.put(data.idempotencyKey(), data);
    }

    public record IdempotencyData(
            String idempotencyKey,
            String method,
            String requestUri,
            int statusCode,
            String contentType,
            String responseBody
    ) {
    }
}
