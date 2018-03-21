<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="profileFlow" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="loginLink" type="java.lang.String" -->
<#-- @ftlvariable name="logoutLink" type="java.lang.String" -->

<#assign loginAction=self.loginAction />
<#assign logoutAction=self.logoutAction />
<#assign profileAction=self.profileAction />

<#if loginAction?has_content && logoutAction?has_content && profileAction?has_content>
  <#if self.authenticated>
  <a href="${cm.getLink(self.profileAction!cm.UNDEFINED)}"
     title=""<@cm.metadata data=[profileAction.content, "properties.id"] />><@bp.message "userDetails_title" /></a>
  <#else>
  <a data-href="${cm.getLink(self, {"next": "$nextUrl$", "absolute": true, "scheme": lc.getSecureScheme()})}"<@cm.metadata data=[loginAction.content, "properties.id"] />><@bp.message "login_title" /></a>
  </#if>
</#if>
