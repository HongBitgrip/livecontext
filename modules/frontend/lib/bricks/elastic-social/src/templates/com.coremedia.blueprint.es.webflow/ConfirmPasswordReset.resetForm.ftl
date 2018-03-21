<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="flowExecutionKey" scope="request" type="java.lang.String" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->

<div class="container">
  <div class="row">
    <div class="cm-form cm-form--resetpassword well col-xs-12 col-md-6 col-md-push-3"<@cm.metadata data=[self.action.content!"", "properties.id"]/>>
      <h1 class="cm-form__headline"><@bp.message "passwordReset_title" /></h1>
      <form method="post" data-cm-form--reset="">
        <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
        <input type="hidden" name="execution" value="${flowExecutionKey!""}">
        <input type="hidden" name="nextUrl" value="${nextUrl!""}">
        <input type="hidden" name="_eventId_submit">

        <@bp.notification type="info" title=bp.getMessage("confirmPasswordReset_title") />

        <#-- new password -->
        <@spring.bind path="bpPasswordReset.password"/>
        <div class="form-group<#if spring.status.error> has-error</#if>">
        <@bp.labelFromSpring path="bpPasswordReset.password" text='${bp.getMessage("confirmPasswordReset_password_label")}'/>
        <@spring.formInput path="bpPasswordReset.password" attributes='class="form-control" placeholder="${bp.getMessage("confirmPasswordReset_password_label")}" required' fieldType="password"/>
        <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
        </div>

        <#-- repeat new password -->
        <@spring.bind path="bpPasswordReset.confirmPassword"/>
        <div class="form-group<#if spring.status.error> has-error</#if>">
        <@bp.labelFromSpring path="bpPasswordReset.confirmPassword" text='${bp.getMessage("confirmPasswordReset_confirmPassword_label")}'/>
        <@spring.formInput path="bpPasswordReset.confirmPassword" attributes='class="form-control" placeholder="${bp.getMessage("confirmPasswordReset_confirmPassword_label")}" required' fieldType="password"/>
        <#if spring.status.error><span class="help-block">${spring.status.getErrorMessagesAsString("\n")}</span></#if>
        </div>

        <div class="form-group text-right">
          <@bp.button text="Reset" attr={"type": "submit", "classes": ["btn btn-primary"]} />
        </div>
      </form>
    </div>
  </div>
</div>
