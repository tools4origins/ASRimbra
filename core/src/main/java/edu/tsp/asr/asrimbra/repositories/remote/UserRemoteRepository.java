package edu.tsp.asr.asrimbra.repositories.remote;

import com.goebl.david.Request;
import com.goebl.david.Request.Method;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import com.google.gson.JsonObject;
import edu.tsp.asr.asrimbra.JSONHelper;
import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.MethodNotAllowedException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.exceptions.UserNotFoundException;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.goebl.david.Request.Method.GET;

/* Repository used to remotely access to a database.
 * All necessary routes must be installed on the server which address is specified by baseURI
 * For instance GET {baseURI}/getRoleByCredentials will be used by getRoleByCredentials
 */
public class UserRemoteRepository implements UserRepository {
    private String baseURI;
    public UserRemoteRepository(String baseURI) {
        this.baseURI = baseURI + "/user/";
    }

    @Override
    public void add(User user) throws StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("mail", user.getMail());
        params.put("passwordHash", user.getPasswordHash());
        params.put("role", user.getRole());
        getRemoteString("add", Method.POST, params);
        // @todo : test if no exceptions
    }


    @Override
    public void removeByMail(String mail) throws StorageException {
        Map<String, Object> params = new HashMap<>();
        getRemoteObject("removeByMail/" + mail, Method.DELETE, params);
        // @todo : test if no exceptions
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
                user = JSONHelper.JSONToUser(userData);
                users.add(user);
            } catch (JSONException e) {
                System.out.println("Unable to parse JSON");
                e.printStackTrace();
                throw new StorageException();
            }

        }
        System.out.println(users.size());

        return users;
    }

    @Override
    public User getByMail(String mail) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("mail", mail);
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

    @Override
    public void setAdmin(String mail) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("mail", mail);
        getRemoteString("setAdmin", Method.POST, params);
    }

    @Override
    public void setSimpleUser(String mail) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("mail", mail);
        getRemoteString("setSimpleUser", Method.POST, params);
    }

    public void empty() throws StorageException {
        Map<String, Object> params = new HashMap<>();
        getRemoteString("empty", Method.GET, params);
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
            System.out.println(method + " not allowed on " + baseURI+URI);
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
            return handleWebbException(e, URI, method);
        }
    }

    private JSONObject getRemoteObject(String URI, Method method, Map<String, Object> params) throws StorageException {
        try {
            Request request = generateRequest(URI, method, params);
            return request.asJsonObject().getBody();
        } catch (WebbException e) {
            handleWebbException(e, URI, method);
            return null;
        }
    }

    private String getRemoteString(String URI, Method method, Map<String, Object> params) throws StorageException {
        try {
            Request request = generateRequest(URI, method, params);
            return request.asString().getBody();
        } catch (WebbException e) {
            handleWebbException(e, URI, method);
        }
        return null;
    }

    private JSONArray handleWebbException(WebbException e, String URI, Method method) throws StorageException {
        System.out.println(method + " on " + baseURI + URI + " did not worked");
        e.printStackTrace();
        throw new StorageException();
    }
}
