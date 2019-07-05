package io.github.slackapi4j.objects;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Narimm on 4/07/2019.
 */
class AttachmentTest {

  @Test
  void testSerialization() {
    final Attachment attachment = new Attachment("test fallback");
    attachment.setColor("red");
    Attachment.MarkDownFormats formats = new Attachment.MarkDownFormats();
    formats.setFormatFields(false);
    attachment.setFormats(formats);
    attachment.getFields().add(new Attachment.AttachmentField("field test title", "some value", true));
    attachment.setAuthorName("TestAuthor");
    attachment.setPretext("Some Test Pretext");
    attachment.setTitle("Test Title");
    try {
      final URL url = new URL("https", "test.com", "/testImage.gpg");
      attachment.setTitleLink(url);
    } catch (final MalformedURLException e) {
      e.printStackTrace();
    }
    final Gson gson = new Gson();
    final Type type = TypeToken.get(Attachment.class).getType();
    final String elem = gson.toJson(attachment);
    final Attachment attachment1 = gson.fromJson(elem, type);
    assertEquals("TestAuthor", attachment1.getAuthorName());
    assertEquals(attachment1, attachment);
  }
}
