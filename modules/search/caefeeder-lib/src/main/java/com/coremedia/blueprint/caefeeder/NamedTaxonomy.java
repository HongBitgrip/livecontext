package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Value object that holds a {@link Content} that represents a taxonomy with the value of its {@link CMTaxonomy#VALUE}
 * property.
 */
public class NamedTaxonomy {

  @Nonnull
  private final Content content;
  private final String name;

  public NamedTaxonomy(@Nonnull Content content) {
    this.content = requireNonNull(content);
    this.name = content.getString(CMTaxonomy.VALUE);
  }

  @Nonnull
  public Content getContent() {
    return content;
  }

  @Nullable
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NamedTaxonomy that = (NamedTaxonomy) o;
    return Objects.equals(content, that.content) &&
           Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, name);
  }

  @Override
  public String toString() {
    return IdHelper.parseContentId(content.getId()) + ':' + name;
  }

}
