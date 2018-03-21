<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGrid"-->

<div id="main">
    <div class="row">
        <#assign errorHeadline><@bp.message "error.notfound.headline" /></#assign>
        <#assign errorText><@bp.message "error.notfound.text" /></#assign>
        <@bp.notification type="error" title=errorHeadline text=errorText dismissable=false />
    </div>
</div>
