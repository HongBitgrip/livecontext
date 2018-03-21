<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->

<#--
    Template Description:

    This template extends the brick generic-templates" for the detail view.
-->

<#assign blockClass=cm.localParameters().blockClass!"cm-details" />
<#assign relatedView=cm.localParameters().relatedView!"related" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#assign renderDate=cm.localParameter("renderDate", true) />
<#assign renderTags=cm.localParameter("renderTags", false) />
<#assign renderRelated=cm.localParameter("renderRelated", false) />
<#assign imageMapParams=bp.initializeImageMap()/>
<#assign useQuickinfo=cm.localParameter("imagemap_use_quickinfo", true)/>

<article class="${blockClass} ${blockClass}--imagemap cm-imagemap"<@cm.metadata self.content />
         data-cm-imagemap='{"coordsBaseWidth": "${bp.IMAGE_TRANSFORMATION_BASE_WIDTH}", "defaultLink": "${cm.getLink(self.target!cm.UNDEFINED)}"}'>

  <#-- title -->
  <h1 class="${blockClass}__headline"<@cm.metadata "properties.teaserTitle"/>>${self.teaserTitle!""}</h1>

  <#-- picture + hot zones -->
  <@cm.include self=self view="_picture" params={
    "blockClass": blockClass,
    "renderDimmer": false,
    "useQuickinfo": useQuickinfo
  } + imageMapParams
  />

  <#-- text -->
  <#if self.teaserText?has_content>
    <div class="${blockClass}__text cm-richtext"<@cm.metadata "properties.teaserText"/>>
      <@cm.include self=self.teaserText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- date -->
  <#if renderDate && self.externallyDisplayedDate?has_content>
    <div class="${blockClass}__date"<@cm.metadata "properties.externallyDisplayedDate"/>>
      <@bp.renderDate self.externallyDisplayedDate.time "${blockClass}__time" />
    </div>
  </#if>

  <#-- tags -->
  <#if renderTags>
    <@cm.include self=self view="_tagList"/>
  </#if>

  <#--include imagemap quickinfos -->
  <#if useQuickinfo>
    <@cm.include self=self view="_areasQuickInfo" params=imageMapParams/>
  </#if>
</article>

<#-- related -->
<#if renderRelated>
  <@cm.include self=self view="_related" params={"relatedView": relatedView}/>
</#if>

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />

