<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.asset.CatalogPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.lang.String" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classImage" type="java.lang.String" -->
<#-- @ftlvariable name="overflow" type="java.lang.Boolean" -->
<#assign limitAspectRatios=cm.localParameters().limitAspectRatios![] />
<#assign classBox=cm.localParameters().classBox!"" />
<#assign classImage=cm.localParameters().classImage!"" />
<#assign overflow=cm.localParameters().overflow!true />

<#if self.picture?has_content>
  <@cm.include self=bp.createBeanFor(self.picture) params={
    "limitAspectRatios": limitAspectRatios,
    "classBox": classBox,
    "classImage": classImage
  }/>
<#else>
  <div class="${classBox}">
    <img class="${classImage}" <@cm.dataAttribute name="data-cm-non-adaptive-content" data={"overflow": overflow} /> src="${self.url!""}">
  </div>
</#if>
