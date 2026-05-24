package senac.tsi.books.config;

import org.springframework.stereotype.Component;
import senac.tsi.books.entities.ApiKeyRole;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ApiKeyStore {

    private final ConcurrentMap<String, ApiKeyData> keys = new ConcurrentHashMap<>();

    public ApiKeyStore() {
        save("professor-admin", "admin-test-key", ApiKeyRole.ADMIN);
        save("aluno-user", "user-test-key", ApiKeyRole.USER);
    }

    public ApiKeyData generate(String username, ApiKeyRole role) {
        return save(username, UUID.randomUUID().toString(), role);
    }

    public Optional<ApiKeyData> findActive(String keyValue) {
        ApiKeyData apiKey = keys.get(keyValue);
        if (apiKey == null || !apiKey.active()) {
            return Optional.empty();
        }
        return Optional.of(apiKey);
    }

    public Optional<ApiKeyData> revoke(String keyValue) {
        ApiKeyData apiKey = keys.get(keyValue);
        if (apiKey == null) {
            return Optional.empty();
        }

        ApiKeyData revoked = new ApiKeyData(
                apiKey.username(),
                apiKey.keyValue(),
                apiKey.role(),
                false,
                apiKey.createdAt()
        );
        keys.put(keyValue, revoked);
        return Optional.of(revoked);
    }

    private ApiKeyData save(String username, String keyValue, ApiKeyRole role) {
        ApiKeyData data = new ApiKeyData(username, keyValue, role, true, LocalDateTime.now());
        keys.put(keyValue, data);
        return data;
    }

    public record ApiKeyData(
            String username,
            String keyValue,
            ApiKeyRole role,
            boolean active,
            LocalDateTime createdAt
    ) {
    }
}
