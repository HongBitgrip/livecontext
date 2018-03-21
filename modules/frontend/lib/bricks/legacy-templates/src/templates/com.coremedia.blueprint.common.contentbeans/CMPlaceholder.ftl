<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->
<#-- @ftlvariable name="recursiveInclude" type="java.lang.Boolean" -->

<#assign recursiveInclude=cm.localParameter("recursiveInclude", false) />
<#assign layout=(self.viewtype.layout)!"" />

<#-- use layout as view -->
<#if layout?has_content && !recursiveInclude>
  <@cm.include self=self view="[${layout}]" params={"recursiveInclude": true} />
<#-- @deprecated: use id, if no layout is set -->
<#elseif self.id?has_content>
  <#assign substitution=bp.substitute(self.id!"", self)!cm.UNDEFINED />
  <@cm.include self=substitution />
<#-- otherwise do nothing. Placeholder without layout or id can't be displayed -->
</#if>
