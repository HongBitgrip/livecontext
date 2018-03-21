<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->


<div class="cm-reviews__new-review cm-new-review">
  <form method="post" enctype="multipart/form-data" class="cm-new-review__form cm-form" action="${cm.getLink(self)}" data-cm-es-ajax-form=''>

    <@bp.notification type="inactive" text="" dismissable=false additionalClasses=["cm-form__notification"] attr={"data-cm-notification": '{"path": ""}'} />

    <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
    <input type="hidden" name="replyTo" value="">
    <fieldset class="cm-form__fieldset cm-fieldset">

      <div class="cm-fieldset__item cm-field">
        <@bp.notification type="inactive" text="" dismissable=false additionalClasses=["cm-field__notification"] attr={"data-cm-notification": '{"path": "rating"}'} />
        <div class="cm-field__value cm-rating">
          <legend>Rating</legend>
          <#-- render all ratings in reversed order -->
          <#list es.getReviewMaxRating()..1 as currentRating>
            <#assign radioAttr="" />
            <#assign radioId=bp.generateId("cm-review-rating-indicator-") />
            <input name="rating" value="${currentRating}" id="${radioId}" type="radio"${radioAttr}><label for="${radioId}" class="cm-rating__option cm-rating-indicator"></label>
          </#list>
        </fieldset>
      </div>
      <div class="cm-fieldset__item cm-button-group cm-button-group--default">
        <@bp.button text=bp.getMessage(es.messageKeys.REVIEW_FORM_LABEL_HIDE) attr={"type": "button", "classes": ["btn", "cm-button-group__button", "cm-button--secondary"], "data-cm-button--cancel": ""} />
        <@bp.button text=bp.getMessage(es.messageKeys.REVIEW_FORM_LABEL_SUBMIT) attr={"type": "submit", "classes": ["btn", "cm-button-group__button"], "data-cm-button--submit": ""} />
      </div>
    </fieldset>
  </form>
</div>
