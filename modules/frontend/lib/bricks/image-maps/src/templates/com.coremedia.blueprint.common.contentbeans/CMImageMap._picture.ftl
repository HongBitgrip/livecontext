<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign renderDimmer=cm.localParameter("renderDimmer", false) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign imageMapId=cm.localParameter("imageMapId", "") />
<#assign quickInfoMainId=cm.localParameter("quickInfoMainId", "") />
<#assign quickInfoIdList=cm.localParameter("quickInfoIdList", "") />
<#assign useQuickinfo=cm.localParameter("useQuickinfo", true) />

<#-- display imagemap only if an image exist -->
<#if self.picture?has_content>
  <div class="cm-imagemap__wrapper">
    <#-- include image -->
    <@cm.include self=self.picture params={
      "classBox": "${blockClass}__picture-box cm-imagemap__picture-box",
      "classImage":  "${blockClass}__picture cm-imagemap__picture",
      "metadata": ["properties.pictures"],
      "additionalAttr": {"useMap": "#" + imageMapId!"", "unselectable": "on"}
    }/>
    <#-- include map -->
    <@cm.include self=self view="_areasMap" params={"imageMapId": imageMapId, "quickInfoMainId": quickInfoMainId, "quickInfoIdList": quickInfoIdList, "useQuickinfo": useQuickinfo}/>
  </div>

  <#-- display optional dimmer -->
  <#if renderDimmer>
    <div class="${blockClass}__dimmer"></div>
  </#if>

<#-- display missing-image placeholder-->
<#elseif renderEmptyImage>
  <div class="${blockClass}__picture-box cm-imagemap__picture-box" <@cm.metadata "properties.pictures" />>
    <div class="${blockClass}__picture cm-imagemap__picture"></div>
  </div>
</#if>
