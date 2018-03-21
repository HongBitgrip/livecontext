<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="entry.link" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="entry.startTimeMillis" type="java.lang.Integer" -->


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
  <div class="cm-shoppable__video"<@cm.metadata self.content />>
    <@cm.include self=self view="video" />
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
  <#-- open default hero teaser without shoppable extras -->
  <@cm.include self=self view="hero[]" />
  <@bp.notification type="error" text="This is a shoppable video without timeline entries." />
</#if>
