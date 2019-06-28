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

import java.net.MalformedURLException;
import java.net.URL;

import io.github.slackapi4j.internal.Utilities;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 20/02/2019.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class ImageElement extends Element {
    
    private URL imageURL;
    private String altText;

    public ImageElement() {
        this.setType(ElementType.IMAGE);
    }

    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        try {
            this.imageURL = new URL(root.get("imageURL").getAsString());
        } catch (MalformedURLException e) {
            throw new JsonParseException("URL could not be decoded");
        }
        this.altText = Utilities.getAsString(root.get("alt_text"));
    }
    
    @Override
    protected JsonObject save(JsonObject root, JsonSerializationContext context) {
        super.save(root, context);
        if(this.imageURL != null) {
            root.addProperty("imageUrl",this.imageURL.toString());
        }
        if (this.altText != null) {
            root.addProperty("alt_test", this.altText);
        }
        return root;
    }
}
