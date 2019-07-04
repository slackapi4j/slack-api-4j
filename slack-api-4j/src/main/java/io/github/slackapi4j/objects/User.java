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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import io.github.slackapi4j.internal.SlackUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

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
  protected void load(final JsonObject root, final JsonDeserializationContext context) {
    super.load(root, context);
    name = root.get("name").getAsString();
    isDeleted = root.get("deleted").getAsBoolean();
    if (!isDeleted) {
      color = root.get("color").getAsString();
      isAdmin = root.get("is_admin").getAsBoolean();
      isOwner = root.get("is_owner").getAsBoolean();
      isPrimaryOwner = root.get("is_primary_owner").getAsBoolean();
      isRestricted = root.get("is_restricted").getAsBoolean();
      isUltraRestricted = root.get("is_ultra_restricted").getAsBoolean();
    }

    if (root.has("profile")) {
      final JsonObject profile = root.get("profile").getAsJsonObject();
      firstName = SlackUtil.getAsString(profile.get("first_name"));
      lastName = SlackUtil.getAsString(profile.get("last_name"));
      realName = SlackUtil.getAsString(profile.get("real_name"));
      email = SlackUtil.getAsString(profile.get("email"));
      skype = SlackUtil.getAsString(profile.get("skype"));
      phone = SlackUtil.getAsString(profile.get("phone"));
      if (profile.has("image_24")) {
        profileImage24 = context.deserialize(profile.get("image_24"), URL.class);
        profileImage32 = context.deserialize(profile.get("image_32"), URL.class);
        profileImage48 = context.deserialize(profile.get("image_48"), URL.class);
        profileImage72 = context.deserialize(profile.get("image_72"), URL.class);
        profileImage192 = context.deserialize(profile.get("image_192"), URL.class);
      }
    }

    if (root.has("has_files")) {
      hasFiles = root.get("has_files").getAsBoolean();
    }
  }
}
