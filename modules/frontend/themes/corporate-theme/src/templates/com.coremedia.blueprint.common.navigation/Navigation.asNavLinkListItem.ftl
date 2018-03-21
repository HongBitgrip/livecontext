<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="isRoot" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->

<#assign cssClass=cm.localParameters().cssClass!""/>
<#assign isRoot=(isRoot!true)/>
<#if isRoot>
  <#assign cssClass="cm-megamenu__item col-xs-12 col-md-4"/>
  <#assign linkCssClass="cm-megamenu__title"/>
<#else>
  <#assign cssClass="cm-menu__item"/>
  <#assign linkCssClass="cm-menu__title"/>
</#if>

<#if isRoot || (!((self.hidden)!false))>

  <#-- add css class active, if this item is part of the active navigation -->
  <#if (bp.isActiveNavigation(self, (cmpage.navigation.navigationPathList)![]))>
    <#assign cssClass=cssClass + ' active'/>
  </#if>

  <li class="${cssClass}">
    <#-- link to this item in navigation -->
    <@cm.include self=self view="asLink" params={"cssClass": linkCssClass}/>
    <#-- include child items, if exist-->
    <#if self.visibleChildren?has_content>
      <@cm.include self=self view="asNavLinkList" params={"isRoot": false}/>
    </#if>
  </li>
</#if>
