package io.github.slackapi4j.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.Message.MessageType;
import io.github.slackapi4j.objects.User;

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
