<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="isRoot" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="navPathList" type="java.util.List" -->

<#assign cssClass=cm.localParameters().cssClass!""/>
<#assign isRoot=(isRoot!true)/>

<#if isRoot || (!((self.hidden)!false))>

  <#if (bp.isActiveNavigation(self, navPathList![]))>
    <#assign cssClass= cssClass + ' active'/>
  </#if>

<li class="${cssClass}">
<#-- link to this item in navigation -->
    <@cm.include self=self view="asLink"/>
</li>
</#if>
