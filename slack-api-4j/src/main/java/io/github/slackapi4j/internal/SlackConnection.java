package io.github.slackapi4j.internal;

/*-
 * #%L
 * slack-api-4j
 * %%
 * Copyright (C) 2018 - 2019 SlackApi4J
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import io.github.slackapi4j.SlackAPI;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.github.slackapi4j.exceptions.*;

@SuppressWarnings("WeakerAccess")
public class SlackConnection
{
    private final String token;
    private boolean isRateLimited;
    private long retryEnd;

    public SlackConnection(final String token)
    {
        this.token = token;

        this.isRateLimited = false;
        this.retryEnd = 0;
    }

    private String encodeRequest(final Map<String, Object> params)
    {
        try
        {
            final StringBuilder data = new StringBuilder();
            data.append("token=");
            data.append(URLEncoder.encode(this.token, "UTF-8"));
            for (final Entry<String, Object> param : params.entrySet())
            {
                data.append('&');
                data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                data.append('=');
                data.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            return data.toString();
        } catch (final UnsupportedEncodingException e)
        {
            // Should never happen
            throw new AssertionError();
        }
    }

    private HttpsURLConnection createConnection(final SlackConstants method, final JsonObject base) throws IOException {
        final URL queryUrl = new URL("https", SlackConstants.HOST.toString(), "/api/" + method);
        final HttpsURLConnection connection = (HttpsURLConnection) queryUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Authorization", "Bearer " + this.token);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
        writer.write(base.toString());
        writer.close();
        return connection;
    }

    private HttpsURLConnection createConnection(final SlackConstants method, final Map<String, Object> params) throws IOException {
        try
        {
            final URL queryUrl = new URL("https", SlackConstants.HOST.toString(), "/api/" + method);
            final HttpsURLConnection connection = (HttpsURLConnection) queryUrl.openConnection();
            if (method.isPost()) {
                connection.setRequestMethod("POST");
            } else {
                connection.setRequestMethod("GET");
            }
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Add request params
            final String request = this.encodeRequest(params);
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
            writer.write(request);
            writer.close();

            return connection;
        } catch (final ProtocolException e)
        {
            // Should not happen
            throw new AssertionError();
        }
    }

    public JsonElement callMethod(final SlackConstants method, final JsonObject object) throws IOException {
        if (this.isRateLimited) {
            if (System.currentTimeMillis() < this.retryEnd) {
                throw new SlackRequestLimitException(this.retryEnd);
            }

            this.isRateLimited = false;
        }
        final HttpsURLConnection connection = this.createConnection(method, object);
        connection.connect();
        return this.processConnectionResult(connection);
    }

    private JsonElement processConnectionResult(final HttpsURLConnection connection) throws IOException {
        if (connection.getResponseCode() == 429) // Too many requests
        {
            final int delay = connection.getHeaderFieldInt("Retry-After", 2);
            this.isRateLimited = true;
            this.retryEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
            throw new SlackRequestLimitException(this.retryEnd);
        }
        final JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        final JsonParser parser = new JsonParser();
        final JsonElement result = parser.parse(reader);
        reader.close();
        return result;
    }

    public JsonObject callMethodHandled(final SlackConstants method, final JsonObject object) throws IOException, SlackException {
        MessageValidator.validateMessage(object, method);
        final JsonObject base = this.callMethod(method, object).getAsJsonObject();
        final boolean ok = base.get("ok").getAsBoolean();
        if (!ok)
        {
            final String code = base.get("error").getAsString();
            throw this.validateErrorCode(code);
        }
        return base;
    }

    private SlackException validateErrorCode(final String code) {
        switch (code) {
            case "not_authed":
            case "invalid_auth":
            case "account_inactive":
                return new SlackAuthException(code);
            case "restricted_action":
            case "user_is_bot":
            case "user_is_restricted":
                return new SlackRestrictedException(code);
            default:
                return new SlackException(code);
        }
    }

    public JsonElement callMethod(final SlackConstants method, final Map<String, Object> params) throws IOException {
        if (this.isRateLimited) {
            if (System.currentTimeMillis() < this.retryEnd) {
                throw new SlackRequestLimitException(this.retryEnd);
            }

            this.isRateLimited = false;
        }

        final HttpsURLConnection connection = this.createConnection(method, params);
        connection.connect();
        return this.processConnectionResult(connection);

    }

    public JsonObject callMethodHandled(final SlackConstants method, final Map<String, Object> params) throws SlackException, IOException {
        final JsonObject base = this.callMethod(method, params).getAsJsonObject();
        final boolean ok = base.get("ok").getAsBoolean();

        if (!ok) {
            final String code = base.get("error").getAsString();
            throw this.validateErrorCode(code);
        } else if (SlackAPI.isDebug() && base.has("warning")) {
            try {
                final String warning = base.get("warning").getAsString();
                throw new SlackMesssageInvalidException("warning", "DEBUG ENABLED : " + method + " Response contained a warning :" + warning);
            } catch (final SlackException e) {
                e.printStackTrace();
            }
            return base;
        }
        return base;
    }

    public JsonObject callMethodHandled(final SlackConstants method) throws SlackException, IOException
    {
        return this.callMethodHandled(method, Utilities.EMPTY_MAP);
    }
}
