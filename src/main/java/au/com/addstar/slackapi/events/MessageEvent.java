package au.com.addstar.slackapi.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import au.com.addstar.slackapi.Message;
import au.com.addstar.slackapi.Message.MessageType;
import au.com.addstar.slackapi.User;

@RequiredArgsConstructor
@Getter
public class MessageEvent extends RealTimeEvent
{
    private final User user;
    private final Message message;
    private final MessageType type;
    
    @Override
    public String toString()
    {
        return "MessageEvent: " + this.message;
    }
}
