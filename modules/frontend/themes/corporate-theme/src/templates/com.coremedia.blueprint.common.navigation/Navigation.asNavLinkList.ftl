<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="childrenCssClass" type="java.lang.String" -->
<#-- @ftlvariable name="maxDepth" type="java.lang.Integer" -->
<#-- @ftlvariable name="isRoot" type="java.lang.Boolean" -->

<#assign isRoot=(isRoot!true)/>
<#-- set css classes -->
<#assign cssClass=cm.localParameters().cssClass!""/>
<#if isRoot>
  <#assign cssClass="cm-megamenu nav navbar navbar-nav row"/>
<#else>
  <#assign cssClass="cm-menu"/>
</#if>
<#assign childrenCssClass=cm.localParameters().childrenCssClass!""/>

<#-- check if navigation has visible children and max tree depth isn't reached yet -->
<#if self.visibleChildren?has_content && (!(maxDepth?has_content) || (maxDepth > 0))>
  <#-- decrease maxDepth if set-->
  <#if maxDepth?has_content>
    <#assign maxDepth=maxDepth - 1 />
  </#if>

  <#-- list children -->
  <ul class="${cssClass}">
    <#list self.visibleChildren![] as child>
      <@cm.include self=child view="asNavLinkListItem" params={
        "maxDepth": maxDepth!0
      } />
    </#list>
  </ul>
</#if>
