<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<#-- This placement is used for the footer section -->
<footer id="cm-${self.name!""}" class="cm-footer"<@cm.metadata data=[bp.getPlacementPropertyName(self)!"",bp.getPlacementHighlightingMetaData(self)!""]/>>
  <#-- links -->
  <#assign numberOfItems=self.items?size />
  <#if (numberOfItems > 0)>
  <ul class="cm-footer__links row">
    <#list self.items![] as item>
      <#-- tablet: 3 rows -->
      <#assign offsetSm=bp.getOffsetClass(item_index, numberOfItems, 3, " col-sm-") />
      <#-- desktop: 6 rows -->
      <#assign offsetMd=bp.getOffsetClass(item_index, numberOfItems, 6, " col-md-", true) />

      <li class="col-xs-12 col-sm-4 col-md-2${offsetSm}${offsetMd}">
        <@cm.include self=item view="asLink" />
      </li>
    </#list>
  </ul>
  </#if>

  <#-- additional infos in footer-->
  <div class="cm-footer__more">
    <#-- logo -->
    <a class="cm-logo navbar-brand" href="${cm.getLink(cmpage.navigation.rootNavigation!cm.UNDEFINED)}">
      <span class="cm-logo__image"></span>
    </a>
    <div class="cm-footer__meta">
      <span class="cm-footer__name">${bp.getMessage("footer_text")}</span>
      <span class="cm-footer__copyright">${bp.getMessage("copyright")}</span>
    </div>
  </div>
</footer>
