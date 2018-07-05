package org.do_the_math_baby.hike;

import org.json.JSONException;
import org.json.JSONObject;

public class Hiker {
    public String name;
    public String id;
    public double lat, lng;

    Hiker() {
        this.name = "";
        this.id = null;
        this.lat = 16;
        this.lng = 18;
    }

    Hiker(JSONObject hiker) throws JSONException {
        this.name = hiker.getString("name");
        this.id = hiker.getString("id");
        this.lat = hiker.getDouble("lat");
        this.lng = hiker.getDouble("lng");
    }

    JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        if (this.id != null)
            json.put("id", this.id);
        json.put("lat", this.lat);
        json.put("lng", this.lng);
        return json;
    }

}
