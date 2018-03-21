package com.coremedia.blueprint.studio.externallibraryproviders;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RSSCallableTest {

  @Test
  public void replaceUnicodeLineSeparator() throws Exception {
    String input = "One.\u2028Two.\u2028Three!";
    String actual = RSSCallable.replaceUnicodeLineSeparator(input);
    assertThat(actual).isEqualTo("One. Two. Three!");
  }
}