<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />

<div class="cm-superhero ${additionalClass!""}"<@cm.metadata self.content /> data-cm-module="superhero">
  <#-- picture -->
  <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix="cm-superhero" background=true/>
  <#if (self.teaserTitle?has_content || self.teaserText?has_content)>
  <#-- with caption -->
    <div class="cm-superhero__caption row">
      <div class="col-xs-10 col-xs-push-1 col-md-8 col-md-push-2">
      <#-- headline -->
          <h1 class="cm-superhero__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
      </div>
    </div>
  </#if>
</div>
