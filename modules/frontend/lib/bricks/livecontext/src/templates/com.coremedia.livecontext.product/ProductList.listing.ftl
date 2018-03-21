<#-- @ftlvariable name="self" type="com.coremedia.livecontext.product.ProductList" -->
<#-- @ftlvariable name="rootChannel" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->

<#if self.category?has_content && (cmpage.navigation.rootNavigation)?has_content>
<#-- in blueprint rootNavigation declared as Navigation is instance of CMChannel -->
  <#assign rootChannel=cmpage.navigation.rootNavigation />

  <#if self.isProductCategory() && self.loadedProducts?has_content>
    <@cm.include self=bp.getContainer(self.loadedProducts) view="asGrid" params={"itemsPerRow": 4, "itemsPerMobileRow":2, "additionalClass": "cm-collection--productlisting", "viewItems": "asCategoryItem", "center": false} />
  </#if>
</#if>
