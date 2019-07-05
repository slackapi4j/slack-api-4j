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
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
public class ObjectID {
  private final String id;
  private ObjectType type;

  /**
   * An object that can be classed as per its identifier.
   *
   * @param full the full Identifier string starting with U,T,D,G,C or B
   */
  public ObjectID(final String full) {
    if (full.isEmpty()) {
      id = "";
      type = ObjectType.Unknown;
      return;
    }

    final char classifier = Character.toUpperCase(full.charAt(0));
    type = ObjectType.Unknown;

    for (final ObjectType type : ObjectType.values()) {
      if (type.getClassifier() == classifier) {
        this.type = type;
        break;
      }
    }

    id = full.substring(1);
  }

  @Override
  public String toString() {
    return String.format("%s%s", type.getClassifier(), id);
  }

  public boolean isUnknown() {
    return type == ObjectType.Unknown;
  }

  @Getter
  @RequiredArgsConstructor
  private enum ObjectType {
    User('U', User.class),
    Conversation('C', Conversation.class),
    GroupConversation('G', Conversation.class),
    DirectConversation('D', Conversation.class),
    File('F',FileObject.class),
    Team('T', null),
    Bot('B', null),
    Unknown('\0', null);

    private final char classifier;
    private final Class<?> typeClass;
  }
}
