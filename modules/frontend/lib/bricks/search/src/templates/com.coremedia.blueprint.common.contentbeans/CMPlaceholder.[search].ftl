<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->
<#-- @ftlvariable name="cmpage.context" type="com.coremedia.blueprint.common.contentbeans.CMContext" -->

<#-- delegate to the search action, defined in the cmpage settings -->
<#assign searchAction=bp.setting(cmpage.context,"searchAction")/>
<@cm.include self=searchAction view="asSearchField" />
