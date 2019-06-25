package au.com.addstar.slackapi.internal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import au.com.addstar.slackapi.exceptions.SlackAuthException;
import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.exceptions.SlackRequestLimitException;
import au.com.addstar.slackapi.exceptions.SlackRestrictedException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.eclipse.jetty.util.IO;

@SuppressWarnings("WeakerAccess")
public class SlackConnection
{
    private String token;
    private boolean isRateLimited;
    private long retryEnd;

    public SlackConnection(String token)
    {
        this.token = token;

        isRateLimited = false;
        retryEnd = 0;
    }

    private String encodeRequest(Map<String, Object> params)
    {
        try
        {
            StringBuilder data = new StringBuilder();
            data.append("token=");
            data.append(URLEncoder.encode(token, "UTF-8"));
            for (Entry<String, Object> param : params.entrySet())
            {
                data.append('&');
                data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                data.append('=');
                data.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            return data.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            // Should never happen
            throw new AssertionError();
        }
    }

    private HttpsURLConnection createGetConnection(String method) throws IOException {
        URL queryUrl = new URL("https", SlackConstants.HOST, "/api/" + method);
        HttpsURLConnection connection = (HttpsURLConnection)queryUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization","Bearer "+ token);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private HttpsURLConnection createConnection(String method, JsonObject base) throws IOException {
        URL queryUrl = new URL("https", SlackConstants.HOST, "/api/" + method);
        HttpsURLConnection connection = (HttpsURLConnection)queryUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Authorization","Bearer "+ token);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        writer.write(base.toString());
        writer.close();
        return connection;
    }

    private HttpsURLConnection createConnection(String method, Map<String, Object> params) throws IOException, MalformedURLException
    {
        try
        {
            URL queryUrl = new URL("https", SlackConstants.HOST, "/api/" + method);
            HttpsURLConnection connection = (HttpsURLConnection)queryUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Add request params
            String request = encodeRequest(params);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(request);
            writer.close();

            return connection;
        }
        catch ( ProtocolException e )
        {
            // Should not happen
            throw new AssertionError();
        }
    }
    public JsonObject call(String method) throws IOException {
        if (isRateLimited)
        {
            if (System.currentTimeMillis() < retryEnd)
                throw new SlackRequestLimitException(retryEnd);

            isRateLimited = false;
        }
        HttpsURLConnection connection = createGetConnection(method);
        connection.connect();
        if (connection.getResponseCode() == 429) // Too many requests
        {
            int delay = connection.getHeaderFieldInt("Retry-After", 2);
            isRateLimited = true;
            retryEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
            throw new SlackRequestLimitException(retryEnd);
        }
        JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

        JsonParser parser = new JsonParser();
        JsonElement result = parser.parse(reader);

        reader.close();
        return result.getAsJsonObject();
    }

    public JsonElement callMethod(String method, JsonObject object) throws IOException {
        if (isRateLimited)
        {
            if (System.currentTimeMillis() < retryEnd)
                throw new SlackRequestLimitException(retryEnd);

            isRateLimited = false;
        }
        HttpsURLConnection connection = createConnection(method, object);
        connection.connect();
        if (connection.getResponseCode() == 429) // Too many requests
        {
            int delay = connection.getHeaderFieldInt("Retry-After", 2);
            isRateLimited = true;
            retryEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
            throw new SlackRequestLimitException(retryEnd);
        }
        JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

        JsonParser parser = new JsonParser();
        JsonElement result = parser.parse(reader);

        reader.close();

        return result;
    }
    public JsonObject callMethodHandled(String method,JsonObject object) throws IOException, SlackException {
        JsonObject base = callMethod(method,object).getAsJsonObject();
        boolean ok = base.get("ok").getAsBoolean();
        if (!ok)
        {
            String code = base.get("error").getAsString();
            switch (code)
            {
                case "not_authed":
                case "invalid_auth":
                case "account_inactive":
                    throw new SlackAuthException(code);

                case "restricted_action":
                case "user_is_bot":
                case "user_is_restricted":
                    throw new SlackRestrictedException(code);

                default:
                    throw new SlackException(code);
            }
        }
        else
            return base;

    }

    public JsonElement callMethod(String method, Map<String, Object> params) throws IOException, SlackRequestLimitException
    {
        if (isRateLimited)
        {
            if (System.currentTimeMillis() < retryEnd)
                throw new SlackRequestLimitException(retryEnd);

            isRateLimited = false;
        }

        HttpsURLConnection connection = createConnection(method, params);
        connection.connect();

        if (connection.getResponseCode() == 429) // Too many requests
        {
            int delay = connection.getHeaderFieldInt("Retry-After", 2);
            isRateLimited = true;
            retryEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
            throw new SlackRequestLimitException(retryEnd);
        }

        JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

        JsonParser parser = new JsonParser();
        JsonElement result = parser.parse(reader);

        reader.close();

        return result;
    }

    public JsonObject callMethodHandled(String method, Map<String, Object> params) throws SlackException, IOException
    {
        JsonObject base = callMethod(method, params).getAsJsonObject();
        boolean ok = base.get("ok").getAsBoolean();

        if (!ok)
        {
            String code = base.get("error").getAsString();
            switch (code)
            {
            case "not_authed":
            case "invalid_auth":
            case "account_inactive":
                throw new SlackAuthException(code);

            case "restricted_action":
            case "user_is_bot":
            case "user_is_restricted":
                throw new SlackRestrictedException(code);

            default:
                throw new SlackException(code);
            }
        }
        else
            return base;
    }

    public JsonObject callMethodHandled(String method) throws SlackException, IOException
    {
        return callMethodHandled(method, Utilities.EMPTY_MAP);
    }
}
