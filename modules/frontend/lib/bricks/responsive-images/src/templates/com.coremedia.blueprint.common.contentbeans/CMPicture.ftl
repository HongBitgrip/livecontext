<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="disableCropping" type="java.lang.Boolean" -->
<#-- @ftlvariable name="classImage" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="crop" type="java.lang.String" -->
<#-- @ftlvariable name="additionalAttr" type="java.util.Map" -->

<#assign background=cm.localParameters().background!false/>
<#assign classBox=cm.localParameters().classBox!""/>
<#assign classImage=cm.localParameters().classImage!""/>

<div class="${classBox!""}"<@cm.metadata (metadata![]) + [self.content]/>>
  <#if self.data?has_content>
    <#assign imageLink=""/>
    <#assign classResponsive=""/>
    <#-- additionalAttr used by imagemaps -->
    <#assign attributes=additionalAttr!{}/>

    <#-- decide if responsiveImage functionality is to be used or uncropped image will be shown -->
    <#if self.disableCropping || disableCropping!false>
      <#-- A) Cropping disabled, display image in full size -->
      <#assign imageLink=bp.uncroppedImageLink(self)/>
      <#-- add all attributes to the map -->
      <#if imageLink?has_content && background>
        <#assign attributes={"style": "background-image: url(${imageLink})"}/>
      </#if>
    <#else>
      <#-- B) display responsive image -->
      <#assign classResponsive="cm-image--responsive"/>
      <#assign attributes += {"data-cm-responsive-image": bp.responsiveImageLinksData(self, limitAspectRatios![])!""}/>
    </#if>

    <#-- use high resolution images -->
    <#assign retinaEnabled=bp.setting(cmpage, "enableRetinaImages", false)/>
    <#if retinaEnabled>
      <#assign attributes += {"data-cm-retina-image": "true"}>
    </#if>

    <#-- alt is the content name by default -->
    <#assign alt=(self.content.name)!""/>
    <#-- if alt property is set, use it as alt -->
    <#if self.alt?has_content>
      <#assign alt=self.alt />
    </#if>

    <#-- title (and copyright) -->
    <#assign title=self.title!""/>
    <#if self.copyright?has_content>
      <#assign title += title?has_content?then(" ", "") + "(© " + self.copyright + ")"/>
    </#if>

    <#-- add all attributes to the map -->
    <#assign attributes += {"title": title}/>
    <#assign classImage += " cm-image"/>
    <#if background>
      <div class="cm-image--background ${classImage!""} ${classResponsive!""}" <@bp.renderAttr attributes/>
        <@cm.metadata data=["properties.data"]/>>
      </div>
    <#else>
      <#assign attributes += {"alt": alt, "src": imageLink}/>
      <img class="cm-image--loading ${classImage!""} ${classResponsive!""}" <@bp.renderAttr attributes/>
        <@cm.metadata data=["properties.data" + crop?has_content?then(".", "") + crop!""]/>>
    </#if>
  </#if>
</div>
