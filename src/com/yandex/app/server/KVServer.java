package com.yandex.app.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
        server.createContext("/delete", this::delete);
        startNewLog();
    }

    private void load(HttpExchange h) throws IOException {
        try {
            logWriting("/load");
            if (!hasAuth(h)) {
                logWriting("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    JsonArray taskList = new JsonArray();
                    for (String key1 : data.keySet()) {
                        if (!key1.equals("0")) {
                            JsonObject taskObject = JsonParser.parseString(data.get(key1)).getAsJsonObject();
                            taskList.add(taskObject);
                        }
                    }
                    logWriting(taskList.toString());
                    writeResponse(h, taskList.toString(), 200);
                    logWriting("Все значения успешно возвращены");
                } else {
                    writeResponse(h, data.get(key), 200);
                    logWriting("Значение для ключа " + key + " успешно возвращено!");
                }
            } else {
                logWriting("/load ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private static void writeResponse(HttpExchange exchange,
                                      String responseString,
                                      int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private void save(HttpExchange h) throws IOException {
        try {
            logWriting("/save");
            if (!hasAuth(h)) {
                logWriting("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    logWriting("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    logWriting("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                logWriting("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                logWriting("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void delete(HttpExchange h) throws IOException {
        try {
            logWriting("/delete");
            if (!hasAuth(h)) {
                logWriting("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("DELETE".equals(h.getRequestMethod())) {
                data.clear();
                logWriting("История удалена!");
                h.sendResponseHeaders(200, 0);
            } else {
                logWriting("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            logWriting("/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                logWriting("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        logWriting("Запускаем сервер на порту " + PORT);
        logWriting("Открой в браузере http://localhost:" + PORT + "/");
        logWriting("API_TOKEN: " + apiToken);
        server.start();
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public void stop() {
        server.stop(0);
        logWriting("Сервер остановлен");
    }


    private void startNewLog() {
        try {
            Files.deleteIfExists(Paths.get("log.txt"));
            Files.createFile(Paths.get("log.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logWriting(String message) {
        try (Writer fileWriter = new FileWriter("log.txt")) {
            fileWriter.write(message + "\n");
        } catch (IOException e) {
            throw new RuntimeException();
        }

    }
}