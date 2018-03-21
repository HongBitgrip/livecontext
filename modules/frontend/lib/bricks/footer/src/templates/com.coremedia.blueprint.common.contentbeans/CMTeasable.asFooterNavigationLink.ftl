<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then(' target="_blank"', "") />
<a href="${link}"${target?no_esc}<@preview.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</a>
