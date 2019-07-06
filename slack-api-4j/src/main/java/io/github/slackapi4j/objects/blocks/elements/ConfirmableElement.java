package io.github.slackapi4j.objects.blocks.elements;

import com.google.gson.annotations.SerializedName;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 6/07/2019.
 */
abstract class ConfirmableElement extends Element {

  @SerializedName("confirm")
  Object confirm;

  @SerializedName("action_id")
  String actionId;

}
