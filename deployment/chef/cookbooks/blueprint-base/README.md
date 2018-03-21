# Description

This is the base cookbook for your chef deployment. Within this cookbook you can do basic stuff like:
- setting MOTD
- create users and set them up


Use this cookbook with care and only put things in here if they don't fit anywhere else. Don't let it become a "junk drawer"



# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* sysctl (~> 0.6.0)
* ulimit (~> 0.3.2)
* chef_handler (~> 1.2.0)
* compat_resource (~> 12.16.3)
* java_se (~> 8.0) (Suggested but not required)

# Attributes

* `node['blueprint']['user']` - The basic blueprint user, i.e. for tool execution. Defaults to `coremedia`.
* `node['blueprint']['group']` - The basic blueprint group. Defaults to `coremedia`.
* `node['blueprint']['base_dir']` - The base dir to install all apps to. Defaults to `/opt/coremedia`.
* `node['blueprint']['log_dir']` - The directory to log files to. Defaults to `/var/log/coremedia`.
* `node['blueprint']['cache_dir']` - The directory for coremedia caches. Defaults to `/var/cache/coremedia`.
* `node['blueprint']['temp_dir']` - The directory for coremedia temporary files. Defaults to `/var/tmp/coremedia`.
* `node['blueprint']['maven_repository_url']` - The repository url from which the webapp artifacts are downloaded. Defaults to `file://localhost/maven-repo/`.
* `node['blueprint']['nexus_url']` - The nexus base url. Set this to use the nexus REST API to retrieve artifacts. Using it allows you to resolve versions like `RELEASE`, `LATEST` and `X-SNAPSHOT` correctly. Defaults to `nil`.
* `node['blueprint']['nexus_repo']` - The nexus repo name to retrieve the artifacts via REST API. Defaults to `public`.
* `node['blueprint']['hostname']` - Convenience property to set the hostname used for public available urls in case of all components installed on the same host. Do not use or set this attribute in recipes, use the concrete attributes instead. Defaults to `node['fqdn']`.
* `node['blueprint']['default_version']` - Convenience property to set the version of all coremedia artifacts. For individual versions you can still set a version attribute beside each artifact_id attribute. Do not use or set this attribute in recipes, use the concrete attributes instead. Defaults to `1-SNAPSHOT`.
* `node['blueprint']['webapps']['solr']['context']` -  Defaults to `solr`.
* `node['blueprint']['webapps']['content-management-server']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['content-management-server']['artifact_id']` -  Defaults to `content-management-server-webapp`.
* `node['blueprint']['webapps']['content-management-server']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['content-management-server']['context']` -  Defaults to `coremedia`.
* `node['blueprint']['webapps']['content-management-server']['explode']` -  Defaults to `true`.
* `node['blueprint']['webapps']['content-feeder']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['content-feeder']['artifact_id']` -  Defaults to `content-feeder-webapp`.
* `node['blueprint']['webapps']['content-feeder']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['content-feeder']['context']` -  Defaults to `contentfeeder`.
* `node['blueprint']['webapps']['workflow-server']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['workflow-server']['artifact_id']` -  Defaults to `workflow-server-webapp`.
* `node['blueprint']['webapps']['workflow-server']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['workflow-server']['context']` -  Defaults to `workflow`.
* `node['blueprint']['webapps']['workflow-server']['explode']` -  Defaults to `true`.
* `node['blueprint']['webapps']['user-changes']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['user-changes']['artifact_id']` -  Defaults to `user-changes-webapp`.
* `node['blueprint']['webapps']['user-changes']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['user-changes']['context']` -  Defaults to `user-changes`.
* `node['blueprint']['webapps']['elastic-worker']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['elastic-worker']['artifact_id']` -  Defaults to `elastic-worker-webapp`.
* `node['blueprint']['webapps']['elastic-worker']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['elastic-worker']['context']` -  Defaults to `elastic-worker`.
* `node['blueprint']['webapps']['caefeeder-preview']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['caefeeder-preview']['artifact_id']` -  Defaults to `caefeeder-preview-webapp`.
* `node['blueprint']['webapps']['caefeeder-preview']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['caefeeder-preview']['context']` -  Defaults to `caefeeder`.
* `node['blueprint']['webapps']['cae-preview']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['cae-preview']['artifact_id']` -  Defaults to `cae-preview-webapp`.
* `node['blueprint']['webapps']['cae-preview']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['cae-preview']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['webapps']['studio']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['studio']['artifact_id']` -  Defaults to `studio-webapp`.
* `node['blueprint']['webapps']['studio']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['studio']['context']` -  Defaults to `studio`.
* `node['blueprint']['webapps']['sitemanager']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['sitemanager']['artifact_id']` -  Defaults to `editor-webstart-webapp`.
* `node['blueprint']['webapps']['sitemanager']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['sitemanager']['context']` -  Defaults to `editor-webstart`.
* `node['blueprint']['webapps']['sitemanager']['explode']` -  Defaults to `true`.
* `node['blueprint']['webapps']['master-live-server']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['master-live-server']['artifact_id']` -  Defaults to `master-live-server-webapp`.
* `node['blueprint']['webapps']['master-live-server']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['master-live-server']['context']` -  Defaults to `coremedia`.
* `node['blueprint']['webapps']['master-live-server']['explode']` -  Defaults to `true`.
* `node['blueprint']['webapps']['replication-live-server']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['replication-live-server']['artifact_id']` -  Defaults to `replication-live-server-webapp`.
* `node['blueprint']['webapps']['replication-live-server']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['replication-live-server']['context']` -  Defaults to `coremedia`.
* `node['blueprint']['webapps']['replication-live-server']['explode']` -  Defaults to `true`.
* `node['blueprint']['webapps']['caefeeder-live']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['caefeeder-live']['artifact_id']` -  Defaults to `caefeeder-live-webapp`.
* `node['blueprint']['webapps']['caefeeder-live']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['caefeeder-live']['context']` -  Defaults to `caefeeder`.
* `node['blueprint']['webapps']['cae-live']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['webapps']['cae-live']['artifact_id']` -  Defaults to `cae-live-webapp`.
* `node['blueprint']['webapps']['cae-live']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['webapps']['cae-live']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['tools']['content-management-server']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['tools']['content-management-server']['artifact_id']` -  Defaults to `cms-tools-application`.
* `node['blueprint']['tools']['content-management-server']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['tools']['workflow-server']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['tools']['workflow-server']['artifact_id']` -  Defaults to `wfs-tools-application`.
* `node['blueprint']['tools']['workflow-server']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['tools']['caefeeder-preview']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['tools']['caefeeder-preview']['artifact_id']` -  Defaults to `caefeeder-tools-application`.
* `node['blueprint']['tools']['caefeeder-preview']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['tools']['master-live-server']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['tools']['master-live-server']['artifact_id']` -  Defaults to `mls-tools-application`.
* `node['blueprint']['tools']['master-live-server']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['tools']['caefeeder-live']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['tools']['caefeeder-live']['artifact_id']` -  Defaults to `caefeeder-tools-application`.
* `node['blueprint']['tools']['caefeeder-live']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['tools']['replication-live-server']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['tools']['replication-live-server']['artifact_id']` -  Defaults to `rls-tools-application`.
* `node['blueprint']['tools']['replication-live-server']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['tools']['theme-importer']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['tools']['theme-importer']['artifact_id']` -  Defaults to `theme-importer-application`.
* `node['blueprint']['tools']['theme-importer']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['common_libs']['coremedia-tomcat.jar']['group_id']` -  Defaults to `com.coremedia.blueprint`.
* `node['blueprint']['common_libs']['coremedia-tomcat.jar']['artifact_id']` -  Defaults to `coremedia-tomcat`.
* `node['blueprint']['common_libs']['coremedia-tomcat.jar']['version']` -  Defaults to `node['blueprint']['default_version']`.
* `node['blueprint']['jaas']['crowd']['enabled']` -  Defaults to `false`.
* `node['blueprint']['jaas']['crowd']['properties']['crowd.domain']` -  Defaults to `crowd`.
* `node['blueprint']['jaas']['crowd']['properties']['crowd.expiration']` -  Defaults to `3599`.
* `node['blueprint']['jaas']['crowd']['properties']['crowd.contentgroups']` -  Defaults to `true`.
* `node['blueprint']['jaas']['crowd']['properties']['crowd.livegroups']` -  Defaults to `false`.
* `node['blueprint']['jaas']['crowd']['properties']['crowd.admingroups']` -  Defaults to `false`.
* `node['blueprint']['jaas']['crowd']['properties']['application.name']` -  Defaults to `blueprint`.
* `node['blueprint']['jaas']['crowd']['properties']['application.password']` -  Defaults to `secret`.
* `node['blueprint']['jaas']['crowd']['properties']['application.login.url']` -  Defaults to `https://mycrowdserver:port/crowd/console/`.
* `node['blueprint']['jaas']['crowd']['properties']['crowd.server.url']` -  Defaults to `https://mycrowdserver:port/crowd/rest/`.
* `node['blueprint']['jaas']['crowd']['properties']['crowd.base.url']` -  Defaults to `https://mycrowdserver:port/crowd/`.
* `node['blueprint']['jaas']['crowd']['properties']['session.isauthenticated']` -  Defaults to `session.isauthenticated`.
* `node['blueprint']['jaas']['crowd']['properties']['session.tokenkey']` -  Defaults to `session.tokenkey`.
* `node['blueprint']['jaas']['crowd']['properties']['session.validationinterval']` -  Defaults to `2`.
* `node['blueprint']['jaas']['crowd']['properties']['session.lastvalidation']` -  Defaults to `session.lastvalidation`.
* `node['blueprint']['jaas']['ldap']['enabled']` -  Defaults to `false`.
* `node['blueprint']['jaas']['ldap']['host']` -  Defaults to `my.ldap.host`.
* `node['blueprint']['jaas']['ldap']['port']` -  Defaults to `1212`.
* `node['blueprint']['jaas']['ldap']['domain']` -  Defaults to `mydomain`.
* `node['blueprint']['jaas']['cas']['enabled']` -  Defaults to `false`.
* `node['blueprint']['jaas']['cas']['validator_url']` -  Defaults to `http://`.
* `node['blueprint']['jaas']['cas']['cap_service_url']` -  Defaults to `http://`.
* `node['blueprint']['libjpeg_turbo_path']` - The path to the libjpeg turbo installation to increase image transformation performance. Defaults to `/opt/libjpeg-turbo/lib64`.

# Recipes

* [blueprint-base::default](#blueprint-basedefault) - This recipe creates the blueprint user and sets its home directory and process ulimits.
* blueprint-base::reporting

## blueprint-base::default

This recipe creates the blueprint user and sets its home directory and process ulimits. This user should be used to for login
and tools.

# Author

Author:: Your Name (<your_name@domain.com>)
