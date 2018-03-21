<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="isRoot" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->

<#assign cssClass=cm.localParameters().cssClass!""/>
<#assign isRoot=(isRoot!true)/>

<#if isRoot || (!((self.hidden)!false))>

  <#-- add css class active, if this item is part of the active navigation -->
  <#if (bp.isActiveNavigation(self, (cmpage.navigation.navigationPathList)![]))>
    <#assign cssClass= cssClass + ' active'/>
  </#if>

  <li class="${cssClass}">
    <#-- link to this item in navigation -->
    <@cm.include self=self view="asLink"/>
    <#-- include child items, if exist-->
    <#if self.visibleChildren?has_content>
      <@cm.include self=self view="asLinkList" params={"isRoot": false}/>
    </#if>
  </li>
</#if>
