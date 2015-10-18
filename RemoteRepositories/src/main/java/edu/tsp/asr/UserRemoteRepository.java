package edu.tsp.asr;

import com.goebl.david.Request;
import com.goebl.david.Request.Method;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.MethodNotAllowedException;
import edu.tsp.asr.exceptions.StorageException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRemoteRepository implements UserRepository {
    private String baseURI;

    public UserRemoteRepository(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override
    public void addUser(User user) throws StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        JSONObject obj = getRemoteObject("addUser", Method.POST, params);
        System.out.println("---------- START ---------");
        System.out.println(obj);
        System.out.println("----------- END ----------");
    }

    @Override
    public void removeUser(User user) throws UserNotFoundException, StorageException {
        removeUserByMail(user.getMail());
    }

    @Override
    public void removeUserByMail(String mail) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_mail", mail);
        getRemoteObject("removeUserByMail", Method.DELETE, params);
    }

    @Override
    public List<User> getAllUsers() throws StorageException {
        List<User> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        JSONArray array = getRemoteArray("getAllUsers", Method.GET, params);
        System.out.println(array);
        System.out.println(array.length());
        User user;
        for (int i = 0; i < array.length(); ++i) {

            try {
                JSONObject userData = array.getJSONObject(i);
                user = new User();
                user.setId((Integer) userData.get("id"));
                user.setMail((String) userData.get("mail"));
                users.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        System.out.println(users.size());

        return users;
    }

    @Override
    public User getUserByMail(String mail) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_mail", mail);
        getRemoteObject("getUserByMail", Method.POST, params);
        return null;
    }

    @Override
    public User getUserByCredentials(String mail, String password) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_mail", mail);
        params.put("user_password", password);
        getRemoteObject("getUserByCredentials", Method.POST, params);
        return null;
    }

    private Request generateRequest(String URI, Method method, Map<String, Object> params) throws StorageException {
        Webb webb = Webb.create();
        Request request;
        try {
            switch (method) {
                case GET:
                    request = webb.get(baseURI+URI);
                    break;
                case POST:
                    request = webb.post(baseURI+URI);
                    break;
                case PUT:
                    request = webb.put(baseURI+URI);
                    break;
                case DELETE:
                    request = webb.delete(baseURI+URI);
                    break;
                default:
                    throw new MethodNotAllowedException();
            }
        } catch (MethodNotAllowedException e) {
            throw new StorageException();
        }

        for(Map.Entry<String, Object> param : params.entrySet()) {
            request.param(param.getKey(), param.getValue());
        }
        request.ensureSuccess();

        return request;
    }

    private JSONArray getRemoteArray(String URI, Method method, Map<String, Object> params) throws StorageException {
        try {
            Request request = generateRequest(URI, method, params);
            return request.asJsonArray().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            throw new StorageException();
        }
    }

    private JSONObject getRemoteObject(String URI, Method method, Map<String, Object> params) throws StorageException {
        try {
            Request request = generateRequest(URI, method, params);
            return request.asJsonObject().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            throw new StorageException();
        }
    }
}
