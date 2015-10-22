package edu.tsp.asr.asrimbra.helpers;

import com.goebl.david.Request;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.MethodNotAllowedException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class RemoteHelper {
    public static Request generateRequest(String URI, Request.Method method, Map<String, Object> params) throws StorageException {
        Webb webb = Webb.create();
        Request request;
        try {
            switch (method) {
                case GET:
                    request = webb.get(URI);
                    break;
                case POST:
                    request = webb.post(URI);
                    break;
                case PUT:
                    request = webb.put(URI);
                    break;
                case DELETE:
                    request = webb.delete(URI);
                    break;
                default:
                    throw new MethodNotAllowedException();
            }
        } catch (MethodNotAllowedException e) {
            System.err.println(method + " not allowed on " + URI);
            throw new StorageException();
        }

        for(Map.Entry<String, Object> param : params.entrySet()) {
            request.param(param.getKey(), param.getValue());
        }
        request.ensureSuccess();

        return request;
    }

    public static JSONArray getRemoteArray(String URI, Request.Method method, Map<String, Object> params) throws StorageException {
        try {
            Request request = generateRequest(URI, method, params);
            return request.asJsonArray().getBody();
        } catch (WebbException e) {
            return handleWebbException(e, URI, method);
        }
    }

    public static JSONObject getRemoteObject(String URI, Request.Method method, Map<String, Object> params) throws StorageException {
        try {
            Request request = generateRequest(URI, method, params);
            return request.asJsonObject().getBody();
        } catch (WebbException e) {
            handleWebbException(e, URI, method);
            return null;
        }
    }

    public static String getRemoteString(String URI, Request.Method method, Map<String, Object> params) throws StorageException {
        try {
            Request request = generateRequest(URI, method, params);
            return request.asString().getBody();
        } catch (WebbException e) {
            handleWebbException(e, URI, method);
        }
        return null;
    }

    public static JSONArray handleWebbException(WebbException e, String URI, Request.Method method) throws StorageException {
        System.err.println(method + " on " + URI + " did not worked");
        e.printStackTrace();
        throw new StorageException();
    }

    public static User JSONToUser(JSONObject userData) throws JSONException {
        User user = new User();
        user.setMail((String) userData.get("mail"));
        user.setRole(Role.valueOf(userData.get("role").toString()));
        return user;
    }
}
