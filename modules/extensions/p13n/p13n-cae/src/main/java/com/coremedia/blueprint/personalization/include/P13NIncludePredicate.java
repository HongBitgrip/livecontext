package com.coremedia.blueprint.personalization.include;

import com.coremedia.blueprint.cae.view.DynamicIncludePredicate;
import com.coremedia.blueprint.personalization.contentbeans.CMP13NSearch;
import com.coremedia.blueprint.personalization.contentbeans.CMSelectionRules;
import com.coremedia.objectserver.view.RenderNode;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public class P13NIncludePredicate implements DynamicIncludePredicate {

  public static Pattern VIEW_EXCLUDE_PATTERN = Pattern.compile("^fragmentPreview(\\[[^\\]]*\\]){0,1}$");
  public static final String VIEW_NAME_AS_PREVIEW = "asPreview";

  @Override
  public boolean apply(@Nullable RenderNode input) {
    if (input == null) {
      return false;
    }
    if (isBeanMatching(input.getBean())){
      return isViewMatching(input.getView());
    }
    return false;
  }

  private boolean isViewMatching(String view) {
    if (view == null) {
      return true;
    }
    return !(VIEW_EXCLUDE_PATTERN.matcher(view).matches() || view.equals(VIEW_NAME_AS_PREVIEW));
  }

  private boolean isBeanMatching(Object bean) {
    return bean instanceof CMSelectionRules || bean instanceof CMP13NSearch;
  }
}
