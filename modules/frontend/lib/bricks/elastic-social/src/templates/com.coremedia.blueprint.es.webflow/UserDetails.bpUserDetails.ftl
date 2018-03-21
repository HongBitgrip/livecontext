<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="userDetails" type="com.coremedia.blueprint.elastic.social.cae.flows.UserDetails" -->
<#-- @ftlvariable name="elasticSocialConfiguration" type="com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />

<div class="container">
  <div class="row">
    <div class="cm-form cm-form--userdetails well col-xs-12 col-md-6 col-md-push-3"<@cm.metadata data=[self.action.content!"", "properties.id"]/>>
      <#-- show user detauils-->
      <#if userDetails?has_content>

        <#-- headline -->
        <#if userDetails.viewOwnProfile>
          <h1 class="cm-form__headline"><@bp.message "userDetails_personalDetails" /></h1>
        </#if>
        <#if !userDetails.viewOwnProfile>
          <h1 class="cm-form__headline"><@bp.message "userDetails_title" /></h1>
        </#if>

        <#-- show properties -->
        <div class="cm-form">

          <#-- premoderation notification -->
          <#if userDetails.viewOwnProfile && userDetails.preModerationChanged>
            <div><@bp.message "userDetails_changesForPreModeration" /></div>
          </#if>

          <#-- image -->
          <#if userDetails.profileImage?has_content>
            <div class="form-group">
              <label><@bp.message "userDetails_profileImage" /></label>
              <#assign link=cm.getLink(userDetails.profileImage {"transform":true, "width":elasticSocialConfiguration.userImageWidth!100?int, "height": elasticSocialConfiguration.userImageHeight!100?int})/>
              <img class="cm-form__image" src="${link}" title="" alt="userimage">
            </div>
          </#if>

          <#-- username -->
          <div class="form-group">
            <@bp.labelFromSpring path="userDetails.username" text='${bp.getMessage("userDetails_username")}'/>
            <@spring.formInput path="userDetails.username" attributes='class="form-control" disabled="disabled"'/>
          </div>

          <#if userDetails.viewOwnProfile || userDetails.preview>
            <div class="form-group">
              <@bp.labelFromSpring path="userDetails.givenname" text='${bp.getMessage("userDetails_givenname")}'/>
            <@spring.formInput path="userDetails.givenname" attributes='class="form-control" disabled="disabled"'/>
            </div>

            <div class="form-group">
              <@bp.labelFromSpring path="userDetails.surname" text='${bp.getMessage("userDetails_surname")}'/>
            <@spring.formInput path="userDetails.surname" attributes='class="form-control" disabled="disabled"'/>
            </div>

            <div class="form-group">
              <@bp.labelFromSpring path="userDetails.emailAddress" text='${bp.getMessage("userDetails_emailAddress")}'/>
            <@spring.formInput path="userDetails.emailAddress" attributes='class="form-control" disabled="disabled"' fieldType="email"/>
            </div>
          </#if>

          <#if userDetails.viewOwnProfile>
            <form method="post" enctype="multipart/form-data">
              <input type="hidden" name="execution" value="${flowExecutionKey!""}">
              <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
              <input type="hidden" name="_eventId_editUser">

              <div class="form-group text-right">
                <@bp.button text=bp.getMessage("userDetails_editProfile") attr={"type": "submit", "id": "saveUser", "classes": ["btn","btn-primary"]} />
              </div>

            </form>
          </#if>

        <#--
        <#if elasticSocialConfiguration?has_content && elasticSocialConfiguration.complainingEnabled && !userDetails.viewOwnProfile
              && (es.isAnonymousUser() || (!es.isAnonymousUser() && !userDetails.viewOwnProfile))>
          <#assign navigationId=bp.id((cmpage.navigation.content.id)!) />
          <@es.complaining id=userDetails.id collection="users"
          value=es.hasComplaintForCurrentUser(userDetails.id, "users")
          itemId=itemId navigationId=navigationId />
        </#if>
        -->
        </div>

        <#-- twitter/facebook login -->
        <#if userDetails.viewOwnProfile
        && elasticSocialConfiguration?has_content
        && ((elasticSocialConfiguration.twitterAuthenticationEnabled && !userDetails.connectedWithTwitter)
        || (elasticSocialConfiguration.facebookAuthenticationEnabled && !userDetails.connectedWithFacebook))>
          <h3><@bp.message "userdetails_external_account" /></h3>
          <div class="cm-form">

            <#assign loginAction=self.loginAction!cm.UNDEFINED />
            <#assign loginFlow=bp.substitute(loginAction.id!"", loginAction)!cm.UNDEFINED />
            <#assign loginUrl=cm.getLink(loginFlow, {
            "absolute": true
            }) />

            <#assign registrationAction=self.registrationAction!cm.UNDEFINED />
            <#assign registrationFlow=bp.substitute(registrationAction.id!"", registrationAction)!cm.UNDEFINED />
            <#assign registerUrl=cm.getLink(registrationFlow, {
            "absolute": true
            }) />
            <#assign tenant=es.getCurrentTenant() />
            <div class="col-sm-offset-2 col-sm-10 cm-form__value">
              <#if elasticSocialConfiguration.facebookAuthenticationEnabled>
                <#assign facebookUrl=cm.getLink('/signin/facebook_' + tenant)/>
                <form action="${facebookUrl!""}" method="post" class="cm-form-facebook pull-left">
                  <input type="hidden" name="nextUrl" value="${nextUrl!""}">
                  <input type="hidden" name="registerUrl" value="${loginUrl!""}">
                  <input type="hidden" name="loginUrl" value="${loginUrl!""}">
                  <input type="hidden" name="scope" value="email">
                  <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
                  <input type="hidden" name="forceRegister" value="false">
                  <@bp.button text=bp.getMessage("login_with_facebook") attr={"type": "submit", "id": "facebookConnect","classes": ["btn", "btn-default"]} />
                </form>
              </#if>

              <#if elasticSocialConfiguration.twitterAuthenticationEnabled>
                <#assign twitterUrl=cm.getLink('/signin/twitter_' + tenant)/>
                <form action="${twitterUrl!""}" method="post" class="cm-form-twitter pull-right">
                  <input type="hidden" name="nextUrl" value="${nextUrl!""}">
                  <input type="hidden" name="registerUrl" value="${loginUrl!""}">
                  <input type="hidden" name="loginUrl" value="${loginUrl!""}">
                  <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}">
                  <input type="hidden" name="forceRegister" value="false">
                  <@bp.button text=bp.getMessage("login_with_twitter") attr={"type": "submit", "id": "twitterConnect","classes": ["btn", "btn-default"]} />
                </form>
              </#if>
            </div>
          </div>
        </#if>

        <#-- user activity -->
        <h2><@bp.message "userDetails_logging"/></h2>
        <div class="cm-form">
          <#if userDetails.lastLoginDate?has_content>
            <div class="form-group">
              <label class="cm-form__label">${bp.getMessage("userDetails_lastLoginDate")}</label>
              <span class="help-block">${userDetails.lastLoginDate!?datetime?string.medium}</span>
            </div>
          </#if>
          <#if userDetails.registrationDate?has_content>
            <div class="form-group">
              <label class="cm-form__label">${bp.getMessage("userDetails_registrationDate")}</label>
              <span class="help-block">${userDetails.registrationDate!?datetime?string.medium}</span>
            </div>
          </#if>
          <#if (userDetails.numberOfLogins?has_content && userDetails.numberOfLogins > 0)>
            <div class="form-group">
              <label class="cm-form__label">${bp.getMessage("userDetails_numberOfLogins")}</label>
              <span class="help-block">${userDetails.numberOfLogins}</span>
            </div>
          </#if>
          <#if (userDetails.numberOfReviews?has_content && userDetails.numberOfReviews > 0)>
          <div class="form-group">
            <label class="cm-form__label">${bp.getMessage("userDetails_numberOfReviews")}</label>
              <span class="help-block">${userDetails.numberOfReviews}</span>
          </div>
          </#if>
          <#if (userDetails.numberOfComments?has_content && userDetails.numberOfComments > 0)>
            <div class="form-group">
              <label class="cm-form__label">${bp.getMessage("userDetails_numberOfComments")}</label>
              <span class="help-block">${userDetails.numberOfComments}</span>
            </div>
          </#if>
          <#if (userDetails.numberOfRatings?has_content && userDetails.numberOfRatings > 0)>
            <div class="form-group">
              <label class="cm-form__label">${bp.getMessage("userDetails_numberOfRatings")}</label>
              <span class="help-block">${userDetails.numberOfRatings}</span>
            </div>
          </#if>
          <#if (userDetails.numberOfLikes?has_content && userDetails.numberOfLikes > 0)>
            <div class="form-group">
              <label class="cm-form__label">${bp.getMessage("userDetails_numberOfLikes")}</label>
              <span class="help-block">${userDetails.numberOfLikes}</span>
            </div>
          </#if>
        </div>

        <#-- personalization -->
        <#--#if userDetails.viewOwnProfile>
          <@cm.include self=self view="showPersonalizationForm"/>
        </#if-->

      </div>
      <#else>
        <@bp.notification type="error" text=bp.getMessage("userDetails_noUserFound") additionalClasses=["alert alert-danger"] />
      </#if>
    </div>
  </div>
</div>
