package edu.tsp.asr.asrimbra;

import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {
    public static User JSONToUser(JSONObject userData) throws JSONException {
        User user = new User();
        user.setMail((String) userData.get("mail"));
        user.setRole(Role.valueOf(userData.get("role").toString()) );
        return user;
    }
}
