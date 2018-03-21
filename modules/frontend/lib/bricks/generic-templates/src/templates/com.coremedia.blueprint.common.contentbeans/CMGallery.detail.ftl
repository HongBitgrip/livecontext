<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMGallery" -->

<#assign blockClass=cm.localParameter("blockClass", "cm-details") />
<#assign itemsClass=cm.localParameter("itemsClass", "") />
<#assign relatedView=cm.localParameters().relatedView!"related" />
<#assign carouselParams=cm.localParameters().carouselParams!{} />
<#assign viewItems=cm.localParameters().viewItems!"asTeaser" />
<#assign renderDate=cm.localParameter("renderDate", true) />
<#assign renderTags=cm.localParameter("renderTags", true) />
<#assign renderRelated=cm.localParameter("renderRelated", true) />

<#assign carouselParams=carouselParams + {"additionalClass": "${blockClass}__medias", "viewItems": "_header", "modifier": "details"}/>

<article class="${blockClass}"<@cm.metadata self.content />>

  <#-- title -->
  <h1 class="${blockClass}__headline"<@cm.metadata "properties.title"/>>${self.title!""}</h1>

  <#-- media -->
  <@cm.include self=bp.getContainer(self.items) view="asGrid" params={"itemsPerRow": 3, "viewItems": "${viewItems}", "additionalClass": "cm_details__media ${itemsClass}"} />

  <#-- text -->
  <#if self.detailText?has_content>
    <div class="${blockClass}__text cm-richtext"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
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
</article>

<#-- related -->
<#if renderRelated>
  <@cm.include self=self view="_related" params={"relatedView": relatedView}/>
</#if>

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
