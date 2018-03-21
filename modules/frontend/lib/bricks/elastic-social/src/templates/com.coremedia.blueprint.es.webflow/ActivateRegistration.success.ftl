<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="flowRequestContext" type="org.springframework.webflow.execution.RequestContext" -->

<div class="container">
  <div class="row">
    <div class="cm-form cm-form--success col-xs-12 col-md-8 col-md-push-2"<@cm.metadata data=[self.action.content!"", "properties.id"]/>>
      <#if flowRequestContext?has_content && flowRequestContext.messageContext?has_content>
        <#list flowRequestContext.messageContext.allMessages![] as message>
          <@bp.notification type="success" title=bp.getMessage("activateRegistration_success_title") text=message.text!"" additionalClasses=["alert alert-success"] />
        </#list>
      </#if>
    </div>
  </div>
</div>
