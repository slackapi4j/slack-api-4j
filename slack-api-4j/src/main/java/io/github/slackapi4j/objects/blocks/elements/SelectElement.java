package io.github.slackapi4j.objects.blocks.elements;

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

import java.util.ArrayList;
import java.util.List;

import io.github.slackapi4j.internal.Utilities;
import io.github.slackapi4j.objects.blocks.composition.ConfirmObject;
import io.github.slackapi4j.objects.blocks.composition.Option;
import io.github.slackapi4j.objects.blocks.composition.OptionGroup;
import io.github.slackapi4j.objects.blocks.composition.TextObject;
import com.google.gson.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class SelectElement extends Element {
    private TextObject placeHolder;
    private String action_id;
    private List<Option> options;
    private List<OptionGroup> optionGroups;
    private Option initialOption;
    private ConfirmObject confirm;

    public SelectElement() {
        this.setType(ElementType.STATIC_SELECT);
    }


    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        this.placeHolder = Utilities.getTextObject(root.get("placeholder"), context, TextObject.TextType.PLAIN);
        this.action_id = Utilities.getAsString(root.get("action_id"));
        if(root.has("optons")) {
            if(root.has("optionGroups")) {
                throw new JsonParseException("SelectElement cannot have both Options and OptionGroups");
            } else {
                this.options = new ArrayList<>();
                JsonArray arr = root.getAsJsonArray("options");
                for(JsonElement el:arr){
                    this.options.add(context.deserialize(el, Option.class));
                }
            }
        } else {
            if (!root.has("optionGroups")) {
                throw new JsonParseException("SelectElement must have either Options or OptionGroups");
            }
            this.optionGroups = context.deserialize(root.get("optionGroups"), OptionGroup.class);
        }
        this.initialOption = context.deserialize(root.get("initial_option"), Option.class);
        this.confirm = context.deserialize(root.get("confirm"), ConfirmObject.class);
    }
    
    @Override
    protected JsonObject save(JsonObject root, JsonSerializationContext context) {
        super.save(root, context);
        Utilities.serializeTextObject(root, "placeholder", this.placeHolder, context);
        root.addProperty("action_id", this.action_id);
        if (this.options != null && !this.options.isEmpty()) {
            if (this.optionGroups != null && !this.optionGroups.isEmpty()) {
                throw new JsonParseException("SelectElement cannot have both Options and OptionGroups");
            }
            JsonArray arr = new JsonArray();
            for (Option opt : this.options) {
                arr.add(context.serialize(opt, Option.class));
            }
            root.add("options", arr);
        } else {
            if (this.optionGroups != null && !this.optionGroups.isEmpty()) {
                root.add("optionGroups", context.serialize(this.optionGroups, OptionGroup.class));
            } else {
                throw new JsonParseException("SelectElement must have either Options or OptionGroups");
            }
        }
        root.add("initial_options", context.serialize(this.initialOption, Option.class));
        root.add("confirm", context.serialize(this.confirm, ConfirmObject.class));
        return root;
    }
}
