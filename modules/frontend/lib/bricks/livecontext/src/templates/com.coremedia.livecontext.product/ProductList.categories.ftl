<#-- @ftlvariable name="self" type="com.coremedia.livecontext.product.ProductList" -->

<#if self.category?has_content>
<div class="cm-category">
  <#if self.hasCategories()&&  self.navigation?has_content>
    <@cm.include self=bp.getContainer(self.subCategoriesInSite) view="asGrid" params={"itemsPerRow": 6, "additionalClass": "cm-collection--categories", "center": false} />
  </#if>
</div>
</#if>