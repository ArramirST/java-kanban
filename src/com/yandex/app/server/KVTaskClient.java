package com.yandex.app.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class KVTaskClient {

    protected static String API_TOKEN;
    HttpClient client;
    String urlString;

    public KVTaskClient(String urlString) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        this.urlString = urlString;
        URI uri = null;
        try {
            uri = URI.create(urlString + "/register");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        API_TOKEN = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = null;
        try {
            uri = URI.create(urlString + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public JsonElement load(String key) throws IOException, InterruptedException {
        URI uri = null;
        try {
            uri = URI.create(urlString + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body());
    }

    public List<JsonElement> load() throws IOException, InterruptedException {
        URI uri = null;
        try {
            uri = URI.create(urlString + "/load/?API_TOKEN=" + API_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<JsonElement> tasksList = new ArrayList<>();
        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            tasksList.add(jsonElement);
        }
        return tasksList;
    }

    public String loadHistory() throws IOException, InterruptedException {
        URI uri = null;
        try {
            uri = URI.create(urlString + "/load/0?API_TOKEN=" + API_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String delete() throws IOException, InterruptedException {
        URI uri = null;
        try {
            uri = URI.create(urlString + "/delete/?API_TOKEN=" + API_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
