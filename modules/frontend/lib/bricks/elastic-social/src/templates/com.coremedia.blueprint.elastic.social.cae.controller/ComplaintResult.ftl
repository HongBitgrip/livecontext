<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.controller.ComplaintResult" -->

<#if self.isEnabled()>
  <#assign complaintId=bp.generateId("cm-complaint-") />
  <div class="cm-complaints" id="${complaintId}" data-cm-refreshable-fragment='{"url": "${cm.getLink(self)}"}'>
      <form method="post" enctype="multipart/form-data" class="cm-new-complaint__form cm-form" action="${cm.getLink(self)}" data-cm-es-ajax-form=''>
         <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
        <@bp.notification type="inactive" text="" dismissable=false attr={"data-cm-notification": '{"path": ""}'} />
        <#if self.hasAlreadyComplained()>
            <h3 class="cm-comments__title cm-heading3">has complained</h3>
            <input type="hidden" name="complain" value="false">
        <#else>
            <h3 class="cm-comments__title cm-heading3">has not complained</h3>
            <input type="hidden" name="complain" value="true">
        </#if>
        <@bp.button text=bp.getMessage(es.messageKeys.COMPLAINT_FORM_LABEL_SUBMIT) iconClass="icon-checkmark" attr={"type": "submit", "class": "cm-button cm-button--small"} />
      </form>
  </div>
</#if>
