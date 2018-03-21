<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMExternalLink" -->

<#--
    Template Description:

    Displays an external link with title, image and text.
    Please check brick "generic-templates" for a more detailed version.
-->
<#assign target=self.openInNewTab?then("_blank", "") />

<div class="cm-teasable cm-teasable--externallink">
  <@bp.optionalLink href=self.url attr={"target":target}>
    <h1 class="cm-teasable_title">${self.teaserTitle!""}</h1>
    <div class="cm-teasable__picture">
      <@cm.include self=self.picture!cm.UNDEFINED />
    </div>
    <div class="cm-teasable__text">
      <@cm.include self=self.teaserText!cm.UNDEFINED />
    </div>
  </@bp.optionalLink>
</div>
