package com.yandex.app.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private static final int PORT = 8078;
    protected static String API_TOKEN;
    HttpClient client;
    String urlString;

    public KVTaskClient(String urlString) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        this.urlString = urlString;
        URI uri = null;
        try {
            uri = URI.create(urlString + PORT + "/register");
        } catch (IllegalArgumentException e) {
            System.out.println("Не читается URI");
            System.out.println(urlString + PORT + "/register");
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
            uri = URI.create(urlString + PORT + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        } catch (IllegalArgumentException e) {
            System.out.println("Не читается URI");
            System.out.println(urlString + PORT + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
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
            uri = URI.create(urlString + PORT + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        } catch (IllegalArgumentException e) {
            System.out.println("Не читается URI");
            System.out.println(urlString + PORT + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body());
    }

    public String loadHistory() throws IOException, InterruptedException {
        URI uri = null;
        try {
            uri = URI.create(urlString + PORT + "/load/0?API_TOKEN=" + API_TOKEN);
        } catch (IllegalArgumentException e) {
            System.out.println("Не читается URI");
            System.out.println(urlString + PORT + "/load/0?API_TOKEN=" + API_TOKEN);
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
            uri = URI.create(urlString + PORT + "/delete/?API_TOKEN=" + API_TOKEN);
        } catch (IllegalArgumentException e) {
            System.out.println("Не читается URI");
            System.out.println(urlString + PORT + "/delete/?API_TOKEN=" + API_TOKEN);
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
