<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#assign additionalClass=cm.localParameters().additionalClass!"" />

<@bp.optionalFrame title=self.title!"">
  <@cm.include self=self view="asCarousel" params={"viewItems": "asTeaserHero", "additionalClass": additionalClass} />
</@bp.optionalFrame>