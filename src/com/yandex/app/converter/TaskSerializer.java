package com.yandex.app.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.yandex.app.model.Task;

import java.lang.reflect.Type;

public class TaskSerializer implements JsonSerializer<Task> {

    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("description", task.getDescription());
        jsonObject.addProperty("identifier", String.valueOf(task.getIdentifier()));
        jsonObject.addProperty("status", task.getStatus().toString());
        jsonObject.addProperty("type", task.getType());
        if (task.getStartTime() != null) {
            jsonObject.addProperty("startTime", task.getStartTime().toString());
        }
        if (task.getDuration() != null) {
            jsonObject.addProperty("duration", task.getDuration().toString());
        }
        return jsonObject;
    }
}