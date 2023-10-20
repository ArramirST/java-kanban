package com.yandex.app.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Endpoint;
import com.yandex.app.service.FileBackedTasksManager;
import com.yandex.app.service.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();
    public FileBackedTasksManager inMemoryTaskManager;

    public void startServer(FileBackedTasksManager inMemoryTaskManager) throws IOException {
        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.start();
        this.inMemoryTaskManager = inMemoryTaskManager;
    }

    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            String uriQuery = uri.getQuery();
            Endpoint endpoint = getEndpoint(uri, exchange.getRequestMethod());
            InputStream inputStream;
            Task task;
            Epic epic;
            switch (endpoint) {
                case GET_PRIORITIZED:
                    String prioritizedTasks = gson.toJson(inMemoryTaskManager.getPrioritizedTasks());
                    writeResponse(exchange, prioritizedTasks, 200);
                    break;
                case GET_HISTORY:
                    String history = gson.toJson(inMemoryTaskManager.getHistory());
                    writeResponse(exchange, history, 200);
                    break;
                case GET_EPIC_SUBTASKS:
                    try {
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        String epicSubtasks = gson.toJson(inMemoryTaskManager.epics.get(id).getSubtasks());
                        writeResponse(exchange, epicSubtasks, 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан, или указан не верный id задачи",
                                404);
                    }
                    break;
                case GET_TASK:
                    try {
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        String taskString = gson.toJson(inMemoryTaskManager.tasks.get(id));
                        writeResponse(exchange, taskString, 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан, или указан не верный id задачи",
                                404);
                    }
                    break;
                case GET_EPIC:
                    try {
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        String epicString = gson.toJson(inMemoryTaskManager.epics.get(id));
                        writeResponse(exchange, epicString, 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан, или указан не верный id задачи",
                                404);
                    }
                    break;
                case GET_SUBTASK:
                    try {
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        String subtask = gson.toJson(inMemoryTaskManager.subtasks.get(id));
                        writeResponse(exchange, subtask, 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан, или указан не верный id задачи",
                                404);
                    }
                    break;
                case GET_TASKS:
                    String tasks = gson.toJson(inMemoryTaskManager.getTaskList());
                    writeResponse(exchange, tasks, 200);
                    break;
                case GET_EPICS:
                    String epics = gson.toJson(inMemoryTaskManager.getEpicList());
                    writeResponse(exchange, epics, 200);
                    break;
                case GET_SUBTASKS:
                    String subtasks = gson.toJson(inMemoryTaskManager.getSubtaskList());
                    writeResponse(exchange, subtasks, 200);
                    break;
                case GET_TASK_KEY:
                    inputStream = exchange.getRequestBody();
                    task = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Task.class);
                    if (task.getName() == null) {
                        writeResponse(exchange, "Не объявлно название задачи", 400);
                        break;
                    }
                    writeResponse(exchange, String.valueOf(inMemoryTaskManager.getTaskKey(task.getName())),
                            200);
                    break;
                case POST_TASK:
                    inputStream = exchange.getRequestBody();
                    task = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Task.class);
                    if (task.getName() == null) {
                        writeResponse(exchange, "Не объявлно название задачи", 400);
                        break;
                    }
                    inMemoryTaskManager.addTask(task);
                    writeResponse(exchange, "Задача успешно добавлена", 200);
                    break;
                case POST_EPIC:
                    inputStream = exchange.getRequestBody();
                    epic = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Epic.class);
                    if (epic.getName() == null) {
                        writeResponse(exchange, "Не объявлно название задачи", 400);
                        break;
                    }
                    inMemoryTaskManager.addEpic(epic);
                    writeResponse(exchange, "Эпик успешно добавлен", 200);
                    break;
                case POST_SUBTASK:
                    try {
                        inputStream = exchange.getRequestBody();
                        Subtask subtask = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET),
                                Subtask.class);
                        if (subtask.getName() == null) {
                            writeResponse(exchange, "Не объявлно название задачи", 400);
                            break;
                        }
                        inMemoryTaskManager.addSubtask(subtask,
                                inMemoryTaskManager.epics.get(subtask.getAttachment()).getName());
                        writeResponse(exchange, "Задача успешно добавлена", 200);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Невозможно создать подзадачу без эпика", 404);
                    }
                    break;
                case POST_UPDATE_TASK:
                    try {
                        InputStream inputTaskStream = exchange.getRequestBody();
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        Status status = Status.valueOf(new String(inputTaskStream.readAllBytes(), DEFAULT_CHARSET));
                        task = inMemoryTaskManager.tasks.get(id);
                        if (task == null) {
                            writeResponse(exchange, "Указан не верный id задачи", 400);
                            break;
                        }
                        inMemoryTaskManager.updateTask(task, status);
                        writeResponse(exchange, "Задача успешно обновлена", 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан id задачи", 404);
                    }
                    break;
                case POST_UPDATE_EPIC:
                    try {
                        InputStream inputEpicStream = exchange.getRequestBody();
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        Status status = Status.valueOf(new String(inputEpicStream.readAllBytes(), DEFAULT_CHARSET));
                        epic = inMemoryTaskManager.epics.get(id);
                        if (epic == null) {
                            writeResponse(exchange, "Указан не верный id задачи", 400);
                            break;
                        }
                        inMemoryTaskManager.updateEpic(epic, status);
                        writeResponse(exchange, "Эпик успешно обновлен", 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан id задачи", 404);
                    }
                    break;
                case POST_UPDATE_SUBTASK:
                    try {
                        InputStream inputSubtaskStream = exchange.getRequestBody();
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        String statusS = new String(inputSubtaskStream.readAllBytes(), DEFAULT_CHARSET);
                        Status status = Status.valueOf(statusS);
                        Subtask subtask = inMemoryTaskManager.subtasks.get(id);
                        if (subtask == null) {
                            writeResponse(exchange, "Указан не верный id задачи", 400);
                            break;
                        }
                        inMemoryTaskManager.updateTask(subtask, status);
                        writeResponse(exchange, "Подзадача успешно обновлена", 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан id задачи", 404);
                    }
                    break;
                case POST_TASK_DATE:
                    try {
                        inputStream = exchange.getRequestBody();
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        Task taskObject = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET),
                                Task.class);
                        LocalDateTime localDateTime = taskObject.getStartTime();
                        Integer duration = taskObject.getDuration();
                        task = inMemoryTaskManager.tasks.get(id);
                        if (task == null) {
                            writeResponse(exchange, "Указан не верный id задачи", 400);
                            break;
                        } else if (localDateTime == null || duration == null) {
                            writeResponse(exchange, "В теле сообщения нет времени начала/длительности",
                                    400);
                            break;
                        }
                        inMemoryTaskManager.setTaskDate(id, localDateTime, duration);
                        writeResponse(exchange, "Время и длительность добавлены", 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан id задачи", 404);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Время выполнения задач совпадает", 400);
                    }
                    break;
                case POST_SUBTASK_DATE:
                    try {
                        inputStream = exchange.getRequestBody();
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        Subtask taskObject = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET),
                                Subtask.class);
                        LocalDateTime localDateTime = taskObject.getStartTime();
                        Integer duration = taskObject.getDuration();
                        Subtask subtask = inMemoryTaskManager.subtasks.get(id);
                        if (subtask == null) {
                            writeResponse(exchange, "Указан не верный id задачи", 400);
                            break;
                        } else if (localDateTime == null || duration == null) {
                            writeResponse(exchange, "В теле сообщения нет времени начала/длительности",
                                    400);
                            break;
                        }
                        inMemoryTaskManager.setSubtaskDate(id, localDateTime, duration);
                        writeResponse(exchange, "Время и длительность добавлены", 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан id задачи", 404);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Время выполнения задач совпадает", 400);
                    }
                    break;
                case DELETE_TASK:
                    try {
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        inMemoryTaskManager.removeTask(id);
                        writeResponse(exchange, "Задача " + id + " успешно удалена", 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан id задачи",
                                404);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Указан не верный id задачи",
                                404);
                    }
                    break;
                case DELETE_EPIC:
                    try {
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        inMemoryTaskManager.removeEpic(id);
                        writeResponse(exchange, "Эпик " + id + " успешно удалена", 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан id задачи",
                                404);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Указан не верный id задачи",
                                404);
                    }
                    break;
                case DELETE_SUBTASK:
                    try {
                        int id = Integer.parseInt(uriQuery.split("=")[1]);
                        inMemoryTaskManager.removeSubtask(id);
                        writeResponse(exchange, "Подзадача " + id + " успешно удалена", 200);
                    } catch (NullPointerException e) {
                        writeResponse(exchange, "Не указан id задачи",
                                404);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Указан не верный id задачи",
                                404);
                    }
                    break;
                case DELETE_TASKS:
                    inMemoryTaskManager.removeTasks();
                    writeResponse(exchange, "Задачи удалены", 200);
                    break;
                case DELETE_EPICS:
                    inMemoryTaskManager.removeEpics();
                    writeResponse(exchange, "Эпики удалены", 200);
                    break;
                case DELETE_SUBTASKS:
                    inMemoryTaskManager.removeSubtasks();
                    writeResponse(exchange, "Подзадачи удалены", 200);
                    break;
                case UNKNOWN:
                    writeResponse(exchange, "UNKNOWN", 404);
                    break;
                default:
                    writeResponse(exchange, "Ошибка распознавания команды", 400);
                    break;
            }
        }
    }

    private static Endpoint getEndpoint(URI requestURI, String requestMethod) {
        String[] pathParts = requestURI.getPath().split("/", -1);
        String uriQuery = requestURI.getQuery();

        switch (requestMethod) {
            case "GET":
                if (pathParts[2].equals("key")) {
                    return Endpoint.GET_TASK_KEY;
                } else if (pathParts[2].equals("")) {
                    return Endpoint.GET_PRIORITIZED;
                } else if (pathParts[2].equals("history")) {
                    return Endpoint.GET_HISTORY;
                } else if (pathParts[2].equals("subtask") && pathParts[3].equals("epic")) {
                    if (uriQuery != null && uriQuery.split("=")[0].equals("id")) {
                        return Endpoint.GET_EPIC_SUBTASKS;
                    }
                    return Endpoint.UNKNOWN;
                } else if (uriQuery != null && uriQuery.split("=")[0].equals("id")) {
                    switch (pathParts[2]) {
                        case "task":
                            return Endpoint.GET_TASK;
                        case "epic":
                            return Endpoint.GET_EPIC;
                        case "subtask":
                            return Endpoint.GET_SUBTASK;
                    }
                } else {
                    switch (pathParts[2]) {
                        case "task":
                            return Endpoint.GET_TASKS;
                        case "epic":
                            return Endpoint.GET_EPICS;
                        case "subtask":
                            return Endpoint.GET_SUBTASKS;
                    }
                }
            case "POST":
                if (pathParts[pathParts.length - 1].equals("update")) {
                    if (uriQuery != null && uriQuery.split("=")[0].equals("id")) {
                        switch (pathParts[2]) {
                            case "task":
                                return Endpoint.POST_UPDATE_TASK;
                            case "epic":
                                return Endpoint.POST_UPDATE_EPIC;
                            case "subtask":
                                return Endpoint.POST_UPDATE_SUBTASK;
                        }
                    }
                } else if (pathParts[pathParts.length - 1].equals("date")) {
                    if (uriQuery != null && uriQuery.split("=")[0].equals("id")) {
                        switch (pathParts[2]) {
                            case "task":
                                return Endpoint.POST_TASK_DATE;
                            case "subtask":
                                return Endpoint.POST_SUBTASK_DATE;
                        }
                    }
                }
                switch (pathParts[2]) {
                    case "task":
                        return Endpoint.POST_TASK;
                    case "epic":
                        return Endpoint.POST_EPIC;
                    case "subtask":
                        return Endpoint.POST_SUBTASK;
                }
            case "DELETE":
                if (uriQuery != null && uriQuery.split("=")[0].equals("id")) {
                    switch (pathParts[2]) {
                        case "task":
                            return Endpoint.DELETE_TASK;
                        case "epic":
                            return Endpoint.DELETE_EPIC;
                        case "subtask":
                            return Endpoint.DELETE_SUBTASK;
                    }
                } else {
                    switch (pathParts[2]) {
                        case "task":
                            return Endpoint.DELETE_TASKS;
                        case "epic":
                            return Endpoint.DELETE_EPICS;
                        case "subtask":
                            return Endpoint.DELETE_SUBTASKS;
                    }
                }
        }
        return Endpoint.UNKNOWN;
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

}
