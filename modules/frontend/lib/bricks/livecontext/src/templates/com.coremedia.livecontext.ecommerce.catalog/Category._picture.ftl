<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.catalog.Category" -->
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />

<div class="${blockClass}__picture-box">
    <img class="${blockClass}__picture" <@cm.dataAttribute name="data-cm-non-adaptive-content" data={"overflow": true} /> src="${(self.thumbnailUrl)!""}" alt="${(self.name)!""}">
</div>
