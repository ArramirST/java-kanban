package com.yandex.app.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.converter.SubtaskDeserializer;
import com.yandex.app.converter.SubtaskSerializer;
import com.yandex.app.converter.TaskDeserializer;
import com.yandex.app.converter.TaskSerializer;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Endpoint;
import com.yandex.app.service.HttpTaskManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static Scanner scanner = new Scanner(System.in);
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .serializeNulls()
            .create();
    private static HttpTaskManager inMemoryTaskManager = Managers.getDefault();

    public void startServer() throws IOException {
        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.start();
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
            Subtask subtask;
            Status status;
            int id;
            String ID_ERROR = "Не указан, или указан не верный id задачи";
            String WRONG_TASK_FORMAT = "Не указан, или указан не верный id задачи";
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
                    if (uriQuery.split("=").length != 2) {
                        writeResponse(exchange, ID_ERROR, 404);
                        break;
                    }
                    id = Integer.parseInt(uriQuery.split("=")[1]);
                    String epicSubtasks = gson.toJson(inMemoryTaskManager.epics.get(id).getSubtasks());
                    writeResponse(exchange, epicSubtasks, 200);
                    break;
                case GET_TASK:
                    if (uriQuery.split("=").length != 2) {
                        writeResponse(exchange, ID_ERROR, 404);
                        break;
                    }
                    id = Integer.parseInt(uriQuery.split("=")[1]);
                    String taskString = gson.toJson(inMemoryTaskManager.tasks.get(id));
                    writeResponse(exchange, taskString, 200);
                case GET_EPIC:
                    if (uriQuery.split("=").length != 2) {
                        writeResponse(exchange, ID_ERROR, 404);
                        break;
                    }
                    id = Integer.parseInt(uriQuery.split("=")[1]);
                    String epicString = gson.toJson(inMemoryTaskManager.epics.get(id));
                    writeResponse(exchange, epicString, 200);
                    break;
                case GET_SUBTASK:
                    if (uriQuery.split("=").length != 2) {
                        writeResponse(exchange, ID_ERROR, 404);
                        break;
                    }
                    id = Integer.parseInt(uriQuery.split("=")[1]);
                    String subtaskString = gson.toJson(inMemoryTaskManager.subtasks.get(id));
                    writeResponse(exchange, subtaskString, 200);
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
                        writeResponse(exchange, WRONG_TASK_FORMAT, 400);
                        break;
                    }
                    writeResponse(exchange, String.valueOf(inMemoryTaskManager.getTaskKey(task.getName())),
                            200);
                    break;
                case POST_TASK:
                    inputStream = exchange.getRequestBody();
                    task = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Task.class);
                    if (task.getName() == null) {
                        writeResponse(exchange, WRONG_TASK_FORMAT, 400);
                        break;
                    }
                    inMemoryTaskManager.addTask(task);
                    writeResponse(exchange, "Задача успешно добавлена", 200);
                    break;
                case POST_EPIC:
                    inputStream = exchange.getRequestBody();
                    epic = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Epic.class);
                    if (epic.getName() == null) {
                        writeResponse(exchange, WRONG_TASK_FORMAT, 400);
                        break;
                    }
                    inMemoryTaskManager.addEpic(epic);
                    writeResponse(exchange, "Эпик успешно добавлен", 200);
                    break;
                case POST_SUBTASK:
                    try {
                        inputStream = exchange.getRequestBody();
                        subtask = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET),
                                Subtask.class);
                        if (subtask.getName() == null) {
                            writeResponse(exchange, WRONG_TASK_FORMAT, 400);
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
                    if (uriQuery.split("=").length != 2) {
                        writeResponse(exchange, ID_ERROR, 404);
                        break;
                    }
                    InputStream inputTaskStream = exchange.getRequestBody();
                    id = Integer.parseInt(uriQuery.split("=")[1]);
                    status = Status.valueOf(new String(inputTaskStream.readAllBytes(), DEFAULT_CHARSET));
                    task = inMemoryTaskManager.tasks.get(id);
                    if (task == null) {
                        writeResponse(exchange, "Указан не верный id задачи", 400);
                        break;
                    }
                    inMemoryTaskManager.updateTask(task, status);
                    writeResponse(exchange, "Задача успешно обновлена", 200);
                    break;
                case POST_UPDATE_EPIC:
                    if (uriQuery.split("=").length != 2) {
                        writeResponse(exchange, ID_ERROR, 404);
                        break;
                    }
                    InputStream inputEpicStream = exchange.getRequestBody();
                    id = Integer.parseInt(uriQuery.split("=")[1]);
                    status = Status.valueOf(new String(inputEpicStream.readAllBytes(), DEFAULT_CHARSET));
                    epic = inMemoryTaskManager.epics.get(id);
                    if (epic == null) {
                        writeResponse(exchange, "Указан не верный id задачи", 400);
                        break;
                    }
                    inMemoryTaskManager.updateEpic(epic, status);
                    writeResponse(exchange, "Эпик успешно обновлен", 200);
                    break;
                case POST_UPDATE_SUBTASK:
                    if (uriQuery.split("=").length != 2) {
                        writeResponse(exchange, ID_ERROR, 404);
                        break;
                    }
                    InputStream inputSubtaskStream = exchange.getRequestBody();
                    id = Integer.parseInt(uriQuery.split("=")[1]);
                    String statusS = new String(inputSubtaskStream.readAllBytes(), DEFAULT_CHARSET);
                    status = Status.valueOf(statusS);
                    subtask = inMemoryTaskManager.subtasks.get(id);
                    if (subtask == null) {
                        writeResponse(exchange, ID_ERROR, 400);
                        break;
                    }
                    inMemoryTaskManager.updateTask(subtask, status);
                    writeResponse(exchange, "Подзадача успешно обновлена", 200);
                    break;
                case POST_TASK_DATE:
                    try {
                        if (uriQuery.split("=").length != 2) {
                            writeResponse(exchange, ID_ERROR, 404);
                            break;
                        }
                        inputStream = exchange.getRequestBody();
                        id = Integer.parseInt(uriQuery.split("=")[1]);
                        Task taskObject = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET),
                                Task.class);
                        LocalDateTime localDateTime = taskObject.getStartTime();
                        Integer duration = taskObject.getDuration();
                        task = inMemoryTaskManager.tasks.get(id);
                        if (task == null) {
                            writeResponse(exchange, ID_ERROR, 400);
                            break;
                        } else if (localDateTime == null || duration == null) {
                            writeResponse(exchange, "В теле сообщения нет времени начала/длительности",
                                    400);
                            break;
                        }
                        inMemoryTaskManager.setTaskDate(id, localDateTime, duration);
                        writeResponse(exchange, "Время и длительность добавлены", 200);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Время выполнения задач совпадает", 400);
                    }
                    break;
                case POST_SUBTASK_DATE:
                    try {
                        if (uriQuery.split("=").length != 2) {
                            writeResponse(exchange, ID_ERROR, 404);
                            break;
                        }
                        inputStream = exchange.getRequestBody();
                        id = Integer.parseInt(uriQuery.split("=")[1]);
                        Subtask taskObject = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET),
                                Subtask.class);
                        LocalDateTime localDateTime = taskObject.getStartTime();
                        Integer duration = taskObject.getDuration();
                        subtask = inMemoryTaskManager.subtasks.get(id);
                        if (subtask == null) {
                            writeResponse(exchange, ID_ERROR, 400);
                            break;
                        } else if (localDateTime == null || duration == null) {
                            writeResponse(exchange, "В теле сообщения нет времени начала/длительности",
                                    400);
                            break;
                        }
                        inMemoryTaskManager.setSubtaskDate(id, localDateTime, duration);
                        writeResponse(exchange, "Время и длительность добавлены", 200);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Время выполнения задач совпадает", 400);
                    }
                    break;
                case DELETE_TASK:
                    try {
                        if (uriQuery.split("=").length != 2) {
                            writeResponse(exchange, ID_ERROR, 404);
                            break;
                        }
                        id = Integer.parseInt(uriQuery.split("=")[1]);
                        inMemoryTaskManager.removeTask(id);
                        writeResponse(exchange, "Задача " + id + " успешно удалена", 200);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Указан не верный id задачи",
                                404);
                    }
                    break;
                case DELETE_EPIC:
                    try {
                        if (uriQuery.split("=").length != 2) {
                            writeResponse(exchange, ID_ERROR, 404);
                            break;
                        }
                        id = Integer.parseInt(uriQuery.split("=")[1]);
                        inMemoryTaskManager.removeEpic(id);
                        writeResponse(exchange, "Эпик " + id + " успешно удалена", 200);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, "Указан не верный id задачи",
                                404);
                    }
                    break;
                case DELETE_SUBTASK:
                    try {
                        if (uriQuery.split("=").length != 2) {
                            writeResponse(exchange, ID_ERROR, 404);
                            break;
                        }
                        id = Integer.parseInt(uriQuery.split("=")[1]);
                        inMemoryTaskManager.removeSubtask(id);
                        writeResponse(exchange, "Подзадача " + id + " успешно удалена", 200);
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

    public static HttpTaskManager bindTaskManager() {
        return inMemoryTaskManager;
    }

    public static Epic epic = new Epic("Учеба", "Нужно учиться");
    public static Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
    public static Task task = new Task("Домашние дела", "Помыть посуду");

    public void manualTest() {
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        int choice;
        while (true) {
            printMenu();
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    getAllTasks();
                    break;
                case 2:
                    removeAllTasks();
                    break;
                case 3:
                    getTasksById();
                    break;
                case 4:
                    createTask();
                    break;
                case 5:
                    updateTasks();
                    break;
                case 6:
                    removeTaskById();
                    break;
                case 7:
                    subtasksCheck();
                    break;
                case 8:
                    historyTest();
                    break;
                case 9:
                    loadTest();
                    break;
                case 10:
                    timeTest();
                    break;
                case 11:
                    sortedListTest();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Такой комманды нет");
            }
            if (choice == 0) {
                break;
            }
        }
    }

    public static void printMenu() {
        System.out.println("Введите комманду:");
        System.out.println("1 - Получение списка всех задач");
        System.out.println("2 - Удаление всех задач");
        System.out.println("3 - Получение по идентификатору");
        System.out.println("4 - Создание задачи");
        System.out.println("5 - Обновление задачи");
        System.out.println("6 - Удаление по идентификатору");
        System.out.println("7 - Получение всех списков задач отдельного Epic");
        System.out.println("8 - Проверить работу истории доступа");
        System.out.println("9 - Проверить возможность загрузки из сохранения");
        System.out.println("10 - Проверить возможность установки дат");
        System.out.println("11 - Получить сортированный список задач");
        System.out.println("0 - Завершение работы");
    }

    public static void getAllTasks() {
        System.out.println(inMemoryTaskManager.getTaskList());
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
    }

    public static void removeAllTasks() {
        inMemoryTaskManager.removeTasks();
        inMemoryTaskManager.removeEpics();
        inMemoryTaskManager.removeSubtasks();
    }

    public static void getTasksById() {
        System.out.println(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")));
        System.out.println(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")));
        System.out.println(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3")));
        System.out.println(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 4")));
        System.out.println(inMemoryTaskManager.getHistory());
    }

    public static void createTask() {
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.addTask(task);
    }

    public static void updateTasks() {
        inMemoryTaskManager.updateEpic(epic, Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task, Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subTask, Status.IN_PROGRESS);
    }

    public static void removeTaskById() {
        inMemoryTaskManager.addTask(new Task("Домашние дела", "Помыть посуду"));
        inMemoryTaskManager.addEpic(new Epic("Учеба", "Нужно учиться"));
        inMemoryTaskManager.addSubtask(new Subtask("Спринт 3", "Выполнить до вечера"),
                "Учеба");
        inMemoryTaskManager.removeSubtask(inMemoryTaskManager.getTaskKey("Спринт 3"));
        inMemoryTaskManager.removeEpic(inMemoryTaskManager.getTaskKey("Учеба"));
        inMemoryTaskManager.removeTask(inMemoryTaskManager.getTaskKey("Домашние дела"));
    }

    public static void subtasksCheck() {
        System.out.println(inMemoryTaskManager.getEpicSubtasksList("Учеба"));
    }

    public static void historyTest() {
        System.out.println("История до добавления задач: " + inMemoryTaskManager.getHistory());
        Task task1 = new Task("Прогуляться", "Выйти на улицу");
        Task task2 = new Task("Позвонить родителям", "Пообщаться");
        Epic epic1 = new Epic("Сходить в магазин, за свежими продуктами", "Купить продукты");
        Epic epic2 = new Epic("Приготовить ужин", "Нужны продукты");
        Subtask subTask1 = new Subtask("Купить мясо", "В мясном отделе");
        Subtask subTask2 = new Subtask("Купить молоко", "В молочном отделе");
        Subtask subTask3 = new Subtask("Купить макарон", "В отделе с крупами");
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);
        inMemoryTaskManager.addSubtask(subTask1, "Сходить в магазин, за свежими продуктами");
        inMemoryTaskManager.addSubtask(subTask2, "Сходить в магазин, за свежими продуктами");
        inMemoryTaskManager.addSubtask(subTask3, "Сходить в магазин, за свежими продуктами");
        inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Сходить в магазин, за свежими продуктами"));
        inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Прогуляться"));
        inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Позвонить родителям"));
        inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Купить мясо"));
        inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Купить молоко"));
        inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Сходить в магазин"));
        inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Приготовить ужин"));
        System.out.println("История после добавления задач: " + inMemoryTaskManager.getHistory());
        inMemoryTaskManager.removeSubtask(inMemoryTaskManager.getTaskKey("Купить мясо"));
        System.out.println("История после удаления подзадачи: " + inMemoryTaskManager.getHistory());
        inMemoryTaskManager.removeEpic(inMemoryTaskManager.getTaskKey("Сходить в магазин, за свежими продуктами"));
        System.out.println("История после удаления эпика: " + inMemoryTaskManager.getHistory());
    }

    public static void timeTest() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.setTaskDate(inMemoryTaskManager.getTaskKey("Домашние дела"),
                LocalDateTime.now(), 10);
        System.out.println(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")));
    }

    public static void sortedListTest() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.setTaskDate(inMemoryTaskManager.getTaskKey("Домашние дела"),
                LocalDateTime.now(), 10);
        Epic epic1 = new Epic("Сходить в магазин, за свежими продуктами", "Купить продукты");
        Subtask subTask1 = new Subtask("Купить мясо", "В мясном отделе");
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addSubtask(subTask1, "Сходить в магазин, за свежими продуктами");
        inMemoryTaskManager.setSubtaskDate(inMemoryTaskManager.getTaskKey("Купить мясо"),
                LocalDateTime.now().plusHours(1), 10);
        System.out.println(inMemoryTaskManager.getPrioritizedTasks());
    }

    public static void loadTest() {
        System.out.println("История первого файла: " + inMemoryTaskManager.getHistory());
        HttpTaskManager loadedTaskManager = Managers.getDefault();
        System.out.println("История нового файла: " + loadedTaskManager.getHistory());
    }
}
