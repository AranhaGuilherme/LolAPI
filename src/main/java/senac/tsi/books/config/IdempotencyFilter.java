package senac.tsi.books.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Order(3)
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";

    private final IdempotencyStore store;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IdempotencyFilter(IdempotencyStore store) {
        this.store = store;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod()) || !isDomainPost(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String key = request.getHeader(IDEMPOTENCY_HEADER);
        if (key == null || key.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 400,
                    "erro", "Chave idempotente obrigatoria",
                    "mensagem", "Informe o header X-Idempotency-Key em operacoes POST."
            ));
            return;
        }

        IdempotencyStore.IdempotencyData cached = store.find(key).orElse(null);
        if (cached != null) {
            if (!cached.method().equalsIgnoreCase(request.getMethod())
                    || !cached.requestUri().equals(request.getRequestURI())) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", 409,
                        "erro", "Chave idempotente em uso",
                        "mensagem", "A chave X-Idempotency-Key ja foi usada em outra operacao."
                ));
                return;
            }

            response.setStatus(cached.statusCode());
            response.setHeader("Idempotency-Replayed", "true");
            if (cached.contentType() != null) {
                response.setContentType(cached.contentType());
            }
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(cached.responseBody() == null ? "" : cached.responseBody());
            return;
        }

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrappedResponse);

        String body = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (wrappedResponse.getStatus() < 500) {
            store.save(new IdempotencyStore.IdempotencyData(
                    key,
                    request.getMethod(),
                    request.getRequestURI(),
                    wrappedResponse.getStatus(),
                    wrappedResponse.getContentType(),
                    body
            ));
        }
        wrappedResponse.copyBodyToResponse();
    }

    private boolean isDomainPost(String path) {
        return path.equals("/api/v1/champions")
                || path.equals("/api/v1/players")
                || path.equals("/api/v1/teams")
                || path.equals("/api/v1/coaches")
                || path.equals("/api/v1/matchgames");
    }
}
