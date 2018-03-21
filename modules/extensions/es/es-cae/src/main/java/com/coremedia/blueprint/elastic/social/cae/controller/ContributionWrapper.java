package com.coremedia.blueprint.elastic.social.cae.controller;


import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ContributionWrapper<T, K> {

  private T contribution;
  private List<K> subContributions;

  ContributionWrapper(@Nonnull T contribution, @Nullable List<K> subContributions) {
    this.contribution = contribution;
    this.subContributions = subContributions;
  }

  void setContribution(T contribution) {
    this.contribution = contribution;
  }

  T getContribution() {
    return contribution;
  }

  List<K> getSubContributions() {
    return subContributions == null ? Collections.<K>emptyList() : ImmutableList.copyOf(subContributions);
  }
}
