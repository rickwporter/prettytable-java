package rickwporter.prettytable.example.pets;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Calls the Swagger Petstore described by pet.yaml.
 * The documented server is http://petstore.swagger.io/v1.
 */
public final class PetstoreClient {
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);
    private static final int HTTP_ERROR = 400;
    private static final String NEXT_HEADER = "x-next";

    private final URI base;
    private final String apiKey;
    private final HttpClient http;
    private final ObjectMapper json;
    private String nextLink;

    public PetstoreClient(String serverUrl, String key) {
        this.base = parseBase(serverUrl);
        this.apiKey = blankToNull(key);
        this.http = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.json = new ObjectMapper();
        this.json.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
    }

    public Object get(String path) throws IOException, PetstoreException {
        return this.exchange("GET", path, null);
    }

    public Object post(String path, Object body) throws IOException, PetstoreException {
        return this.exchange("POST", path, body);
    }

    public Object delete(String path) throws IOException, PetstoreException {
        return this.exchange("DELETE", path, null);
    }

    /** Link from the latest response {@code x-next} header, or null when the header is absent. */
    public String nextLink() {
        return this.nextLink;
    }

    private Object exchange(String method, String path, Object body) throws IOException, PetstoreException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(this.endpoint(path))
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/json")
                .header("User-Agent", "pets-cli");
        if (this.apiKey != null) {
            builder.header("Authorization", "Bearer " + this.apiKey);
        }
        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofByteArray(this.json.writeValueAsBytes(body)));
        }
        HttpResponse<byte[]> response;
        try {
            response = this.http.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", ex);
        }
        this.nextLink = response.headers().firstValue(NEXT_HEADER)
                .filter(value -> !value.isBlank())
                .orElse(null);
        Object parsed = this.parse(response.body());
        if (response.statusCode() >= HTTP_ERROR) {
            throw new PetstoreException(response.statusCode(), parsed);
        }
        return parsed;
    }

    private URI endpoint(String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return URI.create(path);
        }
        String suffix = path.startsWith("/") ? path : "/" + path;
        // URI.resolve("/pets") would drop a path prefix on the base URL, so append the path.
        return URI.create(this.base.toString() + suffix);
    }

    private Object parse(byte[] body) throws IOException {
        if (body == null || body.length == 0 || leadingSpace(body) == body.length) {
            return null;
        }
        try {
            return this.json.readValue(body, Object.class);
        } catch (JsonProcessingException ex) {
            return new String(body, StandardCharsets.UTF_8).trim();
        }
    }

    private static int leadingSpace(byte[] body) {
        int index = 0;
        while (index < body.length && body[index] <= ' ') {
            index++;
        }
        return index;
    }

    private static URI parseBase(String serverUrl) {
        if (serverUrl == null || serverUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Server URL is required.");
        }
        String trimmed = serverUrl.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        URI parsed;
        try {
            parsed = URI.create(trimmed);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid server URL: " + serverUrl, ex);
        }
        String scheme = parsed.getScheme() == null ? "" : parsed.getScheme().toLowerCase(Locale.ROOT);
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            throw new IllegalArgumentException("Server URL must be http or https: " + serverUrl);
        }
        return parsed;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
