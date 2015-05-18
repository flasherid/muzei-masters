package com.example.textrecoapp.persistence;

import java.lang.reflect.Type;

import com.example.textrecoapp.gamification.AchievementCriteria;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class CriteriaSerializerDeserializer implements JsonSerializer<AchievementCriteria>,
    JsonDeserializer<AchievementCriteria> {

  private static final String INTERFACE_IMPL_PACKAGE = "com.example.textrecoapp.achievements.";

  @Override
  public JsonElement serialize(AchievementCriteria src, Type typeOf, JsonSerializationContext context) {
    JsonObject result = new JsonObject();
    result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
    result.add("properties", context.serialize(src, src.getClass()));
    return result;
  }

  @Override
  public AchievementCriteria deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String type = jsonObject.get("type").getAsString();
    JsonElement element = jsonObject.get("properties");
    try {
      return context.deserialize(element, Class.forName(INTERFACE_IMPL_PACKAGE + type));
    } catch (ClassNotFoundException cnfe) {
      throw new JsonParseException("Unknown element type: " + type, cnfe);
    }
  }

}
