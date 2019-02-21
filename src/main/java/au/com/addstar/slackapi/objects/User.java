package au.com.addstar.slackapi.objects;

import java.net.URL;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class User extends IdBaseObject
{
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
		name = root.get("name").getAsString();
		isDeleted = root.get("deleted").getAsBoolean();
		if (!isDeleted)
		{
			color = root.get("color").getAsString();
			isAdmin = root.get("is_admin").getAsBoolean();
			isOwner = root.get("is_owner").getAsBoolean();
			isPrimaryOwner = root.get("is_primary_owner").getAsBoolean();
			isRestricted = root.get("is_restricted").getAsBoolean();
			isUltraRestricted = root.get("is_ultra_restricted").getAsBoolean();
		}

		if (root.has("profile"))
		{
			JsonObject profile = root.get("profile").getAsJsonObject();
			firstName = Utilities.getAsString(profile.get("first_name"));
			lastName = Utilities.getAsString(profile.get("last_name"));
			realName = Utilities.getAsString(profile.get("real_name"));
			email = Utilities.getAsString(profile.get("email"));
			skype = Utilities.getAsString(profile.get("skype"));
			phone = Utilities.getAsString(profile.get("phone"));
			if (profile.has("image_24"))
			{
				profileImage24 = context.deserialize(profile.get("image_24"), URL.class);
				profileImage32 = context.deserialize(profile.get("image_32"), URL.class);
				profileImage48 = context.deserialize(profile.get("image_48"), URL.class);
				profileImage72 = context.deserialize(profile.get("image_72"), URL.class);
				profileImage192 = context.deserialize(profile.get("image_192"), URL.class);
			}
		}

		if (root.has("has_files"))
			hasFiles = root.get("has_files").getAsBoolean();
	}
}
