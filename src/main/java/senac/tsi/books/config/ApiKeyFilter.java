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

@Component
@Order(1)
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-API-Key";
    public static final String API_KEY_VALUE_ATTRIBUTE = "apiKeyValue";

    private final ApiKeyStore apiKeyStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiKeyFilter(ApiKeyStore apiKeyStore) {
        this.apiKeyStore = apiKeyStore;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || path.equals("/")
                || path.equals("/error")
                || path.startsWith("/api-keys/generate")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKeyValue = request.getHeader(API_KEY_HEADER);

        if (apiKeyValue == null || apiKeyValue.isBlank()) {
            escreverErro(response, HttpServletResponse.SC_UNAUTHORIZED, "API Key obrigatoria", "Informe o header X-API-Key.");
            return;
        }

        ApiKeyStore.ApiKeyData apiKey = apiKeyStore.findActive(apiKeyValue)
                .orElse(null);

        if (apiKey == null) {
            escreverErro(response, HttpServletResponse.SC_UNAUTHORIZED, "API Key invalida", "A chave enviada nao existe ou esta inativa.");
            return;
        }

        request.setAttribute(API_KEY_VALUE_ATTRIBUTE, apiKey.keyValue());
        filterChain.doFilter(request, response);
    }

    private void escreverErro(HttpServletResponse response, int status, String erro, String mensagem) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status,
                "erro", erro,
                "mensagem", mensagem
        ));
    }
}
