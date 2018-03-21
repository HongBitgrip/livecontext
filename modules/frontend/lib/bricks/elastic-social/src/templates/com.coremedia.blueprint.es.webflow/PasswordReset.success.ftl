<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="emailAddress" type="java.lang.String" -->

<div class="container">
  <div class="row">
    <div class="cm-form cm-form--success col-xs-12 col-md-8 col-md-push-2"<@cm.metadata data=[self.action.content!"", "properties.id"]/>>
      <@bp.notification type="success" title=bp.getMessage("passwordReset_title") text=bp.getMessage("passwordReset_success", [emailAddress!""]) additionalClasses=["alert alert-success"] />
    </div>
  </div>
</div>
