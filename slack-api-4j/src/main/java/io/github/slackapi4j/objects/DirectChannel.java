package io.github.slackapi4j.objects;

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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import io.github.slackapi4j.internal.SlackUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Deprecated
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class DirectChannel extends TimeStampedBaseObject {
  private ObjectID userId;
  private boolean isUserDeleted;

  // Optional params
  private boolean isOpen;
  private long lastRead;
  private int unreadCount;
  private Message latest;

  @Override
  protected void load(final JsonObject object, final JsonDeserializationContext context) {
    super.load(object, context);

    userId = new ObjectID(object.get("user").getAsString());
    isUserDeleted = SlackUtil.getAsBoolean(object.get("is_user_deleted"), false);

    isOpen = SlackUtil.getAsBoolean(object.get("is_open"), true);
    if (object.has("last_read")) {
      lastRead = SlackUtil.getAsTimestamp(object.get("last_read"));
      unreadCount = SlackUtil.getAsInt(object.get("unread_count"));
      latest = context.deserialize(object.get("latest"), Message.class);
    }
  }
}
