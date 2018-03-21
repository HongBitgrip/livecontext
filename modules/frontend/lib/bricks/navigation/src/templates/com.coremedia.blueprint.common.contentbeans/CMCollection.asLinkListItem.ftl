<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->
<#-- @ftlvariable name="isRoot" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="isTopLevel" type="java.lang.Boolean" -->
<#-- @ftlvariable name="showNavigationLabel" type="java.lang.Boolean" -->

<#assign cssClass=cm.localParameters().cssClass!""/>
<#assign isRoot=(isRoot!true)/>
<#assign isTopLevel=cm.localParameters().isTopLevel!false/>
<#assign showNavigationLabel=cm.localParameters().showNavigationLabel!false/>

<#if isRoot>
  <#if self.flattenedItems?has_content>
  <li class="${cssClass} cm-navigation-item dropdown" <@preview.metadata ["properties.children", self.content]/>>
  <#--link to this item in navigation and render children in dropdown list -->
    <a <@preview.metadata "properties.teaserTitle"/>>${self.teaserTitle!""}</a>
    <#if isTopLevel>
      <a href="#" class="cm-navigation-item__dropdown dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="caret"></span></a>
    </#if>

    <ul class="${cssClass} cm-navigation-item__list dropdown-menu">
      <#if showNavigationLabel>
        <a class="cm-navigation-item__label"<@preview.metadata "properties.teaserTitle"/>>${self.teaserTitle!""}</a>
      </#if>
    <#list self.flattenedItems![] as child>
      <@cm.include self=child view="asLinkListItem" params={
      "maxDepth": maxDepth!0,
      "isTopLevel": isTopLevel,
      "showNavigationLabel": isTopLevel,
      "collectionProperty":"properties.items"
      } />
    </#list>
    </ul>
  </li>
  <#else>
  <li class="${cssClass} cm-navigation-item">
    <#-- link to this item in navigation -->
    <a>${self.teaserTitle!""}</a>
  </li>
  </#if>
</#if>
