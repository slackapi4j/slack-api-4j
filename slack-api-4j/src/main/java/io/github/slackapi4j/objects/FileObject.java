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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by benjamincharlton on 26/08/2018.
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class FileObject extends TimeStampedBaseObject {
  private String name;
  private String title;
  private String mimeType;
  private String fileType;
  private String prettyType;
  private ObjectID user;
  private boolean editable;
  private long size;
  private Mode mode;
  private boolean isExternal;
  private boolean isPublic;
  private boolean publicUrlShared;
  private boolean displayAsBot;

  enum Mode {

    HOSTED("hosted"),
    EXTERNAL("external"),
    SNIPPET("snippet"),
    POST("post");

    private final String mode;

    Mode(final String mode) {
      this.mode = mode;
    }

    public String getMode() {
      return mode;
    }
  }
}
