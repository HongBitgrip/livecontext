<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="entry.link" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="entry.startTimeMillis" type="java.lang.Integer" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign renderTeaserText=cm.localParameter("renderTeaserText", false) />

<#assign timelineEntries=self.timeLineSequences![] />
<#assign overlay={
"displayTitle": true,
"displayShortText": true,
"displayPicture": true,
"displayDefaultPrice": true,
"displayDiscountedPrice": true,
"displayOutOfStockLink": true
} />

<#if (timelineEntries?size > 0 || self.timeLineDefaultTarget?has_content)>
<div class="cm-shoppable cm-container">
  <#-- video on the left -->
  <div class="${blockClass} ${cssClasses} ${blockClass}--video cm-shoppable__video ${additionalClass}" data-cm-teasable--video='{"preview": ".cm-teaser__content", "player": ".${blockClass}--video__video", "play": ".cm-play-button"}'<@cm.metadata (metadata![]) + [self.content] />>
    <div class="${blockClass}__wrapper">
      <#assign ownPictureCssClass="" />
      <#if self.picture?has_content>
        <#assign ownPictureCssClass="cm-hidden" />
        <@cm.include self=self.picture params={
        "limitAspectRatios": [ "portrait_ratio1x1", "landscape_ratio16x9" ],
        "classBox": "cm-teaser__content cm-shoppable__content cm-aspect-ratio-box",
        "classImage": "cm-aspect-ratio-box__content",
        "metadata": ["properties.pictures"]
        }/>
      </#if>

    <#-- play overlay icon-->
      <@cm.include self=self view="_playButton" params={"blockClass": "${blockClass}"}/>

      <div class="${blockClass}__caption">
      <#-- teaser title -->
        <#if self.teaserTitle?has_content>
          <h3 class="${blockClass}__headline" <@cm.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""}</span>
          </h3>
        </#if>
      <#-- teaser text -->
        <#if renderTeaserText && self.teaserText?has_content>
          <p class="${blockClass}__text" <@cm.metadata "properties.teaserText" />>
            <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "square.max.length", 115)) />
          </p>
        </#if>
      </div>

      <div class="${blockClass}--video__video cm-shoppable__player cm-aspect-ratio-box ${ownPictureCssClass}">
        <@cm.include self=self view="video" params={"classVideo": "cm-aspect-ratio-box__content", "hideControls": false, "shoppableVideo": true} />
      </div>
    </div>
  </div>
<#-- teaser on the right -->
  <div class="cm-shoppable__teasers"<@cm.metadata "properties.timeLine" />>
  <#-- default teaser -->
    <#if self.timeLineDefaultTarget?has_content>
      <div class="cm-shoppable__teaser cm-shoppable__default">
        <@cm.include self=self.timeLineDefaultTarget!cm.UNDEFINED view="asQuickInfo" params={
        "classQuickInfo":"cm-quickinfo--shoppable",
        "overlay": overlay
        }/>
      </div>
    </#if>
  <#-- list all timeline teaser -->
    <#if (timelineEntries?size > 0)>
      <#list timelineEntries as entry>
        <#if entry.startTimeMillis?has_content && entry.link?has_content>
          <div class="cm-shoppable__teaser" data-cm-video-shoppable-time="${entry.startTimeMillis}">
            <@cm.include self=entry.link view="asQuickInfo" params={
            "classQuickInfo":"cm-quickinfo--shoppable",
            "overlay": overlay
            }/>
          </div>
        </#if>
      </#list>
    </#if>
  </div>
</div>
<#else>
  <@cm.include self=self view="teaser[]" />
  <@bp.notification type="error" text="This is a shoppable video without timeline entries." />
</#if>
