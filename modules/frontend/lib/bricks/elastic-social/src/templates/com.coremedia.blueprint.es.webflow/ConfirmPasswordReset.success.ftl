<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->

<div class="container">
  <div class="row">
    <div class="cm-form cm-form--success col-xs-12 col-md-8 col-md-push-2"<@cm.metadata data=[self.action.content!"", "properties.id"]/>>
      <@bp.notification type="success" title=bp.getMessage("confirmPasswordReset_success_title") additionalClasses=["alert alert-success"] />
    </div>
  </div>
</div>
