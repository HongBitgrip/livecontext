<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.controller.CommentsResult" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<div class="cm-comments__new-comment cm-new-comment">
  <form method="post" enctype="multipart/form-data" class="cm-new-comment__form cm-form" action="${cm.getLink(self)}" data-cm-es-ajax-form=''>
    <@bp.notification type="inactive" text="" dismissable=false additionalClasses=["cm-form__notification"] attr={"data-cm-notification": '{"path": ""}'} />

    <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
    <input type="hidden" name="replyTo" value="">
    <fieldset class="cm-form__fieldset cm-fieldset">
      <div class="cm-fieldset__item cm-field cm-field--detail">
        <@bp.notification type="inactive" text="" dismissable=false additionalClasses=["cm-field__notification"] attr={"data-cm-notification": '{"path": "comment"}'} />
        <#assign idText=bp.generateId("cm-new-comment__textarea-") />
        <label for="${idText}" class="cm-field__name"><@bp.message es.messageKeys.COMMENT_FORM_LABEL_TEXT /></label>
        <textarea name="comment" class="cm-field__value cm-textarea" id="${idText}" required="" placeholder="${bp.getMessage(es.messageKeys.COMMENT_FORM_ERROR_COMMENT_BLANK"commentForm-error-commentBlank")}"></textarea>
      </div>
      <div class="cm-fieldset__item cm-button-group cm-button-group--default">
        <@bp.button text=bp.getMessage(es.messageKeys.COMMENT_FORM_LABEL_HIDE) attr={"type": "button", "classes": ["btn", "cm-button-group__button", "cm-button--secondary"], "data-cm-button--cancel": ""} />
        <@bp.button text=bp.getMessage(es.messageKeys.COMMENT_FORM_LABEL_SUBMIT) attr={"type": "submit", "classes": ["btn", "cm-button-group__button"], "data-cm-button--submit": ""} />
      </div>
    </fieldset>
  </form>
</div>
