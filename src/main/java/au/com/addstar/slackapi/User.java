package au.com.addstar.slackapi;

import java.lang.reflect.Type;
import java.net.URL;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class User
{
    private ObjectID id;
    private String name;
    private boolean isDeleted;
    private String color;
    
    private String firstName;
    private String lastName;
    private String realName;
    private String email;
    private String skype;
    private String phone;
    
    private URL profileImage24;
    private URL profileImage32;
    private URL profileImage48;
    private URL profileImage72;
    private URL profileImage192;
    
    private boolean isAdmin;
    private boolean isOwner;
    private boolean isPrimaryOwner;
    private boolean isRestricted;
    private boolean isUltraRestricted;
    private boolean hasFiles;
    
    static Object getGsonAdapter()
    {
        return new UserJsonAdapter();
    }
    
    private static class UserJsonAdapter implements JsonDeserializer<User>
    {
        @Override
        public User deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context ) throws JsonParseException
        {
            if (!(element instanceof JsonObject)) {
                throw new JsonParseException("Expected JSONObject as group root");
            }
            
            final JsonObject root = (JsonObject)element;
            
            final User user = new User();
            user.id = new ObjectID(root.get("id").getAsString());
            user.name = root.get("name").getAsString();
            user.isDeleted = root.get("deleted").getAsBoolean();
            if (!user.isDeleted)
            {
                user.color = root.get("color").getAsString();
                user.isAdmin = root.get("is_admin").getAsBoolean();
                user.isOwner = root.get("is_owner").getAsBoolean();
                user.isPrimaryOwner = root.get("is_primary_owner").getAsBoolean();
                user.isRestricted = root.get("is_restricted").getAsBoolean();
                user.isUltraRestricted = root.get("is_ultra_restricted").getAsBoolean();
            }
            
            if (root.has("profile"))
            {
                final JsonObject profile = root.get("profile").getAsJsonObject();
                user.firstName = Utilities.getAsString(profile.get("first_name"));
                user.lastName = Utilities.getAsString(profile.get("last_name"));
                user.realName = Utilities.getAsString(profile.get("real_name"));
                user.email = Utilities.getAsString(profile.get("email"));
                user.skype = Utilities.getAsString(profile.get("skype"));
                user.phone = Utilities.getAsString(profile.get("phone"));
                if (profile.has("image_24"))
                {
                    user.profileImage24 = context.deserialize(profile.get("image_24"), URL.class);
                    user.profileImage32 = context.deserialize(profile.get("image_32"), URL.class);
                    user.profileImage48 = context.deserialize(profile.get("image_48"), URL.class);
                    user.profileImage72 = context.deserialize(profile.get("image_72"), URL.class);
                    user.profileImage192 = context.deserialize(profile.get("image_192"), URL.class);
                }
            }
            
            if (root.has("has_files")) {
                user.hasFiles = root.get("has_files").getAsBoolean();
            }
            return user;
        }
    }
}
