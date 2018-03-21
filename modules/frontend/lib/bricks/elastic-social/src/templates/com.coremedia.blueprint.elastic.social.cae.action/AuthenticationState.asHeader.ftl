<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="profileAction" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="loginLink" type="java.lang.String" -->
<#-- @ftlvariable name="logoutLink" type="java.lang.String" -->

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />
<#assign loginAction=self.loginAction />
<#assign logoutAction=self.logoutAction />
<#assign profileAction=self.profileAction />

<#-- show login/logout link, if ES is enabled. this templates is included as fragment (DynamicInclude.ftl) -->
<#if elasticSocialConfiguration.isFeedbackEnabled()!false>
  <#if loginAction?has_content && logoutAction?has_content && profileAction?has_content>
    <#-- user profile / logout -->
    <#if self.authenticated>
      <#assign logoutLink=cm.getLink(logoutAction)/>
      <div class="cm-icon cm-icon--logout"<@cm.metadata data=[logoutAction.content, "properties.id"] />>
        <a href="${logoutLink}" title="<@bp.message "logout_title" />">
          <i class="cm-icon__symbol icon-profile-unlocked-alternative"></i>
          <span class="cm-icon__info cm-visuallyhidden"><@bp.message "logout_title" /></span>
        </a>
      </div>
      <div class="cm-icon cm-icon--user-details"<@cm.metadata data=[profileAction.content, "properties.id"] />>
        <a href="${cm.getLink(profileAction)}" title="<@bp.message "userDetails_title" />">
          <i class="cm-icon__symbol icon-profile-unlocked"></i>
          <span class="cm-icon__info cm-visuallyhidden"><@bp.message "userDetails_title" /></span>
        </a>
      </div>
    <#else>
      <div class="cm-icon cm-icon--login"<@cm.metadata data=[loginAction.content, "properties.id"] />>
        <#assign loginLink=cm.getLink(self, {"next": "$nextUrl$", "absolute": true, "scheme": "https"})/>
        <a data-href="${loginLink}" <@cm.metadata data="properties.teaserTitle" />>
          <i class="cm-icon__symbol icon-profile-locked"></i>
          <span class="cm-icon__info cm-visuallyhidden">${self.action.teaserTitle!""}</span>
        </a>
      </div>
    </#if>
  </#if>
</#if>
