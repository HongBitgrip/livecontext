<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="collectionProperty" type="java.lang.String" -->

<#assign cssClass=cm.localParameters().cssClass!""/>

<li class="${cssClass} cm-navigation-item" <@cm.metadata data=[collectionProperty!"properties.children"] />><@cm.include self=self view="asLink" /></li>