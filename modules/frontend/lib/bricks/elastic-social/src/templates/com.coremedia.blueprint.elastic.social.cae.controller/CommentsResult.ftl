<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.controller.CommentsResult" -->

<#if self.isEnabled()>
  <#assign commentsId=bp.generateId("cm-comments-") />
<div class="cm-comments" id="${commentsId}" data-cm-refreshable-fragment='{"url": "${cm.getLink(self)}"}'>
  <#assign numberOfContributions=(self.getNumberOfContributions())!0 />
  <#if (numberOfContributions != 0) || (!self.isReadOnly())>
      <h3 class="cm-comments__title cm-heading3">
        <#switch numberOfContributions>
          <#case 0>
          <@bp.message es.messageKeys.COMMENTS_NO_COMMENTS />
          <#break>
          <#case 1>
            <@bp.message es.messageKeys.COMMENTS_HEADLINE_SINGULAR />
            <#break>
          <#default>
            <@bp.message key=es.messageKeys.COMMENTS_HEADLINE args=[numberOfContributions] />
        </#switch>
      </h3>
  </#if>

  <#if self.isWritingContributionsAllowed()>
  <#-- output of dynamic, non-comment specific information -->
    <@bp.notification type="inactive" text="" dismissable=false additionalClasses=["cm-comments__notification"] attr={"data-cm-notification": '{"path": ""}'} />

      <div class="cm-comments__toolbar cm-toolbar cm-toolbar--comments">
        <@bp.button text=bp.getMessage(es.messageKeys.COMMENTS_WRITE) attr={"data-cm-button--comment": '{"replyTo": ""}'} />
      </div>
  <#elseif self.isWritingContributionsEnabled() && es.isAnonymousUser()>
    <@bp.notification type="info" text=bp.getMessage(es.messageKeys.COMMENT_FORM_NOT_LOGGED_IN) dismissable=false additionalClasses=["cm-comments__notification"] attr={"data-cm-comments-notification-type": "LOGIN_REQUIRED"} />
      <div class="cm-comments__toolbar cm-toolbar cm-toolbar--comments">
        <@cm.include self=es.getLogin()!cm.UNDEFINED view="asButtonGroup" />
      </div>
  </#if>
  <@cm.include self=self view="commentForm" />

  <#assign comments=self.rootComments />
  <#if comments?has_content>
      <ul class="cm-collection cm-collection--comments cm-comments__list">
        <#list comments as wrapper>
            <@cm.include self=wrapper!cm.UNDEFINED view="asListItem" params={"commentsResult": self} />
        </#list>
      </ul>
  </#if>
</div>
</#if>
