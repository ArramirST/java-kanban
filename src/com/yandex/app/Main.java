package com.yandex.app;

import com.yandex.app.server.HttpTaskServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        HttpTaskServer httpTaskServer = new HttpTaskServer();
        try {
            httpTaskServer.startServer();
            System.out.println("Сервер работает");
        } catch (IOException e) {
            throw new RuntimeException();
        }
        httpTaskServer.manualTest();
    }
}
