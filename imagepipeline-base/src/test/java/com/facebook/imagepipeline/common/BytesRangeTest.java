/*
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.imagepipeline.common;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

public class BytesRangeTest {

  @Test
  public void testHeaderValueForRangeFrom() {
    assertThat(BytesRange.from(2000).toHttpRangeHeaderValue()).isEqualTo("bytes=2000-");
  }

  @Test
  public void testHeaderValueForRangeTo() {
    assertThat(BytesRange.toMax(1000).toHttpRangeHeaderValue()).isEqualTo("bytes=0-1000");
  }

  @Test
  public void testFromContentRangeHeaderWithValidHeader() {
    assertValidFromContentRangeHeader("bytes 0-499/1234", 0, 499);
    assertValidFromContentRangeHeader("bytes 500-999/1234", 500, 999);
    assertValidFromContentRangeHeader("bytes 500-1233/1234", 500, BytesRange.TO_END_OF_CONTENT);
    assertValidFromContentRangeHeader("bytes 734-1233/1234",734, BytesRange.TO_END_OF_CONTENT);
  }

  @Test
  public void testFromContentRangeHeaderWithInvalidHeader() {
    assertThat(BytesRange.fromContentRangeHeader(null)).isNull();
    assertInvalidFromContentRangeHeader("not bytes 0-499/1234");
    assertInvalidFromContentRangeHeader("bytes -499/1234");
    assertInvalidFromContentRangeHeader("bytes 0-/1234");
    assertInvalidFromContentRangeHeader("bytes 499/1234");
    assertInvalidFromContentRangeHeader("bytes 0-499");
    assertInvalidFromContentRangeHeader("bytes 0-/");
  }

  private static void assertValidFromContentRangeHeader(
      String header,
      int expectedFrom,
      int expectedEnd) {
    final BytesRange bytesRange = BytesRange.fromContentRangeHeader(header);
    assertThat(bytesRange.from).isEqualTo(expectedFrom);
    assertThat(bytesRange.to).isEqualTo(expectedEnd);
  }

  private static void assertInvalidFromContentRangeHeader(String header) {
    try {
      BytesRange.fromContentRangeHeader(header);
      failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
    } catch (IllegalArgumentException x) {
      // Expected
    }
  }
}