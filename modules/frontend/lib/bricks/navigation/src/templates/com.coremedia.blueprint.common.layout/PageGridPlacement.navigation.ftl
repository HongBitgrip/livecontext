<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="childrenCssClass" type="java.lang.String" -->

<#assign cssClass=cm.localParameters().cssClass!""/>
<#assign childrenCssClass=cm.localParameters().childrenCssClass!""/>

<#if (cmpage.navigation.rootNavigation)?has_content>
  <li id="cm-navigation" class="cm-navigation">
  <@cm.include self=cmpage.navigation.rootNavigation view="asLinkList" params={
  "maxDepth": bp.setting(cmpage, "navigation_depth", 3),
  "isTopLevel": true,
  "cssClass": cssClass,
  "childrenCssClass": childrenCssClass
  } />
  </li>
</#if>
