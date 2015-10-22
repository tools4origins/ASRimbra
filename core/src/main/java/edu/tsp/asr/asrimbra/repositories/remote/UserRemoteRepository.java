package edu.tsp.asr.asrimbra.repositories.remote;

import com.goebl.david.Request.Method;
import edu.tsp.asr.asrimbra.helpers.RemoteHelper;
import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
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
        RemoteHelper.getRemoteString("add", Method.POST, params);
        // @todo : test if no exceptions
    }


    @Override
    public void removeByMail(String mail) throws StorageException {
        Map<String, Object> params = new HashMap<>();
        RemoteHelper.getRemoteObject(baseURI+"removeByMail/" + mail, Method.DELETE, params);
        // @todo : test if no exceptions
    }

    @Override
    public List<User> getAll() throws StorageException {
        List<User> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        JSONArray array = RemoteHelper.getRemoteArray(baseURI+"getAll", Method.GET, params);
        User user;
        for (int i = 0; i < array.length(); ++i) {

            try {
                JSONObject userData = array.getJSONObject(i);
                user = RemoteHelper.JSONToUser(userData);
                users.add(user);
            } catch (JSONException e) {
                System.err.println("Unable to parse JSON");
                e.printStackTrace();
                throw new StorageException();
            }

        }

        return users;
    }

    @Override
    public User getByMail(String mail) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("mail", mail);
        RemoteHelper.getRemoteObject(baseURI+"getByMail", Method.GET, params);
        return null;
    }

    @Override
    public Optional<Role> getRoleByCredentials(String login, String password) throws StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("login", login);
        params.put("password", password);
        JSONObject obj = RemoteHelper.getRemoteObject(baseURI+"getRoleByCredentials", GET, params);

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
        RemoteHelper.getRemoteString(baseURI+"setAdmin", Method.POST, params);
    }

    @Override
    public void setSimpleUser(String mail) throws UserNotFoundException, StorageException {
        Map<String, Object> params = new HashMap<>();
        params.put("mail", mail);
        RemoteHelper.getRemoteString(baseURI+"setSimpleUser", Method.POST, params);
    }

    public void empty() throws StorageException {
        Map<String, Object> params = new HashMap<>();
        RemoteHelper.getRemoteString(baseURI+"empty", Method.GET, params);
    }
}
