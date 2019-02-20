package au.com.addstar.slackapi.objects.blocks;

import au.com.addstar.slackapi.objects.*;
import com.google.gson.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 20/02/2019.
 */
@NoArgsConstructor
@Getter
public class Block extends BaseObject {
    private BlockType type;

    public static Object getGSONAdapter(){

    }

    private static class BlockJSONAdapter implements JsonDeserializer<Block>{

        @Override
        public Block deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonObject))
                throw new JsonParseException("Expected JSONObject as channel root");
            JsonObject root = (JsonObject)json;
            Block object;
            if(type.equals(Section.class)) {
                object = new Section();
            }else {
                throw new JsonParseException("Cant load unknown channel type");
            }
            object.load(root,context);
            return object;
        }
    }

    protected void load(JsonObject root, JsonDeserializationContext context) {
        type = BlockType.valueOf(root.get("type").getAsString());
    }

    enum BlockType {
         SECTION,
         DIVIDER,
         IMAGE,
         ACTIONS,
         CONTEXT,
    }


}
