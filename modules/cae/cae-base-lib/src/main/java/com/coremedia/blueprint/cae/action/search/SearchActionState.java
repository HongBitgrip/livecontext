package com.coremedia.blueprint.cae.action.search;

import com.coremedia.blueprint.cae.action.CMActionState;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.common.contentbeans.CMAction;


/**
 * The current state of the search action. Holds the action form as well as the action result
 *
 * @cm.template.api
 */
public class SearchActionState implements CMActionState {

  public static final String ERROR_QUERY_TOO_SHORT = "queryTooShort";

  private final CMAction action;
  private final SearchFormBean form;
  private final SearchResultBean result;
  private final SearchResultBean topicsResult;
  private String errorCode = null;

  public SearchActionState(CMAction action, SearchFormBean form, SearchResultBean result, SearchResultBean topicsResult) {
    this.action = action;
    this.form = form;
    this.result = result;
    this.topicsResult = topicsResult;
  }

  public SearchActionState(CMAction action, SearchFormBean form, SearchResultBean result) {
    this(action, form, result, null);
  }

  public SearchActionState(CMAction action, SearchFormBean form, String errorCode) {
    this(action, form, null, null);
    this.errorCode = errorCode;
  }

  public SearchActionState(CMAction action) {
    this(action, new SearchFormBean(), null, null);
  }

  @Override
  public CMAction getAction() {
    return action;
  }

  /**
   * @cm.template.api
   */
  public SearchFormBean getForm() {
    return form;
  }

  /**
   * @cm.template.api
   */
  public SearchResultBean getResult() {
    return result;
  }

  public SearchResultBean getTopicsResult() {
    return topicsResult;
  }

  /**
   * @cm.template.api
   */
  public boolean isQueryTooShort() {
    return ERROR_QUERY_TOO_SHORT.equals(errorCode);
  }

  @Override
  public String toString() {
    return getClass().getName();
  }
}
