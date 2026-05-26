package senac.tsi.books.config;

public final class RequestIdUtils {

    private RequestIdUtils() {}

    public static boolean semIdValido(Long id) {
        return id == null || id <= 0;
    }
}
