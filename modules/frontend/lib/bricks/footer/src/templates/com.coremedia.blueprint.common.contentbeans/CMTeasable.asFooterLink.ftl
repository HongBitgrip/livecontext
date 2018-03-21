<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#assign target=(self.target?has_content && self.target.openInNewTab)?then(' target="_blank"', "") />

<a href="${cm.getLink(self.target)}"${target?no_esc} class="cm-footer__link"<@preview.metadata "properties.teaserTitle"/>>${self.teaserTitle!""}</a>
