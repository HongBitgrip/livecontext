package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.links.Link;
import com.coremedia.objectserver.web.links.UriComponentsHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_NAVIGATION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ACTION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;

/**
 * Handler and Linkscheme for {@link com.coremedia.blueprint.common.contentbeans.CMAction} beans that are contained in a
 * {@link com.coremedia.blueprint.common.contentbeans.Page}. Can handle Webflows.
 */
@SuppressWarnings("LocalCanBeFinal")
@Link
@RequestMapping
public class PageActionHandler extends DefaultPageActionHandler {

  /**
   * URI pattern prefix for actions on page resources. The action name is appended to this URI with a slash.
   * The final URI might look like {@value #URI_PATTERN}
   */
  public static final String URI_PATTERN = UriConstants.Patterns.ACTION_URI_PATTERN;

  /**
   * Fallback: Handles all remaining actions by simply displaying the page
   */
  @RequestMapping(URI_PATTERN)
  public ModelAndView handleRequest(@PathVariable(SEGMENT_ID) ContentBean contentBean,
                                    @PathVariable(SEGMENTS_NAVIGATION) List<String> navigationPath,
                                    @PathVariable(SEGMENT_ACTION) String action,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
    Navigation navigationFromLink = getNavigation(navigationPath);

    //find navigation context from the _folderProperties of the Action
    CMAction actionBean = (CMAction) contentBean;
    final List<CMContext> contexts = actionBean.getContexts();
    Navigation navigation = contexts != null && !contexts.isEmpty() ? contexts.get(0) : navigationFromLink;

    return handleRequestInternal(contentBean, navigation, action, request, response);
  }

  @SuppressWarnings({"TypeMayBeWeakened", "UnusedParameters"})
  @Link(type = CMAction.class, uri = URI_PATTERN)
  @Nullable
  public UriComponents buildLink(
          @Nonnull CMAction action,
          @Nonnull UriTemplate uriPattern,
          @Nonnull Map<String, Object> linkParameters) {
    return buildLinkInternal(action, uriPattern, linkParameters);
  }
}
