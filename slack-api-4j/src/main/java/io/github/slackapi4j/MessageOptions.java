package io.github.slackapi4j;

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

import io.github.slackapi4j.objects.Attachment;
import io.github.slackapi4j.objects.blocks.Block;
import lombok.Builder;
import lombok.Getter;

import java.net.URL;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "RedundantFieldInitialization"})
@Builder
@Getter
public class MessageOptions {
  public static final MessageOptions DEFAULT = builder()
      .unfurlLinks(true)
      .unfurlMedia(false)
      .asUser(true)
      .linkNames(false)
      .mode(ParseMode.Partial)
      .format(false)
      .build();
  @Builder.Default
  private boolean unfurlLinks = true;
  @Builder.Default
  private boolean unfurlMedia = false;
  private URL iconUrl;
  private String iconEmoji;
  @Builder.Default
  private boolean asUser = false;
  @Builder.Default
  private boolean linkNames = false;
  private String username;
  @Builder.Default
  private ParseMode mode = ParseMode.Partial;
  @SuppressWarnings("deprecation")
  private List<Attachment> attachments;
  private List<Block> blocks;
  @Builder.Default
  private boolean format = false;

  public enum ParseMode {
    Full,
    Partial,
    None
  }
}
