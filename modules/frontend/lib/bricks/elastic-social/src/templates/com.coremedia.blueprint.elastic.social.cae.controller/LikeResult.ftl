<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.controller.LikeResult" -->

<#if self.isEnabled()>
  <#assign likeId=bp.generateId("cm-like-") />
  <div class="cm-like" id="${likeId}" data-cm-refreshable-fragment='{"url": "${cm.getLink(self)}"}'>
      <form method="post" enctype="multipart/form-data" class="cm-new-like__form cm-form" action="${cm.getLink(self)}" data-cm-es-ajax-form=''>
         <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
        <@bp.notification type="inactive" text="" dismissable=false attr={"data-cm-notification": '{"path": ""}'} />
        <#if self.hasLiked()>
            <h3 class="cm-like__title cm-heading3">has liked of  likes ${self.numberOfLikes}</h3>
            <input type="hidden" name="like" value="false">
        <#else>
            <h3 class="cm-like__title cm-heading3">has not liked of likes ${self.numberOfLikes}</h3>
            <input type="hidden" name="like" value="true">
        </#if>
        <@bp.button text="Like" iconClass="icon-checkmark" attr={"type": "submit", "class": "cm-button cm-button--small"} />
      </form>
  </div>
</#if>
