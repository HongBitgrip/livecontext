<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="userDetails" type="com.coremedia.blueprint.elastic.social.cae.flows.UserDetails" -->
<#-- @ftlvariable name="actionHandler" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="elasticSocialConfiguration" type="com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration" -->
<#-- @ftlvariable name="explicitInterests" type="com.coremedia.blueprint.personalization.forms.PersonalizationForm" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="value" type="java.util.TimeZone" -->

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />
<#assign actionHandler=self.action />

<div class="container">
  <div class="row">
    <div class="cm-form cm-form--userdetailsform well col-xs-12 col-md-6 col-md-push-3"<@cm.metadata data=[self.action.content!"", "properties.id"]/>>
      <h1 class="cm-form__headline"><@bp.message "userDetails_personalDetails" /></h1>

      <#if userDetails?has_content>
        <form method="post" enctype="multipart/form-data">
          <input type="hidden" name="_CSRFToken" value="${_CSRFToken}">
          <input type="hidden" name="execution" value="${flowExecutionKey}">
          <input type="hidden" name="_eventId_saveUser">
          <@bp.notificationFromSpring path="userDetails" />

          <#-- username -->
          <@spring.bind path="userDetails.username"/>
          <div class="form-group<#if spring.status.error> has-error</#if>">
            <@bp.labelFromSpring path="userDetails.username" text='${bp.getMessage("userDetails_username")}'/>
            <@spring.formInput path="userDetails.username" attributes='class="form-control" placeholder="${bp.getMessage("registration_username_label")}" required'/>
            <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
          </div>

          <#if userDetails.viewOwnProfile || userDetails.preview>

            <#-- givenname -->
            <@spring.bind path="userDetails.givenname"/>
            <div class="form-group<#if spring.status.error> has-error</#if>">
              <@bp.labelFromSpring path="userDetails.givenname" text='${bp.getMessage("userDetails_givenname")}'/>
              <@spring.formInput path="userDetails.givenname" attributes='class="form-control" placeholder="${bp.getMessage("registration_username_label")}" required'/>
              <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
            </div>

            <#-- surname -->
            <@spring.bind path="userDetails.surname"/>
            <div class="form-group<#if spring.status.error> has-error</#if>">
              <@bp.labelFromSpring path="userDetails.surname" text='${bp.getMessage("userDetails_surname")}'/>
              <@spring.formInput path="userDetails.surname" attributes='class="form-control" placeholder="${bp.getMessage("registration_username_label")}" required'/>
              <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
            </div>

            <#-- email -->
            <@spring.bind path="userDetails.emailAddress"/>
            <div class="form-group<#if spring.status.error> has-error</#if>">
              <@bp.labelFromSpring path="userDetails.emailAddress" text='${bp.getMessage("userDetails_emailAddress")}'/>
              <@spring.formInput path="userDetails.emailAddress" attributes='class="form-control" placeholder="${bp.getMessage("userDetails_emailAddress")}" required' fieldType="email"/>
              <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
            </div>

            <#-- notification -->
            <@spring.bind path="userDetails.receiveCommentReplyEmails"/>
            <div class="form-group<#if spring.status.error> has-error</#if>">
              <div class="checkbox">
                <label>
                  <@spring.formCheckbox path="userDetails.receiveCommentReplyEmails"/>${bp.getMessage("userDetails_receiveCommentReplyEmails")}
                </label>
              </div>
            </div>
          </#if>

          <#-- profile image -->
            <div class="form-group">
              <@spring.bind path="userDetails.profileImage"/>
              <label for="${spring.status.expression?replace('[','')?replace(']','')}">${bp.getMessage("userDetails_profileImage")}</label>

              <div class="row">
                <#if userDetails.profileImage?has_content>
                <div class="col-xs-12 col-sm-4">
                  <#assign link=cm.getLink(userDetails.profileImage {"transform":true, "width":elasticSocialConfiguration.userImageWidth!60?int, "height": elasticSocialConfiguration.userImageHeight!60?int})/>
                  <img class="cm-form__image" src="${link}" title="" alt="userimage">

                  <@spring.bind path="userDetails.deleteProfileImage"/>
                  <div class="checkbox">
                    <label>
                      <@spring.formCheckbox path="userDetails.deleteProfileImage"/>${bp.getMessage("userDetails_deleteProfileImage")}
                    </label>
                  </div>
                </div>
                </#if>
                <div class="col-xs-12 col-sm-8">
                  <input type="file" accept="image/*" name="imageFile" id="imageFile">
                </div>
              </div>
            </div>

          <#-- timezone -->
          <@spring.bind path="userDetails.timeZoneId"/>
          <div class="form-group<#if spring.status.error> has-error</#if>">
            <@bp.labelFromSpring path="userDetails.timeZoneId" text='${bp.getMessage("userDetails_timeZone")}'/>
            <select id="${spring.status.expression?replace('[','')?replace(']','')}"
                    name="${spring.status.expression}" class="form-control">
              <#list timeZones![] as value>
                <option value="${value.ID}"<@spring.checkSelected value.ID/>>${value.ID}</option>
              </#list>
            </select>
            <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
          </div>

          <#-- language -->
          <@spring.bind path="userDetails.localizedLocale"/>
          <div class="form-group<#if spring.status.error> has-error</#if>">
            <@bp.labelFromSpring path="userDetails.localizedLocale" text='${bp.getMessage("userDetails_localeLanguage")}'/>
            <select id="${spring.status.expression?replace('[','')?replace(']','')}"
                    name="${spring.status.expression}" class="form-control">
              <#list locales as value>
                <option value="${value}"<@spring.checkSelected value/>>${value.displayLanguage}</option>
              </#list>
            </select>
            <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
          </div>

          <div class="form-group">
            <h3><@bp.message "userDetails_changePassword" /></h3>
          </div>

          <#-- password -->
          <@spring.bind path="userDetails.password"/>
          <div class="form-group<#if spring.status.error> has-error</#if>">
            <@bp.labelFromSpring path="userDetails.password" text='${bp.getMessage("userDetails_password")}'/>
            <@spring.formInput path="userDetails.password" attributes='class="form-control" placeholder="${bp.getMessage("userDetails_password")}"' fieldType="password"/>
            <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
          </div>

          <#-- new password -->
          <@spring.bind path="userDetails.newPassword"/>
          <div class="form-group<#if spring.status.error> has-error</#if>">
            <@bp.labelFromSpring path="userDetails.newPassword" text='${bp.getMessage("userDetails_newPassword")}'/>
            <@spring.formInput path="userDetails.newPassword" attributes='class="form-control" placeholder="${bp.getMessage("userDetails_newPassword")}"' fieldType="password"/>
            <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
          </div>

          <#-- repeat new password -->
          <@spring.bind path="userDetails.newPasswordRepeat"/>
          <div class="form-group<#if spring.status.error> has-error</#if>">
            <@bp.labelFromSpring path="userDetails.newPasswordRepeat" text='${bp.getMessage("userDetails_newPasswordRepeat")}'/>
            <@spring.formInput path="userDetails.newPasswordRepeat" attributes='class="form-control" placeholder="${bp.getMessage("userDetails_newPasswordRepeat")}"' fieldType="password"/>
            <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
          </div>

          <div class="form-group text-right">
            <@bp.button text=bp.getMessage("userDetails_deleteProfile") attr={"type": "submit", "id": "deleteUser", "name": "_eventId_deleteUser", "classes": ["btn","cm-button--secondary", "pull-left"]} />
            <@bp.button text=bp.getMessage("userDetails_cancel")        attr={"type": "submit", "id": "cancel", "name": "_eventId_cancel","classes": ["btn", "cm-button--secondary"]} />
            <@bp.button text=bp.getMessage("userDetails_saveProfile")   attr={"type": "submit", "id": "saveUser", "classes": ["btn","btn-primary"]} />
          </div>
        </form>
      <#else>
        <@bp.notification type="error" text=bp.getMessage("userDetails_noUserFound") additionalClasses=["alert alert-danger"] />
      </#if>
    </div>
  </div>
</div>
