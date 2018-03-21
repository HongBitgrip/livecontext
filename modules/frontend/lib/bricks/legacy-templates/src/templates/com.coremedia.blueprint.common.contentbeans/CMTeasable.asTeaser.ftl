<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<div class="cm-teasable">
    <h2 class="cm-teasable__headline">${self.teaserTitle!""}</h2>
    <div class="cm-teasable__text">
    <@cm.include self=self.teaserText!cm.UNDEFINED />
    </div>
</div>
