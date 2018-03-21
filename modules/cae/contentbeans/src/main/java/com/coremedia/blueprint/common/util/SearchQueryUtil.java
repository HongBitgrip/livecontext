package com.coremedia.blueprint.common.util;

import com.coremedia.blueprint.cae.search.Condition;
import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.cae.search.Value;
import com.coremedia.cap.common.CapType;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Utility methods for search queries
 */
public final class SearchQueryUtil {

  private SearchQueryUtil() {
    // utility class
  }

  /**
   * Convert the given comma-separated string of doctypes into a filter condition
   * @param docTypes comma-separated string of content type names
   * @param repository the content repository
   * @return Condition for use as document type filter
   */
  public static Condition createDocumentTypeFilter(String docTypes, ContentRepository repository) {
    final List<CapType> capTypes = new ArrayList<>();
    final StringTokenizer tokenizer = new StringTokenizer(docTypes, ",");
    while (tokenizer.hasMoreTokens()) {
      final String docTypeToken = tokenizer.nextToken().trim();
      final Set<ContentType> types = repository.getContentType(docTypeToken).getSubtypes();
      capTypes.addAll(types);
    }

    final List<String> types = Lists.transform(capTypes, new CapTypeToString());
    return Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE, Value.anyOf(types));
  }

  /**
   * Helper class.
   */
  private static class CapTypeToString implements Function<CapType, String> {
    @Override
    public String apply(CapType input) {
      return input.toString();
    }
  }
}
