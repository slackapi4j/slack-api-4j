package au.com.addstar.slackapi;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import lombok.Getter;
import au.com.addstar.slackapi.Message.MessageType;
import au.com.addstar.slackapi.events.MessageEvent;
import au.com.addstar.slackapi.events.RealTimeEvent;
import au.com.addstar.slackapi.exceptions.SlackRTException;
import au.com.addstar.slackapi.internal.Utilities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@SuppressWarnings("WeakerAccess")
public class RealTimeSession implements Closeable
{
    private final Gson gson;
    private final List<RealTimeListener> listeners;
    private final Map<Integer, Message> pendingMessages;
    @Getter
    private User self;
    private Set<User> users;
    private Set<BaseChannel> channels;
    private Map<String, User> userMap;
    private Map<String, BaseChannel> channelMap;
    private Map<ObjectID, User> userIdMap;
    private Map<ObjectID, BaseChannel> channelIdMap;
    private WebSocketClient client;
    private Session session;
    private int nextMessageId = 1;
    private boolean needJoinConfirm;
    
    RealTimeSession(final JsonObject object, final SlackAPI main) throws IOException
    {
        this.gson = main.getGson();
        
        this.listeners = Lists.newArrayList();
        this.pendingMessages = Maps.newHashMap();
        
        this.load(object);
        
        this.initWebSocket(object.get("url").getAsString());
    }
    
    private void load(final JsonObject object)
    {
        final JsonObject self = object.getAsJsonObject("self");
        final JsonArray channels = object.getAsJsonArray("channels");
        final JsonArray groups = object.getAsJsonArray("groups");
        final JsonArray users = object.getAsJsonArray("users");
        final JsonArray ims = object.getAsJsonArray("ims");
        
        final ObjectID selfId = new ObjectID(self.get("id").getAsString());
        
        // Load users
        this.users = Sets.newHashSetWithExpectedSize(users.size());
        this.userMap = Maps.newHashMapWithExpectedSize(users.size());
        this.userIdMap = Maps.newHashMapWithExpectedSize(users.size());
        for (final JsonElement user : users)
        {
            try
            {
                final User loaded = this.gson.fromJson(user, User.class);
                if (loaded.getId().equals(selfId)) {
                    this.self = loaded;
                }
                
                this.addUser(loaded);
            }
            catch (final Throwable e)
            {
                System.err.println("Unable to load user " + user);
                e.printStackTrace();
            }
        }
        
        // Load channels
        this.channels = Sets.newHashSetWithExpectedSize(channels.size());
        this.channelMap = Maps.newHashMapWithExpectedSize(channels.size());
        this.channelIdMap = Maps.newHashMapWithExpectedSize(channels.size());
        for (final JsonElement channel : channels)
        {
            final NormalChannel loaded = this.gson.fromJson(channel, NormalChannel.class);
            this.addChannel(loaded);
        }
        
        // Load groups
        for (final JsonElement group : groups)
        {
            final GroupChannel loaded = this.gson.fromJson(group, GroupChannel.class);
            this.addChannel(loaded);
        }
        
        // Load DMs
        for (final JsonElement dm : ims)
        {
            final DirectChannel loaded = this.gson.fromJson(dm, DirectChannel.class);
            this.addChannel(loaded);
        }
    }
    
    private void initWebSocket(final String url) throws IOException
    {
        try
        {
            final URI uri = new URI(url);
            this.needJoinConfirm = true;
            this.client = new WebSocketClient(new SslContextFactory());
            this.client.start();
            final Future<Session> future = this.client.connect(new SocketClient(), uri);
            
            this.session = future.get(this.client.getConnectTimeout() + 1000, TimeUnit.MILLISECONDS);
            
            this.nextMessageId = 1;
        }
        catch ( final URISyntaxException e )
        {
            // Should never happen
        }
        catch (final IOException e)
        {
            throw e;
        }
        catch (final InterruptedException ignored)
        {
        
        }
        catch (final ExecutionException e)
        {
            if (e.getCause() instanceof IOException) {
                throw (IOException)e.getCause();
            } else {
                throw new IOException(e.getCause());
            }
        }
        catch (final TimeoutException e)
        {
            // Probably wont
            throw new SocketTimeoutException();
        }
        // Sigh, couldn't they pick a more specific one? :/
        catch ( final Exception e )
        {
            throw new IOException(e);
        }
    }
    
    private void addUser(final User user)
    {
        this.users.add(user);
        this.userMap.put(user.getName().toLowerCase(), user);
        this.userIdMap.put(user.getId(), user);
    }
    
    private void addChannel(final BaseChannel channel)
    {
        this.channels.add(channel);
        if (channel instanceof NormalChannel) {
            this.channelMap.put(((NormalChannel)channel).getName().toLowerCase(), channel);
        }
        this.channelIdMap.put(channel.getId(), channel);
    }
    
    public void addListener(final RealTimeListener listener)
    {
        synchronized(this.listeners)
        {
            this.listeners.add(listener);
        }
    }
    
    public void removeListener(final RealTimeListener listener)
    {
        synchronized(this.listeners)
        {
            this.listeners.remove(listener);
        }
    }
    
    private void postClose()
    {
        synchronized(this.listeners)
        {
            for (final RealTimeListener listener : this.listeners)
            {
                listener.onClose();
            }
        }
    }
    
    public Set<User> getUsers()
    {
        return Collections.unmodifiableSet(this.users);
    }
    
    public User getUser(final String name)
    {
        return this.userMap.get(name.toLowerCase());
    }
    
    public Set<BaseChannel> getAllChannels()
    {
        return Collections.unmodifiableSet(this.channels);
    }
    
    public NormalChannel getChannel(final String name)
    {
        return (NormalChannel) this.channelMap.get(name.toLowerCase());
    }
    
    public BaseChannel getChannelById(final ObjectID id)
    {
        return this.channelIdMap.get(id);
    }
    
    public void sendMessage(final String text, final BaseChannel channel)
    {
        this.sendMessage(new Message(text, channel));
    }
    
    private void sendMessage(final Message message)
    {
        final JsonObject object = this.gson.toJsonTree(message).getAsJsonObject();
        final int id = this.appendId(object);
        this.pendingMessages.put(id, message);
        this.send(object);
    }
    
    private int appendId(final JsonObject object)
    {
        final int id = this.nextMessageId++;
        object.addProperty("id", id);
        return id;
    }
    
    private void send(final JsonObject object)
    {
        this.session.getRemote().sendStringByFuture(this.gson.toJson(object));
    }
    
    public boolean isOpen()
    {
        return this.client != null && this.client.isRunning();
    }
    
    private void onReply(final JsonObject reply)
    {
        final int replyId = reply.get("reply_to").getAsInt();
        // TODO: Handle other types of replies
        final Message message = this.pendingMessages.remove(replyId);
        
        if (reply.get("ok").getAsBoolean())
        {
            this.postEvent(new MessageEvent(this.self, message, message.getSubtype()));
        }
        else
        {
            final SlackRTException exception = this.makeException(reply);
            if (exception != null) {
                this.postError(exception);
            }
            // Not sure what to do it no error
        }
    }
    
    private void postEvent(final RealTimeEvent event)
    {
        synchronized(this.listeners)
        {
            for (final RealTimeListener listener : this.listeners)
            {
                listener.onEvent(event);
            }
        }
    }
    
    private SlackRTException makeException(final JsonObject object)
    {
        if (object.has("error"))
        {
            final JsonObject error = object.getAsJsonObject("error");
            return new SlackRTException(error.get("code").getAsInt(), error.get("msg").getAsString());
        }
        return null;
    }
    
    private void postError(final SlackRTException ex)
    {
        synchronized(this.listeners)
        {
            for (final RealTimeListener listener : this.listeners)
            {
                listener.onError(ex);
            }
        }
    }
    
    private void onEvent(final JsonObject event)
    {
        final String type = Utilities.getAsString(event.get("type"));
        if (type == null) {
            return;
        }
        
        // Handle login first
        if (this.needJoinConfirm)
        {
            if ("hello".equals(type))
            {
                this.needJoinConfirm = false;
                this.postLogin();
            }
            else
            {
                this.postError(this.makeException(event));
                this.close();
                return;
            }
            
            return;
        }
        
        RealTimeEvent newEvent = null;
        switch (type)
        {
        case "message":
        {
            // A message from a previous session
            if (event.has("reply_to")) {
                return;
            }
            
            final Message message = this.gson.fromJson(event, Message.class);
            final User user;
            if (message.getSubtype() == MessageType.Edit) {
                user = this.getUserById(message.getEditUserId());
            } else {
                user = this.getUserById(message.getUserId());
            }
            
            newEvent = new MessageEvent(user, message, message.getSubtype());
            break;
        }
        case "channel_created":
            break;
        case "channel_joined":
            break;
        case "channel_left":
            break;
        case "channel_rename":
            break;
        case "channel_archive":
            break;
        case "channel_unarchive":
            break;
        case "channel_history_changed":
            break;
        case "group_joined":
            break;
        case "group_left":
            break;
        case "group_open":
            break;
        case "group_close":
            break;
        case "group_archive":
            break;
        case "group_unarchive":
            break;
        case "group_rename":
            break;
        case "group_history_changed":
            break;
        case "user_change":
            break;
        case "team_join":
            break;
        case "error":
            this.postError(this.makeException(event));
            break;
        }
        
        if (newEvent != null) {
            this.postEvent(newEvent);
        }
    }
    
    private void postLogin()
    {
        synchronized(this.listeners)
        {
            for (final RealTimeListener listener : this.listeners)
            {
                listener.onLoginComplete();
            }
        }
    }
    
    @Override
    public void close()
    {
        try
        {
            this.client.stop();
            this.client = null;
        }
        catch ( final Exception e )
        {
            // Its shutting down, I dont care
        }
    }
    
    public User getUserById(final ObjectID id)
    {
        return this.userIdMap.get(id);
    }
    
    private class SocketClient implements WebSocketListener
    {
        @SuppressWarnings("EmptyMethod")
        @Override
        public void onWebSocketBinary(final byte[] payload, final int offset, final int len )
        {
        }

        @Override
        public void onWebSocketClose(final int statusCode, final String reason )
        {
            RealTimeSession.this.postClose();
        }

        @Override
        public void onWebSocketConnect(final Session session )
        {
            RealTimeSession.this.session = session;
        }

        @Override
        public void onWebSocketError(final Throwable cause )
        {
            cause.printStackTrace();
        }

        @Override
        public void onWebSocketText(final String message )
        {
            final JsonObject event = RealTimeSession.this.gson.fromJson(message, JsonElement.class).getAsJsonObject();
            if (event.has("ok")) {
                RealTimeSession.this.onReply(event);
            } else {
                RealTimeSession.this.onEvent(event);
            }
        }
    }
}
