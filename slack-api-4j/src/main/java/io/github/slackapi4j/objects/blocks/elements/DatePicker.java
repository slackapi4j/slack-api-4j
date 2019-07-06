package io.github.slackapi4j.objects.blocks.elements;

/*-
 * #%L
 * Slack-Api-4J
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
