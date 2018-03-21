package com.coremedia.blueprint.analytics.elastic.webtrends;

import com.google.common.base.Function;

import javax.annotation.Nullable;

class ReportEntryTransformer implements Function<ReportEntry, String> {
  @Nullable
  @Override
  public String apply(@Nullable ReportEntry input) {
    if(input != null) {
      return input.getValue();
    }
    return null;
  }
}
