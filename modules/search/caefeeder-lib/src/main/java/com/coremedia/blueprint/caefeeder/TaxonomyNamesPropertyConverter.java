package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A {@link TaxonomyPropertyConverter} that returns a comma-separated string of taxonomy names.
 */
public class TaxonomyNamesPropertyConverter extends TaxonomyPropertyConverter {

  @Override
  @Nullable
  protected String convertNamedTaxonomy(@Nonnull NamedTaxonomy namedTaxonomy) {
    return namedTaxonomy.getName();
  }

  @Override
  @Nullable
  protected String convertTaxonomy(@Nonnull CMTaxonomy taxonomy) {
    return taxonomy.getValue();
  }

}
