package io.github.slackapi4j.objects.blocks.elements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.annotations.SerializedName;
import io.github.slackapi4j.internal.SlackUtil;
import io.github.slackapi4j.objects.blocks.composition.ConfirmObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 6/07/2019.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatePicker extends ConfirmableElement {

  @SerializedName("initial_date")
  private long initialDate;

  public DatePicker() {
    super();
    setType(ElementType.DATEPICKER);
  }

  @Override
  protected void load(final JsonObject root, final JsonDeserializationContext context) {
    super.load(root, context);
    initialDate = SlackUtil.getAsTimestamp(root.get("initial_date"));
    actionId = SlackUtil.getAsString(root.get("action_id"));
    confirm = context.deserialize(root.get("confirm"), ConfirmObject.class);
  }

  @Override
  protected JsonObject save(final JsonObject root, final JsonSerializationContext context) {
    return super.save(root, context);
  }

}
