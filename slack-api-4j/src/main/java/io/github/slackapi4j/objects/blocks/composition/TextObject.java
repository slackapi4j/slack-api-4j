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
import com.google.gson.*;
import lombok.*;

/**
 * Created by benjamincharlton on 20/02/2019.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
public class TextObject extends CompositionObject  {

    private TextType type = TextType.PLAIN;
    private Boolean emoji;
    private Boolean verbatim;
    private String text;

    public TextObject() {
    }

    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        this.type = TextType.getTextType(root.get("type").getAsString());
        this.emoji = Utilities.getAsBoolean(root.get("emoji"), false);
        this.verbatim = Utilities.getAsBoolean(root.get("verbatim"), false);
        this.text = root.get("text").getAsString();
    }
    
    @Override
    protected JsonElement save(JsonObject root, JsonSerializationContext context) {
        root.addProperty("type", this.type.getValue());
        if (this.emoji != null && this.type == TextType.PLAIN) {
            root.addProperty("emoji", this.emoji);
        }
        if (this.verbatim != null) {
            root.addProperty("verbatim", this.verbatim);
        }
        root.addProperty("text", this.text);
        return root;
    }
    
    public enum TextType {
        PLAIN("plain_text"),
        MARKDOWN("mrkdwn");
        @Getter
        private final String value;

        TextType(String value) {
            this.value = value;
        }
        
        public static TextType getTextType(final String val){
            if(PLAIN.value.equals(val)) {
                return PLAIN;
            }
            if(MARKDOWN.value.equals(val)) {
                return MARKDOWN;
            }
            else {
                throw new JsonParseException("Invalid TextObject Type: " + val);
            }
        }
    }
}
