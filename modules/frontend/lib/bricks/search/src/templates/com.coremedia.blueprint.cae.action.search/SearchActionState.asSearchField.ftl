<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.search.SearchActionState" -->

<#assign searchLink=cm.getLink(self!cm.UNDEFINED, {"page": cmpage})/>
<#assign searchQuery=self.form.query!""/>
<#assign cssClass=""/>
<#if searchQuery?has_content>
  <#assign cssClass=" focus"/>
</#if>

<form class="cm-search cm-search--form" action="${searchLink}" role="search">
  <label for="searchterm" class="cm-search__label sr-only">${bp.getMessage("search_label")}</label>
  <input id="searchterm" type="search" class="cm-search__input${cssClass}" name="query" value="${searchQuery}" placeholder="${bp.getMessage("search_placeholder")}" minlength="3">
  <button class="cm-search__button">
    <i class="glyphicon glyphicon-search"></i>
    <span class="cm-icon__info sr-only">${self.action.title!""}</span>
  </button>
</form>
