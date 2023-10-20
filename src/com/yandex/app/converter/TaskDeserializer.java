package com.yandex.app.converter;

import com.google.gson.*;
import com.yandex.app.model.Task;
import com.yandex.app.service.Status;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class TaskDeserializer implements JsonDeserializer<Task> {

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        Task task = new Task(name, description);
        String type = jsonObject.get("type").getAsString();
        int id = jsonObject.get("identifier").getAsInt();
        Status status = Status.valueOf(jsonObject.get("status").getAsString());
        //task.setName(name);
        //task.setDescription(description);
        task.setIdentifier(id);
        task.setStatus(status);
        task.setType(type);
        if (!(jsonObject.get("startTime") == null)) {
            LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
            task.setStartTime(startTime);
        }
        if (!(jsonObject.get("duration") == null)) {
            int duration = (int) jsonObject.get("duration").getAsLong();
            task.setDuration(duration);
        }
        return task;
    }
}