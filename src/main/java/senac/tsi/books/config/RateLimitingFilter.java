package senac.tsi.books.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(2)
public class RateLimitingFilter extends OncePerRequestFilter {

    public static final int REQUESTS_PER_MINUTE = 20;
    private static final long WINDOW_MILLIS = 60_000L;

    private final Map<String, ClientWindow> windows = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || path.equals("/error")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientKey = obterCliente(request);
        long now = System.currentTimeMillis();
        ClientWindow window = windows.compute(clientKey, (key, current) -> {
            if (current == null || now >= current.resetAtMillis) {
                return new ClientWindow(1, now + WINDOW_MILLIS);
            }
            current.requests++;
            return current;
        });

        int remaining = Math.max(0, REQUESTS_PER_MINUTE - window.requests);
        long retryAfterSeconds = Math.max(1, (window.resetAtMillis - now + 999) / 1000);

        response.setHeader("X-RateLimit-Limit", String.valueOf(REQUESTS_PER_MINUTE));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));

        if (window.requests > REQUESTS_PER_MINUTE) {
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 429,
                    "erro", "Too Many Requests",
                    "mensagem", "Limite de 20 requisicoes por minuto excedido. Tente novamente apos o tempo indicado em Retry-After."
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String obterCliente(HttpServletRequest request) {
        Object apiKeyValue = request.getAttribute(ApiKeyFilter.API_KEY_VALUE_ATTRIBUTE);
        if (apiKeyValue != null) {
            return apiKeyValue.toString();
        }
        return request.getRemoteAddr();
    }

    private static class ClientWindow {
        private int requests;
        private final long resetAtMillis;

        private ClientWindow(int requests, long resetAtMillis) {
            this.requests = requests;
            this.resetAtMillis = resetAtMillis;
        }
    }
}
