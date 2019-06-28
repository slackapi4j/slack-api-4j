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

import io.github.slackapi4j.internal.Utilities;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
@Deprecated
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper=true)
public class NormalChannel extends Conversation
{
    // Member only values
    private long lastRead;
    private int unreadCount;
    private int unreadCountDisplay;

    private Message latest;

    @Override
    protected void load( JsonObject root, JsonDeserializationContext context )
    {
        super.load(root, context);

        if (super.isMember() && root.has("last_read"))
        {
            this.lastRead = Utilities.getAsTimestamp(root.get("last_read"));
            this.latest = context.deserialize(root.get("latest"), Message.class);
            this.unreadCount = root.get("unread_count").getAsInt();
            this.unreadCountDisplay = root.get("unread_count_display").getAsInt();
        }
    }
}
