<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="overlay" type="java.util.Map" -->
<#-- @ftlvariable name="classOverlay" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#-- if overlay configuration is not set explicitly assert false for each key not set -->
<#assign overlay={
  "displayTitle": false
} + overlay!{} />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />

<div class="cm-overlay ${classOverlay}"<@cm.metadata data=(metadata![]) + [self.content] />>
  <@bp.optionalLink href=cm.getLink(self.target!cm.UNDEFINED) attr={"class": "cm-overlay__link", "target":target}>
    <#if self.teaserTitle?has_content && overlay.displayTitle>
      <span<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle}</span>
    <#else>
      <span><@bp.message "button_quickinfo" /></span>
    </#if>
  </@bp.optionalLink>
</div>
