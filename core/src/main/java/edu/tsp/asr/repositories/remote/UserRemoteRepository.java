package edu.tsp.asr.repositories.remote;

import com.goebl.david.Request;
import com.goebl.david.Request.Method;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import edu.tsp.asr.entities.Role;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.MethodNotAllowedException;
import edu.tsp.asr.exceptions.StorageException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.api.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.goebl.david.Request.Method.*;

/* Repository used to remotely access to a database.
 * All necessary routes must be installed on the server which address is specified by baseURI
 * For instance GET {baseURI}/getRoleByCredentials will be used by getRoleByCredentials
 */
public class UserRemoteRepository implements UserRepository {
    private String baseURI;
    // @todo : consider using a socket instead of a rest interface
    public UserRemoteRepository(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override
    public void add(User user) throws StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        JSONObject obj = getRemoteObject("add", Method.POST, params);
    }

    @Override
    public void remove(User user) throws UserNotFoundException, StorageException {
        removeByMail(user.getMail());
    }

    @Override
    public void removeByMail(String mail) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_mail", mail);
        getRemoteObject("removeByMail", Method.DELETE, params);
    }

    @Override
    public List<User> getAll() throws StorageException {
        List<User> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        JSONArray array = getRemoteArray("getAll", Method.GET, params);
        System.out.println(array);
        System.out.println(array.length());
        User user;
        for (int i = 0; i < array.length(); ++i) {

            try {
                JSONObject userData = array.getJSONObject(i);
                user = JSONToUser(userData);
                users.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        System.out.println(users.size());

        return users;
    }

    @Override
    public User getByMail(String mail) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_mail", mail);
        getRemoteObject("getByMail", Method.GET, params);
        return null;
    }

    @Override
    public Optional<Role> getRoleByCredentials(String login, String password) throws StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("login", login);
        params.put("password", password);
        JSONObject obj = getRemoteObject("getRoleByCredentials", GET, params);

        // obj is of the form JSONObject {} if user not found else JSONObject { "value" = "ADMIN" } for instance
        // JSONException is thrown when there is no value (user not found)
        // IllegalArgumentException is thrown when the value is not recognized
        try {
            String roleAsString = obj.get("value").toString();
            return Optional.of(Role.valueOf(roleAsString));
        } catch (JSONException | IllegalArgumentException e) {
            return Optional.empty();
        }
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
            throw new StorageException();
        }
    }

    private JSONObject getRemoteObject(String URI, Method method, Map<String, Object> params) throws StorageException {
        try {
            Request request = generateRequest(URI, method, params);
            return request.asJsonObject().getBody();
        } catch (WebbException e) {
            throw new StorageException();
        }
    }

    private User JSONToUser(JSONObject userData) throws JSONException {
        User user = new User();
        user.setMail((String) userData.get("mail"));
        return user;
    }
}
