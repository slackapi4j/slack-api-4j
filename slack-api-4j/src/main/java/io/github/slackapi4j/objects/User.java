package io.github.slackapi4j.objects;

import java.net.URL;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.github.slackapi4j.internal.Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

/**
 * A object that can represent a Slack user.
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class User extends IdBaseObject {
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


    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        this.name = root.get("name").getAsString();
        this.isDeleted = root.get("deleted").getAsBoolean();
        if (!this.isDeleted) {
            this.color = root.get("color").getAsString();
            this.isAdmin = root.get("is_admin").getAsBoolean();
            this.isOwner = root.get("is_owner").getAsBoolean();
            this.isPrimaryOwner = root.get("is_primary_owner").getAsBoolean();
            this.isRestricted = root.get("is_restricted").getAsBoolean();
            this.isUltraRestricted = root.get("is_ultra_restricted").getAsBoolean();
        }

        if (root.has("profile")) {
            JsonObject profile = root.get("profile").getAsJsonObject();
            this.firstName = Utilities.getAsString(profile.get("first_name"));
            this.lastName = Utilities.getAsString(profile.get("last_name"));
            this.realName = Utilities.getAsString(profile.get("real_name"));
            this.email = Utilities.getAsString(profile.get("email"));
            this.skype = Utilities.getAsString(profile.get("skype"));
            this.phone = Utilities.getAsString(profile.get("phone"));
            if (profile.has("image_24")) {
                this.profileImage24 = context.deserialize(profile.get("image_24"), URL.class);
                this.profileImage32 = context.deserialize(profile.get("image_32"), URL.class);
                this.profileImage48 = context.deserialize(profile.get("image_48"), URL.class);
                this.profileImage72 = context.deserialize(profile.get("image_72"), URL.class);
                this.profileImage192 = context.deserialize(profile.get("image_192"), URL.class);
            }
        }

        if (root.has("has_files")) {
            hasFiles = root.get("has_files").getAsBoolean();
        }
    }
}