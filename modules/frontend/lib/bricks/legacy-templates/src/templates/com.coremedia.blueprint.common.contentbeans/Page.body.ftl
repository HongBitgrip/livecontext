<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="self.content" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->
<#assign sliderMetadata=bp.setting(cmpage, "sliderMetaData", "")/>

<body id="top" class="${(self.pageGrid.cssClassName)!""}" <@cm.metadata data=sliderMetadata />>

<#-- Iterate over each row -->
<#if self.pageGrid?has_content>
  <#list self.pageGrid.rows![] as row>
    <div class="row">
    <#-- Iterate over each placement-->
      <#list row.placements![] as placement>
        <@cm.include self=placement/>
      </#list>
    </div>
  </#list>
</#if>

<@cm.include self=self view="_bodyEnd"/>

</body>
