<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="isInSidebar" type="java.lang.Boolean" -->

<#assign isInSidebar=cm.localParameter("isInSidebar", false) />
<#assign viewItemCssClass=cm.localParameter("viewItemCssClass", "") />
<#assign columnCssClass=""/>
<#if isInSidebar>
  <#assign columnCssClass="col-md-12 col-md-offset-0 " />
</#if>

<@cm.include self=self view="asGrid" params={"viewItems": "asClaim", "columnCssClass": columnCssClass, "viewItemCssClass": viewItemCssClass, "additionalClass": "cm-container--claim"} />
