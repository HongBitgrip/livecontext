<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSitemap" -->

<div class="cm-sitemap"<@cm.metadata self.content />>
  <#-- title -->
  <h2 class="cm-sitemap__title"<@cm.metadata "properties.title"/>>${self.title!""}</h2>
  <#-- items/tree of navigation with default max depth of 3 -->
  <#if self.root?has_content>
    <@cm.include self=self.root view="asLinkList" params={
      "maxDepth": self.sitemapDepth,
      "cssClass": "cm-sitemap__items",
      "childrenCssClass": "cm-sitemap__item"
    } />
  </#if>
</div>
