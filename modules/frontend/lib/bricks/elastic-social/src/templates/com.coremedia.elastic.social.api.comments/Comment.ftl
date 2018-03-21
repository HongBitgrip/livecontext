<#-- @ftlvariable name="self" type="com.coremedia.elastic.social.api.comments.Comment" -->
<#-- @ftlvariable name="commentView" type="java.lang.String" -->
<#-- @ftlvariable name="commentingAllowed" type="java.lang.Boolean" -->

<#assign commentView=es.getCommentView(self) />

<div class="cm-comment" data-cm-comment-id="${self.id}"<@cm.metadata self/>>

  <#assign strAuthorName=self.authorName!"" />
  <#assign strDate=self.creationDate?datetime?string.long_short />
  <#if !strAuthorName?has_content>
    <#if !self.author?has_content || es.isAnonymous(self.author)>
      <#assign strAuthorName=bp.getMessage(es.messageKeys.COMMENT_AUTHOR_ANONYMOUS) />
    <#else>
      <#assign strAuthorName=(self.author.name)!"" />
    </#if>
  </#if>
  <#if ["default", "undecided", "rejected"]?seq_contains(commentView)>
    <div class="cm-comment__header">
      <span class="cm-comment__author-date">
        <@bp.message es.messageKeys.COMMENT_AUTHOR_BY /> <span class="cm-comment__author">${strAuthorName!""}</span>
        <span class="cm-comment__date">${strDate}</span>
      </span>
    </div>
  </#if>
  <#-- output of comment specific information -->
  <#-- At least one dynamic notification is rendered -->
  <#if ["undecided"]?seq_contains(commentView)>
    <@bp.notification type="info" text=bp.getMessage(es.messageKeys.COMMENT_APPROVAL_UNDECIDED) dismissable=false additionalClasses=["cm-comment__notification"] attr={"data-cm-notification": '{"path": ""}', "data-cm-contribution-notification-type": "UNDECIDED"} />
  <#elseif ["rejected"]?seq_contains(commentView)>
    <@bp.notification type="warning" text=bp.getMessage(es.messageKeys.COMMENT_APPROVAL_REJECTED) dismissable=false additionalClasses=["cm-comment__notification"] attr={"data-cm-notification": '{"path": ""}', "data-cm-contribution-notification-type": "REJECTED"} />
  <#elseif ["deleted"]?seq_contains(commentView)>
    <div class="cm-comment__deleted">
      <@bp.message es.messageKeys.COMMENT_DELETED />
    </div>
  <#else>
    <@bp.notification type="inactive" text="" dismissable=false additionalClasses=["cm-comment__notification"] attr={"data-cm-notification": '{"path": ""}'} />
  </#if>
  <#if ["default", "undecided", "rejected"]?seq_contains(commentView)>
    <div class="cm-comment__text cm-readmore" data-cm-readmore='{"lines": 5, "text": "${bp.getMessage(es.messageKeys.COMMENT_MORE)}"}'>
      <div class="cm-readmore__wrapper">
        ${self.textAsHtml?no_esc}
      </div>
      <div class="cm-readmore__buttonbar">
        <@bp.button baseClass="" text=bp.getMessage(es.messageKeys.COMMENT_MORE) attr={"class": "cm-readmore__button-more"} />
        <@bp.button baseClass="" text=bp.getMessage(es.messageKeys.COMMENT_LESS) attr={"class": "cm-readmore__button-less"} />
      </div>
    </div>
  </#if>
  <#if ["default", "undecided"]?seq_contains(commentView) && commentingAllowed!false>
    <div class="cm-comment__toolbar cm-toolbar cm-toolbar--comments">
      <@bp.button text=bp.getMessage(es.messageKeys.COMMENT_FORM_LABEL_REPLY) iconClass="icon-pencil" attr={"data-cm-button--comment": '{"replyTo": "${self.id}"}'} />
      <@bp.button text=bp.getMessage(es.messageKeys.COMMENT_FORM_LABEL_QUOTE) iconClass="icon-quotes" attr={"data-cm-button--comment": '{"replyTo": "${self.id}", "quote": {"author": "${(strAuthorName!"")?json_string}", "date": "${strDate?json_string}", "text": "${self.text?json_string}"}}'} />
    </div>
  </#if>

</div>
