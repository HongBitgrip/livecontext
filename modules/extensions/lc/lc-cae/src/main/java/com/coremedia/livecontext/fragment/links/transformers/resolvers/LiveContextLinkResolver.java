package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;

import javax.servlet.http.HttpServletRequest;

public interface LiveContextLinkResolver {

  /**
   * Is resolver applicable for current bean?
   * @param bean ContentBean
   * @return <code>true</code> if Resolver should try to resolve url for current bean.
   *         <code>false</code> if not
   */
  boolean isApplicable(Object bean);

  /**
   * Resolves static part of the Commerce-URL for the current bean.
   *
   * @param bean current content
   * @param variant parameter can be provided as param via link-tag. variants are configured within a settings-dokument in the repository.
   * @param navigation current navigation context
   * @param request Current request
   * @return the static url part of the LiveContext-URL.
   */
  String resolveUrl(Object bean, String variant, CMNavigation navigation, HttpServletRequest request);

}
