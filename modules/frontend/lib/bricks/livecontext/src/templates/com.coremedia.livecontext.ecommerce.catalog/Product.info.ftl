<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.catalog.Product" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classPrice" type="java.lang.String" -->

<div class="cm-product-info ${classBox!""}">
  <#-- headline -->
  <div class="cm-product-info__title">
    <h4 class="cm-heading4">${self.name!""}</h4>
  </div>
  <#-- price -->
  <div class="cm-product-info__pricing">
    <@cm.include self=self view="pricing" params={
      "classListPrice": "cm-product-info__listprice " + classPrice!"",
      "classOfferPrice": "cm-product-info__offerprice " + classPrice!""
    } />
  </div>
</div>