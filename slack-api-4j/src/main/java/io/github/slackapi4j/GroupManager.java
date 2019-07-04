package io.github.slackapi4j;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.internal.SlackConnection;
import io.github.slackapi4j.internal.SlackConstants;
import io.github.slackapi4j.objects.GroupChannel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This is used to manager group channels.
 *
 * @deprecated Use {@link io.github.slackapi4j.objects.Conversation}
 */

@Deprecated
public class GroupManager {
  private final Gson gson;
  private final SlackConnection connection;

  GroupManager(final SlackApi main) {
    gson = main.getGson();
    connection = main.getSlack();
  }

  public List<GroupChannel> getGroups() throws SlackException, IOException {
    return getGroups(true);
  }

  /**
   * Get the GroupChannels optional the archived ones.
   *
   * @param includeArchived true if you want archived
   * @return A list of channels
   * @throws SlackException for endpoint errors
   * @throws IOException    encoding errors
   */
  public List<GroupChannel> getGroups(final boolean includeArchived)
      throws SlackException, IOException {
    final JsonObject raw;
    if (includeArchived) {
      raw = connection.callMethodHandled(SlackConstants.GROUP_LIST);
    } else {
      final Map<String, Object> params = ImmutableMap.<String, Object>builder()
          .put("exclude_archived", 1)
          .build();
      raw = connection.callMethodHandled(SlackConstants.GROUP_LIST, params);
    }

    final JsonArray rawList = raw.getAsJsonArray("groups");
    final ImmutableList.Builder<GroupChannel> groups = ImmutableList.builder();

    for (final JsonElement rawGroup : rawList) {
      groups.add(gson.fromJson(rawGroup, GroupChannel.class));
    }

    return groups.build();
  }
}
