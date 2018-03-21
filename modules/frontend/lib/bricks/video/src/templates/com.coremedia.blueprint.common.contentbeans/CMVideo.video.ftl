<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classVideo" type="java.lang.String" -->
<#-- @ftlvariable name="hideControls" type="java.lang.Boolean" -->
<#-- @ftlvariable name="autoplay" type="java.lang.Boolean" -->
<#-- @ftlvariable name="loop" type="java.lang.Boolean" -->
<#-- @ftlvariable name="muted" type="java.lang.Boolean" -->
<#-- @ftlvariable name="adaptive" type="java.lang.Boolean" -->
<#-- @ftlvariable name="youtube" type="java.lang.String" -->
<#-- @ftlvariable name="vimeo" type="java.lang.String" -->

<#assign params={
    "classVideo": classVideo!"",
    "autoplay":autoplay!false,
    "loop":loop!false,
    "hideControls":hideControls!false,
    "muted":muted!false,
    "adaptive": adaptive!false,
    "youtube":youtube!"",
    "vimeo":vimeo!""
} />

<#if (self.dataUrl!"")?contains("youtube.com") || (self.dataUrl!"")?contains("youtu.be")>
  <@cm.include self=self view="youtube" params=params />
<#elseif (self.dataUrl!"")?contains("vimeo.com")>
  <@cm.include self=self view="vimeo" params=params />
<#else>
  <@cm.include self=self view="html5" params=params />
</#if>
