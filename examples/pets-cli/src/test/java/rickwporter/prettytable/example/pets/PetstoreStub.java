package rickwporter.prettytable.example.pets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

final class PetstoreStub {
    static final String FIDO = "{\"id\":1,\"name\":\"Fido\",\"tag\":\"trained\",\"owner\":\"Rick\"}";
    static final String WHISKERS = "{\"id\":2,\"name\":\"Whiskers\",\"tag\":\"fluffy\",\"owner\":\"Sam\"}";
    private static final String PETS = "[" + FIDO + "," + WHISKERS + "]";

    private final HttpServer server;
    private volatile String lastMethod;
    private volatile String lastPath;
    private volatile String lastQuery;
    private volatile String lastApiKey;
    private volatile String lastBody = "";
    private final List<String> queries = new ArrayList<String>();

    private PetstoreStub(HttpServer httpServer) {
        this.server = httpServer;
    }

    static PetstoreStub start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        PetstoreStub stub = new PetstoreStub(httpServer);
        httpServer.createContext("/", stub::handle);
        httpServer.start();
        return stub;
    }

    void close() {
        this.server.stop(0);
    }

    String url() {
        return "http://127.0.0.1:" + this.server.getAddress().getPort();
    }

    String lastMethod() {
        return this.lastMethod;
    }

    String lastPath() {
        return this.lastPath;
    }

    String lastQuery() {
        return this.lastQuery;
    }

    List<String> queries() {
        return this.queries;
    }

    String lastApiKey() {
        if (this.lastApiKey == null) {
            return null;
        }
        return this.lastApiKey.replaceFirst("Bearer ", "");
    }

    String lastBody() {
        return this.lastBody;
    }

    private void handle(HttpExchange exchange) throws IOException {
        byte[] request;
        try (InputStream in = exchange.getRequestBody()) {
            request = in.readAllBytes();
        }
        this.lastMethod = exchange.getRequestMethod();
        this.lastPath = exchange.getRequestURI().getPath();
        this.lastQuery = exchange.getRequestURI().getRawQuery();
        this.queries.add(this.lastQuery);
        this.lastApiKey = exchange.getRequestHeaders().getFirst("Authorization");
        this.lastBody = new String(request, StandardCharsets.UTF_8);

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getRawQuery();
        if ("GET".equals(method) && "/pets".equals(path)) {
            if (query != null && query.contains("page=2")) {
                respond(exchange, 200, "[" + WHISKERS + "]", null);
                return;
            }
            if (query != null && query.contains("limit=1")) {
                respond(exchange, 200, "[" + FIDO + "]", "/pets?page=2");
                return;
            }
            respond(exchange, 200, PETS, null);
            return;
        }
        if ("GET".equals(method) && "/pets/1".equals(path)) {
            respond(exchange, 200, FIDO, null);
            return;
        }
        if ("GET".equals(method) && "/pets/5".equals(path)) {
            respond(exchange, 400, "nope", null);
            return;
        }
        if ("GET".equals(method) && "/pets/404".equals(path)) {
            respond(exchange, 404, "{\"code\":404,\"message\":\"Pet not found\"}", null);
            return;
        }
        if ("POST".equals(method) && "/pets".equals(path)) {
            respond(exchange, 201, "", null);
            return;
        }
        if ("DELETE".equals(method) && "/pets/1".equals(path)) {
            respond(exchange, 204, "", null);
            return;
        }
        respond(exchange, 404, "{\"message\":\"no such route\"}", null);
    }

    private static void respond(HttpExchange exchange, int status, String body, String next) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        if (next != null) {
            exchange.getResponseHeaders().set("x-next", next);
        }
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
