package io.github.slackapi4j.objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
public class ObjectID
{
    private ObjectType type;
    private final String id;

    public ObjectID(String full)
    {
        if (full.isEmpty())
        {
            this.id = "";
            this.type = ObjectType.Unknown;
            return;
        }

        char classifier = Character.toUpperCase(full.charAt(0));
        this.type = ObjectType.Unknown;

        for (ObjectType type : ObjectType.values())
        {
            if (type.getClassifier() == classifier)
            {
                this.type = type;
                break;
            }
        }

        this.id = full.substring(1);
    }

    @Override
    public String toString()
    {
        return String.format("%s%s", this.type.getClassifier(), this.id);
    }

    @Getter
    @RequiredArgsConstructor
    private enum ObjectType
    {
        User('U', io.github.slackapi4j.objects.User.class),
        Conversation('C', io.github.slackapi4j.objects.Conversation.class),
        GroupConversation('G', io.github.slackapi4j.objects.Conversation.class),
        DirectConversation('D', io.github.slackapi4j.objects.Conversation.class),
        Team('T', null),
        Bot('B', null),
        Unknown('\0', null);

        private final char classifier;
        private final Class<?> typeClass;
    }
}
