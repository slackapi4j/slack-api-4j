package io.github.slackapi4j.objects.blocks.composition;

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

import io.github.slackapi4j.internal.Utilities;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.*;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
public class ConfirmObject extends CompositionObject{
    
    private TextObject title;
    private TextObject text;
    private TextObject confirm;
    private TextObject deny;

    @Override
    protected JsonElement save(JsonObject root, JsonSerializationContext context) {
        Utilities.serializeTextObject(root, "title", this.title, context);
        Utilities.serializeTextObject(root, "text", this.text, context);
        Utilities.serializeTextObject(root, "confirm", this.confirm, context);
        Utilities.serializeTextObject(root, "deny", this.deny, context);
        return root;
    }
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        this.title = Utilities.getTextObject(root.get("title"), context, TextObject.TextType.PLAIN);
        this.text = Utilities.getTextObject(root.get("text"), context, null);
        this.confirm = Utilities.getTextObject(root.get("confirm"), context, TextObject.TextType.PLAIN);
        this.deny = Utilities.getTextObject(root.get("deny"), context, TextObject.TextType.PLAIN);
    }
}
