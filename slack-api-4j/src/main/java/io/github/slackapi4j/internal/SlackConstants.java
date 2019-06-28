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

import java.io.Serializable;

public enum SlackConstants {
    HOST("api.slack.com"),
    API_TEST("api.test"),
    AUTH_TEST("auth.test"),
    CHANNEL_ARCHIVE("channels.archive"),
    CHANNEL_CREATE("channel.create"),
    CHANNEL_HISTORY("CHANNEL_HISTORY"),
    CHANNEL_INFO("channels.info"),
    CHANNEL_INVITE("channels.invite"),
    CHANNEL_JOIN("channels.join"),
    CHANNEL_KICK("channels.kick"),
    CHANNEL_LEAVE("channels.leave"),

    CHANNEL_LIST("channels.list", false),
    CHANNEL_MARK("channels.mark"),
    CHANNEL_RENAME("channels.rename"),
    CHANNEL_REPLIES("channel.replies"),
    CHANNEL_SET_PURPOSE("channels.setPurpose"),
    CHANNEL_SET_TOPIC("channels.setTopic"),
    CHANNEL_UNARCHIVE("channels.unarchive"),

    CONVERSATION_ARCHIVE("conversations.archive"),
    CONVERSATION_CLOSE("conversations.close"),
    CONVERSATION_OPEN("conversations.open"),
    CONVERSATION_HISTORY("conversations.history"),
    CONVERSATION_INFO("conversations.info"),
    CONVERSATION_INVITE("conversations.invite"),
    CONVERSATION_KICK("conversations.kick"),
    CONVERSATION_LEAVE("conversations.leave"),
    CONVERSATION_JOIN("conversations.join"),
    CONVERSATION_LIST("conversations.list", false),
    CONVERSATION_MARK("channels.mark"),
    CONVERSATION_RENAME("conversations.rename"),
    CONVERSATION_REPLIES("conversations.replies"),
    CONVERSATION_SET_PURPOSE("conversations.setPurpose"),
    CONVERSATION_SET_TOPIC("conversations.setTopic"),
    CONVERSATION_UNARCHIVE("conversations.unarchive"),
    CONVERSATION_MEMBERS("conversations.members", false),

    CHAT_DELETE("chat.delete"),
    CHAT_GETPERMALINK("chat.getPermalink"),
    CHAT_MEMESSAGE("chat.meMessage"),
    CHAT_POSTEMPHEMERAL("chat.postEphemeral"),
    CHAT_POST("chat.postMessage"),
    CHAT_UNFURL("chat.unfurl"),
    CHAT_UPDATE("chat.update"),

    EMOJI_LIST("emoji.list"),

    FILE_DELETE("files.delete"),
    FILE_INFO("files.info"),
    FILE_LIST("files.list"),
    FILE_UPLOAD("files.upload"),

    GROUP_ARCHIVE("groups.archive"),
    GROUP_CLOSE("groups.close"),
    GROUP_CREATE("groups.create"),
    GROUP_CREATE_CHILD("groups.createChild"),
    GROUP_HISTORY("groups.history"),
    GROUP_INVITE("groups.invite"),
    GROUP_KICK("groups.kick"),
    GROUP_LEAVE("groups.leave"),

    GROUP_LIST("groups.list"),
    GROUP_MARK("groups.mark"),
    GROUP_OPEN("groups.open"),
    GROUP_RENAME("groups.rename"),
    GROUP_SET_PURPOSE("groups.setPurpose"),
    GROUP_SET_TOPIC("groups.setTopic"),
    GROUP_UNARCHIVE("groups.unarchive"),

    IM_CLOSE("im.close"),
    IM_HISTORY("im.history"),

    IM_LIST("im.list"),
    IM_MARK("im.mark"),
    IM_OPEN("im.open"),

    REACTION_ADD("reaction.add"),
    REACTION_REMOVE("reaction.remove"),

    MPIM_OPEN("mpim.open"),
    MPIM_CLOSE("mpim.close"),
    MPIM_MARK("mpim.mark"),

    OAUTH_ACCESS("oauth.access"),

    RTM_START("rtm.start"),

    SEARCH_ALL("search.all"),
    SEARCH_FILES("search.files"),
    SEARCH_MESSAGES("search.messages"),

    STARS_LIST("stars.list"),

    USER_GET_PRESENCE("users.getPresence"),
    USER_INFO("users.info"),
    USER_LIST("users.list", false),
    USER_SET_ACTIVE("users.setActive"),
    USER_SET_PRESENCE("users.setPresence");

    private final String value;
    private boolean post = true;

    SlackConstants(final String value) {
        this.value = value;
    }

    SlackConstants(final String value, final boolean post) {
        this.value = value;
        this.post = post;
    }

    /**
     * @return true if this method prefers a post v a get
     */
    public boolean isPost() {
        return this.post;
    }

    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum type should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return this.value;
    }
}
