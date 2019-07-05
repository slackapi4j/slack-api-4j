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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.github.slackapi4j.SlackApi;
import io.github.slackapi4j.exceptions.SlackAuthException;
import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.exceptions.SlackMessageInvalidException;
import io.github.slackapi4j.exceptions.SlackRequestLimitException;
import io.github.slackapi4j.exceptions.SlackRestrictedException;
import org.eclipse.jetty.http.HttpStatus;

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

@SuppressWarnings("WeakerAccess")
public class SlackConnection {
  private final String token;
  private boolean isRateLimited;
  private long retryEnd;

  /**
   * Create a Connection Object.
   *
   * @param token the String token from the Slack developer workspace
   */
  public SlackConnection(final String token) {
    this.token = token;

    isRateLimited = false;
    retryEnd = 0;
  }

  private String encodeRequest(final Map<String, Object> params) {
    try {
      final StringBuilder data = new StringBuilder();
      data.append("token=");
      data.append(URLEncoder.encode(token, "UTF-8"));
      for (final Entry<String, Object> param : params.entrySet()) {
        data.append('&');
        data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
        data.append('=');
        data.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
      }

      return data.toString();
    } catch (final UnsupportedEncodingException e) {
      // Should never happen
      throw new AssertionError(e);
    }
  }

  private HttpsURLConnection createConnection(final SlackConstants method, final JsonObject base)
      throws IOException {
    final URL queryUrl = new URL("https", SlackConstants.HOST.toString(), "/api/" + method);
    final HttpsURLConnection connection = (HttpsURLConnection) queryUrl.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
    connection.setRequestProperty("Authorization", "Bearer " + token);
    connection.setDoInput(true);
    connection.setDoOutput(true);
    final BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
    writer.write(base.toString());
    writer.close();
    return connection;
  }

  private HttpsURLConnection createConnection(final SlackConstants method,
                                              final Map<String, Object> params) throws IOException {
    try {
      final URL queryUrl = new URL("https", SlackConstants.HOST.toString(),
          "/api/" + method);
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
      final String request = encodeRequest(params);
      final BufferedWriter writer = new BufferedWriter(
          new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
      writer.write(request);
      writer.close();

      return connection;
    } catch (final ProtocolException e) {
      // Should not happen
      throw new AssertionError(e);
    }
  }

  /**
   * Place a call to a slack api endpoint.
   *
   * @param method the Method to call
   * @param object the object to send
   * @return a JsonElement
   * @throws IOException if the encoding fails
   */
  public JsonElement callMethod(final SlackConstants method, final JsonObject object)
      throws IOException {
    checkRateLimit();
    final HttpsURLConnection connection = createConnection(method, object);
    connection.connect();
    return processConnectionResult(connection);
  }

  /**
   * Place a call to a slack api endpoint.
   *
   * @param method the Method to call
   * @param params a map of value : objects to send
   * @return a JsonElement
   * @throws IOException if the encoding fails
   */
  public JsonElement callMethod(final SlackConstants method, final Map<String, Object> params)
      throws IOException {
    checkRateLimit();
    final HttpsURLConnection connection = createConnection(method, params);
    connection.connect();
    return processConnectionResult(connection);

  }

  private void checkRateLimit() throws SlackRequestLimitException {
    if (isRateLimited) {
      if (System.currentTimeMillis() < retryEnd) {
        throw new SlackRequestLimitException(retryEnd);
      }

      isRateLimited = false;
    }
  }

  private JsonElement processConnectionResult(final HttpsURLConnection connection)
      throws IOException {
    if (connection.getResponseCode() == HttpStatus.TOO_MANY_REQUESTS_429) {  //too many requests
      final int delay = connection.getHeaderFieldInt("Retry-After", 2);
      isRateLimited = true;
      retryEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
      throw new SlackRequestLimitException(retryEnd);
    }
    final JsonReader reader = new JsonReader(
        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
    final JsonParser parser = new JsonParser();
    final JsonElement result = parser.parse(reader);
    reader.close();
    return result;
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

  /**
   * This method add error handling to a method call as well as validation of the object for the
   * endpoint being targeted.  The other
   * {@link SlackConnection#callMethod(SlackConstants, JsonObject)} does not handle an error.
   *
   * @param method the endpoint method
   * @param object the  object to send
   * @return a JsonObject response
   * @throws IOException    if Json doesnt decode
   * @throws SlackException if the endpoint sends an error
   */
  public JsonObject callMethodHandled(final SlackConstants method, final JsonObject object)
      throws IOException, SlackException {
    MessageValidator.validateMessage(object, method);
    final JsonObject base = callMethod(method, object).getAsJsonObject();
    final boolean ok = base.get("ok").getAsBoolean();
    if (!ok) {
      final String code = base.get("error").getAsString();
      throw validateErrorCode(code);
    }
    return base;
  }

  /**
   * This method add error handling to a method call as well as validation of the object for the
   * endpoint being targeted.  The other
   * {@link SlackConnection#callMethod(SlackConstants, JsonObject)} does not handle an error.
   *
   * @param method the endpoint method
   * @param params a map of name: objects to send
   * @return a JsonObject response
   * @throws IOException    if Json doesnt decode
   * @throws SlackException if the endpoint sends an error
   */

  public JsonObject callMethodHandled(final SlackConstants method, final Map<String, Object> params)
      throws SlackException, IOException {
    final JsonObject base = callMethod(method, params).getAsJsonObject();
    final boolean ok = base.get("ok").getAsBoolean();

    if (!ok) {
      final String code = base.get("error").getAsString();
      throw validateErrorCode(code);
    } else if (SlackApi.isDebug() && base.has("warning")) {
        final String warning = base.get("warning").getAsString();
      new SlackMessageInvalidException("warning", "DEBUG ENABLED : " + method
          + " Response contained a warning :" + warning).printStackTrace();
      return base;
    }
    return base;
  }

  /**
   * This method add error handling to a method call for the endpoint being targeted.
   *
   * @param method the endpoint method
   * @return a JsonObject response
   * @throws IOException    if Json doesnt decode
   * @throws SlackException if the endpoint sends an error
   */

  public JsonObject callMethodHandled(final SlackConstants method)
      throws SlackException, IOException {
    return callMethodHandled(method, SlackUtil.EMPTY_MAP);
  }
}
