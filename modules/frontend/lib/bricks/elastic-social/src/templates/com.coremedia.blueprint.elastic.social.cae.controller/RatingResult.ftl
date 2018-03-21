<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.controller.RatingResult" -->

<#if self.isEnabled()>
  <#assign ratingsId=bp.generateId("cm-ratings-") />
  <div class="cm-ratings" id="${ratingsId}" data-cm-refreshable-fragment='{"url": "${cm.getLink(self)}"}'>

    <#-- rating summary -->
    <#assign numberOfRatings=self.numberOfRatings!0 />
    <#if (numberOfRatings > 0)>
      <div class="cm-ratings__average-rating cm-ratings-average" itemscope="itemscope" itemtype="http://data-vocabulary.org/Rating-aggregate">

        <#assign averageRating=(self.getAverageRating())!0 />
        <#assign averageRatingRounded=averageRating?round />

        <div class="cm-ratings-average__header">
  
          <span class="cm-ratings-average__rating cm-rating">
            <#list es.getMaxRating()..1 as currentRating>
              <#assign classRatingIndicator="" />
              <#if currentRating == averageRatingRounded>
                <#assign classRatingIndicator=" cm-rating-indicator--active" />
              </#if>
                <div class="cm-rating__option cm-rating-indicator${classRatingIndicator}">${currentRating}</div>
            </#list>
          </span>
          <span class="cm-ratings-average__text" itemprop="rating" itemscope="itemscope" itemtype="http://data-vocabulary.org/Rating">
            <@bp.message es.messageKeys.RATINGS_AVERAGE_SYMBOL /> <span itemprop="average">${averageRating?string("0.##")}</span> <@bp.message es.messageKeys.RATINGS_AVERAGE_OUT_OF /> <span itemprop="best">${es.getMaxRating()}</span>
          </span>
          <span class="cm-ratings-average__votes">
            <#assign ratingsLabel="" />
            <#if (numberOfRatings == 1)>
              <#assign ratingsLabel=bp.getMessage(es.messageKeys.RATING_NUMBER_OF_RATINGS_SINGULAR) />
            <#else>
              <#assign ratingsLabel=bp.getMessage(es.messageKeys.RATING_NUMBER_OF_RATINGS) />
            </#if>
            (<span itemprop="votes">${numberOfRatings}</span> ${ratingsLabel})
          </span>
        </div>

    <#else>
      <#if !self.isReadOnly()>
        <h3 class="cm-ratings__title cm-heading3">
          <#assign numberOfRatings=self.getNumberOfRatings()!0 />
          <#switch numberOfRatings>
            <#case 0>
              <@bp.message es.messageKeys.RATINGS_NO_RATINGS />
              <#break>
            <#case 1>
              <@bp.message es.messageKeys.RATINGS_HEADLINE_SINGULAR />
              <#break>
            <#default>
              <@bp.message key=es.messageKeys.RATINGS_HEADLINE args=[numberOfRatings] />
          </#switch>
        </h3>
      </#if>
    </#if>

    <#-- write a rating -->
    <#if self.isWritingContributionsAllowed()>
      <#-- output of dynamic, non-rating specific information -->
      <@bp.notification type="inactive" text="" dismissable=false additionalClasses=["cm-ratings__notification"] attr={"data-cm-notification": '{"path": ""}'} />
      <#if (es.hasUserRated(self.getTarget()))!false>
        <div class="cm-ratings__toolbar cm-toolbar cm-toolbar--ratings">
          <@bp.button text=bp.getMessage(es.messageKeys.RATINGS_WRITE) attr={"class": "cm-button cm-button--small cm-button--rating cm-button--disabled"} />
        </div>
        <@bp.notification type="info" text=bp.getMessage(es.messageKeys.RATING_FORM_ALREADY_RATINGED) dismissable=false additionalClasses=["cm-ratings__notification"] attr={"data-cm-ratings-notification-type": "ALREADY_RATED"} />
      <#else>
        <div class="cm-ratings__toolbar cm-toolbar cm-toolbar--ratings">
          <@bp.button text=bp.getMessage(es.messageKeys.RATINGS_WRITE) attr={"class": "cm-button cm-button--small cm-button--rating", "data-cm-button--rating": ''} />
        </div>
        <@cm.include self=self view="ratingForm" />
      </#if>
    <#elseif self.isWritingContributionsEnabled() && es.isAnonymousUser()>
      <@bp.notification type="info" text=bp.getMessage(es.messageKeys.RATING_FORM_NOT_LOGGED_IN) dismissable=false additionalClasses=["cm-ratings__notification"] attr={"data-cm-ratings-notification-type": "LOGIN_REQUIRED"} />
      <div class="cm-ratings__toolbar cm-toolbar cm-toolbar--ratings">
        <@cm.include self=es.getLogin()!cm.UNDEFINED view="asButton" />
      </div>
    </#if>

  </div>
</#if>
