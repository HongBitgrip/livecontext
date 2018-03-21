<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="isRoot" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="isTopLevel" type="java.lang.Boolean" -->
<#-- @ftlvariable name="showNavigationLabel" type="java.lang.Boolean" -->

<#assign cssClass=cm.localParameters().cssClass!""/>
<#assign isRoot=(isRoot!true)/>
<#assign isTopLevel=cm.localParameters().isTopLevel!false/>
<#assign showNavigationLabel=cm.localParameters().showNavigationLabel!false/>
<#assign contentData=self.content!{}/>

<#if isRoot || (!((self.hidden)!false))>

  <#-- add css class active, if this item is part of the active navigation -->
  <#if (bp.isActiveNavigation(self, (cmpage.navigation.navigationPathList)![]))>
    <#assign cssClass= cssClass + ' active'/>
  </#if>

  <#if self.visibleChildren?has_content>
    <li class="${cssClass} cm-navigation-item dropdown" <@preview.metadata data=["properties.children", contentData]/>>
      <#--link to this item in navigation and render children in dropdown list -->
      <@cm.include self=self view="asLink"/>
      <#if isTopLevel>
        <a href="#" class="cm-navigation-item__dropdown dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="caret"></span></a>
      </#if>
      <@cm.include self=self view="asLinkList" params={
        "isRoot": false,
        "cssClass": "dropdown-menu ${cssClass}",
        "showNavigationLabel": showNavigationLabel
      }/>
    </li>
  <#else>
    <li class="${cssClass} cm-navigation-item" <@preview.metadata data=["properties.children", contentData]/>>
      <#-- link to this item in navigation -->
      <@cm.include self=self view="asLink"/>
    </li>
  </#if>
</#if>