package io.github.slackapi4j.objects.blocks;

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

import java.util.List;

import io.github.slackapi4j.objects.blocks.elements.Element;
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
public class ActionBlock extends Block{
    private List<Element> elements;

    public ActionBlock() {
        this.setType(BlockType.ACTIONS);
    }

    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        JsonArray arr = root.getAsJsonArray("elements");
        this.elements = Element.deserialize(arr,context);
    }
    
    @Override
    protected JsonObject save(JsonObject root, JsonSerializationContext context)
    {
        JsonArray array = new JsonArray();
        for (Element el : this.elements) {
            JsonElement e  = context.serialize(el);
            array.add(e);
        }
        root.add("elements",array);
        return super.save(root, context);
    }
    
    
}
