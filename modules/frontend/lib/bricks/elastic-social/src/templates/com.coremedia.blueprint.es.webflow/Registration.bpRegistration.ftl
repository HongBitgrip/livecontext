<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->
<#-- @ftlvariable name="elasticSocialConfiguration" type="com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration" -->
<#-- @ftlvariable name="registrationFlow" type="com.coremedia.blueprint.elastic.social.cae.flows.Registration" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage)/>
<#assign termsOfUseLink=cm.getLink(bp.setting(cmpage, "linkTermsOfUse"))/>
<#assign privacyPolicyLink=cm.getLink(bp.setting(cmpage, "linkTermsOfUse"))/>
<#assign registrationAction=self.registrationAction!cm.UNDEFINED />
<#assign registrationFlow=bp.substitute(registrationAction.id!"", registrationAction)!cm.UNDEFINED />

<div class="container">
  <div class="row">
    <div class="cm-form cm-form--registration well col-xs-12 col-md-6 col-md-push-3"<@cm.metadata data=[registrationAction.content!"", "properties.id"]/>>
      <h1 class="cm-form__headline"><@bp.message "registration_register" /></h1>
      <form method="post" enctype="multipart/form-data" data-cm-form--registration=""${(elasticSocialConfiguration.recaptchaForRegistrationRequired!false)?then(' onsubmit="substituteRecaptchaBindElement();"'?no_esc,'')}>
        <input type="hidden" name="execution" value="${flowExecutionKey!""}">
        <#--<input type="hidden" name="tenant" value="${elasticSocialConfiguration.tenant!""}">-->
        <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
        <input type="hidden" name="_eventId_submit">
        <@spring.bind path="bpRegistration.timeZoneId"/>
        <input type="hidden" name="timeZoneId" id="timezone" value="${spring.stringStatusValue}">

        <#-- notification-->
        <@bp.notificationFromSpring path="bpRegistration" additionalClasses=["alert alert-danger"] />

        <#-- username -->
        <@spring.bind path="bpRegistration.username"/>
        <div class="form-group<#if spring.status.error> has-error</#if>">
          <@bp.labelFromSpring path="bpRegistration.username" text='${bp.getMessage("registration_username_label")}'/>
          <@spring.formInput path="bpRegistration.username" attributes='class="form-control" placeholder="${bp.getMessage("registration_username_label")}" required'/>
          <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
        </div>

        <#-- given name -->
        <@spring.bind path="bpRegistration.givenname"/>
        <div class="form-group<#if spring.status.error> has-error</#if>">
          <@bp.labelFromSpring path="bpRegistration.givenname" text='${bp.getMessage("registration_givenname_label")}'/>
          <@spring.formInput path="bpRegistration.givenname" attributes='class="form-control" placeholder="${bp.getMessage("registration_givenname_label")}" required'/>
          <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
        </div>

        <#-- surname -->
        <@spring.bind path="bpRegistration.surname"/>
        <div class="form-group<#if spring.status.error> has-error</#if>">
          <@bp.labelFromSpring path="bpRegistration.surname" text='${bp.getMessage("registration_surname_label")}'/>
          <@spring.formInput path="bpRegistration.surname" attributes='class="form-control" placeholder="${bp.getMessage("registration_surname_label")}" required'/>
          <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
        </div>

        <#-- email -->
        <@spring.bind path="bpRegistration.emailAddress"/>
        <div class="form-group<#if spring.status.error> has-error</#if>">
          <@bp.labelFromSpring path="bpRegistration.emailAddress" text='${bp.getMessage("registration_emailAddress_label")}'/>
          <@spring.formInput path="bpRegistration.emailAddress" attributes='class="form-control" placeholder="${bp.getMessage("registration_emailAddress_label")}" required' fieldType="email"/>
          <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
        </div>

        <#-- password -->
        <#if !(registrationFlow.registeringWithProvider!false)>
          <@spring.bind path="bpRegistration.password"/>
          <div class="form-group<#if spring.status.error> has-error</#if>">
            <@bp.labelFromSpring path="bpRegistration.password" text='${bp.getMessage("registration_password_label")}'/>
            <@spring.formInput path="bpRegistration.password" attributes='class="form-control" placeholder="${bp.getMessage("registration_password_label")}" required' fieldType="password"/>
            <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
          </div>

          <@spring.bind path="bpRegistration.confirmPassword"/>
          <div class="form-group<#if spring.status.error> has-error</#if>">
            <@bp.labelFromSpring path="bpRegistration.confirmPassword" text='${bp.getMessage("registration_confirmPassword_label")}'/>
            <@spring.formInput path="bpRegistration.confirmPassword" attributes='class="form-control" placeholder="${bp.getMessage("registration_confirmPassword_label")}" required' fieldType="password"/>
            <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
          </div>
        </#if>

        <#-- user image -->
        <@spring.bind path="bpRegistration.profileImage"/>
        <div class="form-group<#if spring.status.error> has-error</#if>">
          <label for="imageFile"><@bp.message "registration_imageFile_label" /></label>
          <input name="imageFile" type="file" value="" accept="image/*">
          <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
        </div>

        <#if registration?has_content && bpRegistration.profileImage?has_content>
          <#assign imageUrl=cm.getLink(bpRegistration.profileImage)/>
          <div>
            <img src="${imageUrl!""}" alt="userimage"><br>
            <@spring.formCheckbox bp.getMessage("registration_deleteProfileImage") />
            <label for="deleteProfileImage"><@bp.message "registration_deleteProfileImage" /></label>
          </div>
        </#if>

        <#-- recaptcha -->
        <#if elasticSocialConfiguration.recaptchaForRegistrationRequired!false>
          <@spring.bind path="bpRegistration.${elasticSocialConfiguration.recaptchaResponseParamWebflow}"/>
          <div class="form-group<#if spring.status.error> has-error</#if>">
            <script src="https://www.google.com/recaptcha/api.js" async defer></script>
            <div class="g-recaptcha" data-sitekey="${elasticSocialConfiguration.recaptchaPublicKey!""}"></div>
            <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
          </div>
          <script>
            <#-- This is important since it would break Spring form bindings otherwise!
              Do not delete or change this line or at least make sure you know what you are doing.
              The problem ist that recaptcha writes response to an element with id and name "g-recaptcha-response"
              Spring binding unfortunately cannot bind elements that have names containing "-"
              So we have to substitute the elements name to enable spring binding before clicking submit -->
            function substituteRecaptchaBindElement() {
              document.getElementById('${elasticSocialConfiguration.recaptchaResponseParam}').name = '${elasticSocialConfiguration.recaptchaResponseParamWebflow}';
            }
          </script>
        </#if>

        <#-- terms of use -->
        <@spring.bind path="bpRegistration.acceptTermsOfUse"/>
        <#outputformat "plainText">
          <#assign privacyPolicy><a href="${privacyPolicyLink!""}" target="_blank"><@bp.message "registration_linkPrivacyPolicy_label" /></a></#assign>
          <#assign termsOfUse><a href="${termsOfUseLink!""}" target="_blank"><@bp.message "registration_linkTermsOfUse_label" /></a></#assign>
          <#assign text=bp.getMessage("registration_acceptTermsOfUse_label", ["#privacyPolicy#", "#termsOfUse#"])/>
        </#outputformat>
        <div class="form-group<#if spring.status.error> has-error</#if>">
          <div class="checkbox">
            <label>
              <@spring.bind path="bpRegistration.acceptTermsOfUse"/>
              <@spring.formCheckbox path="bpRegistration.acceptTermsOfUse"/>
              <#noautoesc>${text?replace("#privacyPolicy#", privacyPolicy)?replace("#termsOfUse#", termsOfUse)}</#noautoesc>
            </label>
          </div>
          <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
        </div>

        <#-- button-->
        <div class="form-group text-right">
          <@bp.button text=bp.getMessage("registration_title") attr={"type": "submit", "classes": ["btn","btn-primary"]} />
        </div>
      </form>
    </div>
  </div>
</div>
