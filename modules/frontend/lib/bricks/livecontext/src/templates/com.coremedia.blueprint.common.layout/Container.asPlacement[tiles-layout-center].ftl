<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#assign items=self.items/>

<#if (items?size > 0) >
<div class="cm-container cm-collection--tiles-center" <@cm.metadata data=bp.getContainerMetadata(self) />>
    <div class="row-grid row">
        <div class="col-xs-12 col-sm-4">
            <div class="row-grid row">
                <div class="col-xs-12 col-sm-12">
                  <@cm.include self=items[0]!cm.UNDEFINED view="asTeaser"/>
                </div>
            </div>
          <#if (items?size > 3) >
              <div class="row-grid row">
                  <div class="col-xs-12 col-sm-12">
                    <@cm.include self=items[3]!cm.UNDEFINED view="asTeaser"/>
                  </div>
              </div>
          </#if>
        </div>
      <#if (items?size > 1) >
          <div class="col-xs-12 col-sm-4">
            <@cm.include self=items[1]!cm.UNDEFINED view="asTeaser"/>
          </div>
      </#if>
      <#if (items?size > 2) >
          <div class="col-xs-12 col-sm-4">
              <div class="row-grid row">
                  <div class="col-xs-12 col-sm-12">
                    <@cm.include self=items[2]!cm.UNDEFINED view="asTeaser"/>
                  </div>
              </div>
            <#if (items?size > 4) >
                <div class="row-grid row">
                    <div class="col-xs-12 col-sm-12">
                      <@cm.include self=items[4]!cm.UNDEFINED view="asTeaser"/>
                    </div>
                </div>
            </#if>
          </div>
      </#if>
    </div>
</div>
  <#if (self.items?size>5) >
    <@cm.include self=bp.getContainerFromBase(self,self.items[5..*self.items?size]) view="asGrid" params={"itemsPerRow": 3, "additionalClass": "cm-collection--tiles-landscape"} />
  </#if>
</#if>