package com.willowwanderer.villagetopia.entity.villager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class TraitDatabase {

    private static final List<TraitDefinition> traits = new ArrayList<>();

    public static void load() {

        Gson gson = new Gson();

        InputStream stream = TraitDatabase.class.getResourceAsStream(
                                "/data/villagetopia/traits/traits.json"
                            );

        if (stream == null) {
            throw new RuntimeException("Missing traits.json");
        }

        JsonObject root = gson.fromJson(
                new InputStreamReader(stream),
                JsonObject.class
        );

        JsonObject traitObject = root.getAsJsonObject("traits");

        for (String key : traitObject.keySet()) {

            double weight = traitObject
                    .get(key)
                    .getAsDouble();

            traits.add(new TraitDefinition(key, weight));
        }
    }


    public static List<TraitDefinition> getTraits() {
        if (traits.size() == 0){
            load();
        }

        return traits;
    }
}