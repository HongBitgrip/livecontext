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
      <div class="cm-placeholder cm-user-chooser" <@cm.metadata data=[logoutAction.content, "properties.id"] />>
        <a class="cm-user-chooser__button" title="${self.user.givenName}" data-toggle="collapse"
           data-target=".cm-user-chooser__items">
          <span class="cm-user-chooser__givenname">${self.user.givenName!""}</span>
          <span class="cm-user-chooser__surname">${self.user.surName!""}</span>
          <span class="cm-user-chooser__icon glyphicon glyphicon-triangle-bottom" aria-hidden="true"></span>
        </a>
        <ul class="cm-user-chooser__items collapse">
          <li class="cm-user-chooser__item">
            <a href="${cm.getLink(profileAction)}"
               title="${bp.getMessage("userDetails_title")}" <@cm.metadata data=[profileAction.content, "properties.id"] />>
              <span class="cm-icon__info"><@bp.message "userDetails_title" /></span>
            </a>
          </li>
          <li class="cm-user-chooser__item">
            <a href="${logoutLink}"
               title="${bp.getMessage("logout_title")}" <@cm.metadata data=[logoutAction.content, "properties.id"] />>
              <span class="cm-icon__info"><@bp.message "logout_title" /></span>
            </a>
          </li>
        </ul>
      </div>
    <#-- login -->
    <#else>
      <div class="cm-placeholder cm-user-chooser" <@cm.metadata data=[loginAction.content, "properties.id"] />>
        <#assign loginLink=cm.getLink(self, {"next": "$nextUrl$", "absolute": true, "scheme": "https"})/>
        <a data-href="${loginLink}" <@cm.metadata data="properties.teaserTitle" />>
          ${self.action.teaserTitle!""}
        </a>
      </div>
    </#if>
  </#if>
</#if>
