# if you use this environment with chef-solo make sure you set the following convenience attributes in you node.json
# * node['blueprint']['hostname'] if the default node['fqdn'] is not enough
# * node['blueprint']['default_version'] if you want to use this convenience attribute

# attributes to set here
node.default['blueprint']['dev']['db']['type'] = 'mysql'
node.default['blueprint']['tomcat']['jmx_remote_authenticate'] = false

# REPO CONFIGURATION
node.default['blueprint']['maven_repository_url'] = 'http://your.maven.repo'
# set your nexus url and repo here, if you want to use the nexus rest api. If you always do so remove the branch logic below for the versions.
node.default['blueprint']['nexus_url'] = nil
# there should be a repo that groups your snapshot, your releases and a central mirror repo together, we call it public but you may use an arbitrary name.
node.default['blueprint']['nexus_repo'] = 'public'
# VERSIONS
if node['blueprint']['nexus_url']
  # in case we are using the nexus rest api to determine versions, running on the LATEST is a good pattern for
  # a staging environment
  node.default['blueprint']['webapps']['content-management-server']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['master-live-server']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['workflow-server']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['replication-live-server']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['caefeeder-preview']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['caefeeder-live']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['content-feeder']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['elastic-worker']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['user-changes']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['studio']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['cae-preview']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['cae-live']['version'] = 'LATEST'
  node.default['blueprint']['webapps']['sitemanager']['version'] = 'LATEST'
  node.default['blueprint']['tools']['caefeeder-preview']['version'] = 'LATEST'
  node.default['blueprint']['tools']['caefeeder-live']['version'] = 'LATEST'
  node.default['blueprint']['tools']['content-management-server']['version'] = 'LATEST'
  node.default['blueprint']['tools']['master-live-server']['version'] = 'LATEST'
  node.default['blueprint']['tools']['workflow-server']['version'] = 'LATEST'
  node.default['blueprint']['tools']['theme-importer']['version'] = 'LATEST'
  node.default['blueprint']['tools']['replication-live-server']['version'] = 'LATEST'
  node.default['blueprint']['common_libs']['coremedia-tomcat.jar']['version'] = 'LATEST'
end

# by setting this to nothing and including the blueprint-postgresql::_single-node between the server and the databases recipe
# we make sure, that the service is only restarted once and not during delayed phase, which might conflict with the asynchronous
# initialization of webapps started by tomcat or the content import
node.default['postgresql']['server']['config_change_notify'] = :nothing

# allow automatic schemamigration in this environment. If you allow it also in production or staging, you need to set
# these attributes in those environments too
node.default['blueprint']['webapps']['content-management-server']['application.properties']['sql.schema.alterTable'] = true
node.default['blueprint']['webapps']['master-live-server']['application.properties']['sql.schema.alterTable'] = true
node.default['blueprint']['webapps']['replication-live-server']['application.properties']['sql.schema.alterTable'] = true
