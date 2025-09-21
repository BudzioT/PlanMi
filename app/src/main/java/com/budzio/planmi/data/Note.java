package com.budzio.planmi.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Note {
    private String text;
    private List<String> imageUris;

    public Note() {
        this.text = "";
        this.imageUris = new ArrayList<>();
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public List<String> getImageUris() {
        return imageUris;
    }
    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("text", text);

        JSONArray images = new JSONArray();
        for (String uri : imageUris) {
            images.put(uri);
        }
        json.put("imagesUris", images);
        return json;
    }

    public static Note fromJSON(JSONObject json) throws JSONException {
        Note note = new Note();
        note.text = json.optString("text", "");
        JSONArray images = json.optJSONArray("imagesUris");

        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                note.imageUris.add(images.getString(i));
            }
        }
        return note;
    }
}
