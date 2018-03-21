<#ftl encoding="UTF-8">
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->
<#-- @ftlvariable name="imageMapId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoMainId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoIdList" type="java.lang.String" -->

<#-- if imageMapId is not given, generate new id -->
<#assign imageMapId=imageMapId!(bp.generateId("cm-map-")) />
<#assign quickInfoMainId=quickInfoMainId!(imageMapId + "-quickinfo--main") />

<#assign useQuickinfo=cm.localParameter("useQuickinfo", true) />
<#assign quickInfoIdList=quickInfoIdList?split(",") />

<#assign imageMapAreas=bp.responsiveImageMapAreas(self) />
<#assign areaCssClass="cm-imagemap__area"/>
<#if !useQuickinfo>
  <#assign areaCssClass=areaCssClass + "--static"/>
</#if>

<#--imagemap with areas -->
<map <@bp.renderAttr { "name": imageMapId, "classes": ["cm-imagemap__areas"] } /> <@cm.dataAttribute name="data-cm-areas" data={"quickInfoMainId": quickInfoMainId} />>
  <#list imageMapAreas![] as imageMapArea>
    <#if imageMapArea?has_content>
      <#assign index=imageMapArea?index/>
      <#assign quickInfoId=quickInfoIdList[index]/>
      <#assign dataCoords=bp.responsiveImageMapAreaData(imageMapArea.coords)/>

      <#if imageMapArea.linkedContent?has_content>
        <#assign linkedContent=imageMapArea.linkedContent />
        <#assign link=cm.getLink(linkedContent.target!cm.UNDEFINED) />

        <#-- hot zones as areas -->
        <area shape="${imageMapArea.shape}"
              coords="0,0,0,0"
              <@cm.dataAttribute name="data-coords" data=dataCoords />
              href="${link}"
              class="${areaCssClass}"
              <#if useQuickinfo>
                data-quickinfo="${quickInfoId}"
              </#if>
              alt="${imageMapArea.alt!""}">

        <#-- quickinfo text overlay -->
        <#if imageMapArea.displayAsInlineOverlay!false>
          <#assign classOverlay="" />
          <#assign theme=imageMapArea.inlineOverlayTheme!"" />
          <#-- only allow valid themes -->
          <#if (["dark", "light", "dark-on-light", "light-on-dark"]?seq_contains(theme))>
            <#assign classOverlay="cm-overlay--theme-" + theme />
          </#if>
          <div class="cm-imagemap__hotzone cm-imagemap__hotzone--text" data-quickinfo="${quickInfoId}">
            <@cm.include self=linkedContent!cm.UNDEFINED view="asOverlay" params={
            "classOverlay": classOverlay,
            "metadata": ["properties.localSettings"],
            "overlay": bp.setting(self, "overlay", {})
            } />
          </div>
        <#-- quickinfo icon -->
        <#else>
          <#assign parameters={"class": "cm-imagemap__hotzone cm-imagemap__hotzone--icon"}/>
          <#if linkedContent.content?has_content>
            <#assign parameters+={"metadata" : ["properties.localSettings", linkedContent.content]}/>
          </#if>
          <#if useQuickinfo>
            <#assign parameters+={"data-cm-button--quickinfo": '{"target": "${quickInfoId!""}"}'}/>
          </#if>
          <@bp.button baseClass="" href="${link}" iconText=(linkedContent.teaserTitle!bp.getMessage("button_quickinfo")) attr=parameters />
        </#if>
      </#if>
    </#if>
  <#else>
    <#assign target=(self.target?has_content && self.target.openInNewTab)?then(' target="_blank"', "") />
    <area shape="default" href="${cm.getLink(self.target!cm.UNDEFINED)}"${target?no_esc} class="cm-imagemap__area">
  </#list>
</map>
