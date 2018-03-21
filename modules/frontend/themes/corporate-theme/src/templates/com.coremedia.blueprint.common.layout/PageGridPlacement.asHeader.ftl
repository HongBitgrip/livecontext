<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<#assign numberOfItems=self.items?size />
<#assign searchAction=bp.setting(cmpage.context,"searchAction", {})/>

<header id="cm-${self.name!""}" class="cm-header <#if !searchAction?has_content>cm-${self.name!""}--without-search</#if> navbar navbar-default"<@cm.metadata data=[bp.getPlacementPropertyName(self)!"",bp.getPlacementHighlightingMetaData(self)!""]/>>

  <#-- fixed position of logo and navigation button-->
  <div class="navbar-header">
    <#-- logo -->
    <a class="cm-logo navbar-brand" href="${cm.getLink(cmpage.navigation.rootNavigation!cm.UNDEFINED)}">
      <span class="cm-logo__image"></span>
      <span class="sr-only">${bp.getMessage("home")}</span>
    </a>

    <#-- button for navigation -->
    <button type="button" class="cm-header__button navbar-toggle collapsed" data-toggle="collapse" data-target=".cm-header-is-collapse">
      <span class="sr-only">${bp.getMessage("navigation_toggle")}</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>

    <#-- global search field -->
    <#if searchAction?has_content>
      <div id="cm-search" class="cm-header__search">
        <@cm.include self=searchAction view="asSearchField" />
      </div>
    </#if>
  </div>

  <#-- breadcrumb -->
  <div class="cm-breadcrumb--outer cm-header-is-collapse collapse in">
    <@cm.include self=cmpage.navigation!cm.UNDEFINED view="asBreadcrumb" params={"classBreadcrumb": "breadcrumb"} />
  </div>

  <#-- header items like search, language chooser, links -->
  <#if (numberOfItems > 0)>
    <ul class="cm-header__items cm-header-is-collapse collapse">
    <#list self.items![] as item>
      <li class="cm-header__item">
        <@cm.include self=item view="asHeader" />
      </li>
    </#list>
    </ul>
  </#if>
</header>

<#-- main navigation with default max depth of 2 -->
<#if (cmpage.navigation.rootNavigation)?has_content>
  <nav id="cm-navigation" class="cm-header-is-collapse cm-nav-collapse navbar-collapse collapse">
    <@cm.include self=cmpage.navigation.rootNavigation view="asNavLinkList" params={
      "maxDepth": bp.setting(cmpage, "navigation_depth", 2),
      "cssClass": "cm-megamenu nav navbar navbar-nav row",
      "childrenCssClass": "cm-menu"
    } />
    <div class="cm-nav-collapse__gradiant"></div>
  </nav>
</#if>
