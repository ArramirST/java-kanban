package com.yandex.app.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.yandex.app.model.Subtask;

import java.lang.reflect.Type;

public class SubtaskSerializer implements JsonSerializer<Subtask> {

    @Override
    public JsonElement serialize(Subtask subtask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", subtask.getName());
        jsonObject.addProperty("epicId", subtask.getAttachment());
        jsonObject.addProperty("description", subtask.getDescription());
        jsonObject.addProperty("identifier", String.valueOf(subtask.getIdentifier()));
        jsonObject.addProperty("status", subtask.getStatus().toString());
        jsonObject.addProperty("type", subtask.getType());
        if (subtask.getStartTime() != null) {
            jsonObject.addProperty("startTime", subtask.getStartTime().toString());
        }
        if (subtask.getDuration() != null) {
            jsonObject.addProperty("duration", subtask.getDuration().toString());
        }
        return jsonObject;
    }
}