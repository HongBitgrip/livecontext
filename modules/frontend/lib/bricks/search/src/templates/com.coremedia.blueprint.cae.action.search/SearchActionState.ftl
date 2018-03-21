<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.search.SearchActionState" -->

<#assign searchResultHits=(self.result.hits)![]/>

<div class="cm-search cm-search--results">

  <div class="cm-search__header">
    <#-- headline -->
    <h1 class="cm-search__headline">${bp.getMessage("search_results")}</h1>
    <#-- box with infos about this search -->
    <#if self.queryTooShort!false>
      <div class="alert alert-warning" role="alert">
        ${bp.getMessage("search_error_belowMinQueryLength")}
      </div>
    <#elseif (searchResultHits?size == 0)>
      <div class="alert alert-warning" role="alert">
        ${bp.getMessage("search_error_noresults", [self.form.query!""])?no_esc}
      </div>
    <#else>
      <div class="alert alert-success" role="alert">
        ${bp.getMessage("search_searchTerm", [self.result.numHits, self.form.query!""])?no_esc}
      </div>
    </#if>
  </div>

  <#-- results -->
  <#if searchResultHits?has_content>
    <div class="cm-search__results">
      <#list searchResultHits as hit>
        <@cm.include self=hit view="asSearchResult" params={
          "highlightingMap": self.result.highlightingResults,
          "isLast": hit?is_last
        } />
      </#list>
    </div>
  </#if>
</div>
