package com.yandex.app.converter;

import com.google.gson.*;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.Status;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class SubtaskDeserializer implements JsonDeserializer<Subtask> {

    @Override
    public Subtask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        Subtask subtask = new Subtask(name, description);
        String type = jsonObject.get("type").getAsString();
        int epicId = jsonObject.get("epicId").getAsInt();
        int id = jsonObject.get("identifier").getAsInt();
        Status status = Status.valueOf(jsonObject.get("status").getAsString());
        subtask.setIdentifier(id);
        subtask.setStatus(status);
        subtask.setType(type);
        subtask.setAttachment(epicId);
        if (!(jsonObject.get("startTime") == null)) {
            LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
            subtask.setStartTime(startTime);
        }
        if (!(jsonObject.get("duration") == null)) {
            int duration = (int) jsonObject.get("duration").getAsLong();
            subtask.setDuration(duration);
        }
        return subtask;
    }
}