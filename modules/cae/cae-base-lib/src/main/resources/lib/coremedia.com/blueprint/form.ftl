<#ftl strip_whitespace=true>
<#import "util.ftl" as util>

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "Freemarker API" in chapter "Reference" in the frontend manual for details and examples
 * for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#--
 * Renders a label associated to spring forms
 *
 * @param path the name of the field to bind to
 * @param text (optional) the text of the label. By default localization for the bound field will be applied.
 * @param bindPath (optional) prevents the rebinding of the path, e.g. if you already know that the path is bound
 * @param attr (optional) additional attributes for label tag
 * todo: CMS-11137 move to elasticsocial
 -->
<#macro labelFromSpring path text="" bindPath=true attr={}>
  <#if bindPath><@spring.bind path=path /></#if>
  <label for="${_getIdFromExpression(spring.status.expression)}" <@util.renderAttr attr />>${text}</label>
</#macro>


<#-- --- PRIVATE --------------------------------------------------------------------------------------------------- -->


<#-- PRIVATE -->
<#function _getIdFromExpression expression>
  <#return expression?replace("[", "")?replace("]", "") />
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->


<#--
 * Renders an input field associated to spring forms
 *
 * @param path the name of the field to bind to
 * @param showErrors (optional) specifies if errors are rendered
          Errors will be rendered into the placeholder of the input and cause its value to be empty.
 * @param type (optional) specifies the type of the input field
 *        If "password" is used the value of the input field will always be empty
 * @param placeholder (optional) specifies a placeholder
 * @param bindPath (optional) false prevents the rebinding of the path, e.g. if you already know that the path is bound
 * @param attr (optional) additional attributes for rendered label tag
 * DEPRECATED, PRIVATE
 -->
<#macro inputFromSpring path showErrors=true type="text" placeholder="" bindPath=true attr={}>
  <#local classes=[] />

  <#if bindPath><@spring.bind path=path /></#if>
  <#assign hasErrors=spring.status.error />

  <#local attr={"type": type, "id": spring.status.expression, "name": spring.status.expression} + attr />

  <#if type == "checkbox">
    <#local classes=classes + ["cm-checkbox"] />
    <input type="hidden" name="_${spring.status.expression}" value="on">
    <#if spring.status.value?has_content
         && ((spring.status.value?is_string && spring.status.value == "true")
             || (spring.status.value?is_boolean && spring.status.value))>
      <#local attr={"checked": "checked"} + attr />
    </#if>
    <#if showErrors && hasErrors>
      <#local classes=classes + ["cm-checkbox--error"] />
    </#if>
  <#else>
    <#local classes=classes + ["cm-textfield"] />
    <#if type != "password" && (!showErrors || !hasErrors)>
      <#local attr={"value": spring.stringStatusValue} + attr />
    <#else>
      <#local attr={"value": ""} + attr />
    </#if>
    <#if showErrors && hasErrors>
      <#local classes=classes + ["cm-textfield--error"] />
      <#local attr={"placeholder": spring.status.getErrorMessagesAsString(" - ")} + attr />
    <#else>
      <#local attr={"placeholder": placeholder} + attr />
    </#if>
  </#if>
  <#local attr=util._extendSequenceInMap(attr, "classes", classes) />

  <input<@util.renderAttr attr />>
</#macro>

<#-- DEPRECATED, UNUSED -->
<#macro hiddenFieldFromSpring path bindPath=true attr={}>
  <#if bindPath><@spring.bind path=path /></#if>
  <#local attr={"type": "hidden", "name": spring.status.expression, "id": _getIdFromExpression(spring.status.expression), "value": spring.stringStatusValue} + attr />
  <input<@util.renderAttr attr />>
</#macro>

<#--
 * Renders a field containing a label and an input field associated to spring forms
 *
 * @param path the name of the field to bind to
 * @param baseClass (optional) specifies the base class attached to the field and its members
 * @param additionalClasses (optional) specifies a sequence of additional class attached to the field
 * @param bindPath (optional) false prevents the rebinding of the path, e.g. if you already know that the path is bound
 * @param attr (optional) additional attributes for rendered field
 * @param labelText (optional) @see labelFromSpring#text
 * @param labelAttr (optional) @see labelFromSpring#attr
 * @param inputShowErrors (optional) @see inputFromSpring#showErrors
 * @param inputType (optional) inputFromSpring#type
 * @param inputPlaceholder(optional) inputFromSpring#placeholder
 * @param inputAttr (optional) @see inputFromSpring#attr
 * @nested (optional) nested content will be placed inside the field before the label and input elements are rendered
 * DEPRECATED, UNUSED
 -->
<#macro fieldFromSpring path baseClass="cm-field" additionalClasses=[] bindPath=true attr={} labelText="" labelAttr={} inputShowErrors=true inputType="text" inputPlaceholder="" inputAttr={}>
  <#if bindPath><@spring.bind path=path /></#if>
  <@spring.bind path=path />
  <#local classes=[baseClass] + additionalClasses />
  <#if spring.status.error>
    <#local classes=classes +  [baseClass + "--error"] />
  </#if>
  <#local attr=util._extendSequenceInMap(attr, "classes", classes) />
  <div<@util.renderAttr attr />>
    <#nested />

    <#local labelAttr=util._extendSequenceInMap(labelAttr, "classes", [baseClass + "__name"]) />
    <#local inputAttr=util._extendSequenceInMap(inputAttr, "classes", [baseClass + "__value"]) />

    <#if inputType != "checkbox">
      <@labelFromSpring path=path text=labelText bindPath=false attr=labelAttr />
      <@inputFromSpring path=path showErrors=inputShowErrors type=inputType placeholder=inputPlaceholder bindPath=false attr=inputAttr />
    <#else>
      <@inputFromSpring path=path showErrors=inputShowErrors type=inputType placeholder=inputPlaceholder bindPath=false attr=inputAttr />
      <@labelFromSpring path=path text=labelText bindPath=false attr=labelAttr />
    </#if>
  </div>
</#macro>
