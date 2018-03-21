# if you use this environment make sure you set the following convenience attributes in you kitchen file
# * node['blueprint']['hostname'] if the default node['fqdn'] is not enough
# * node['blueprint']['default_version'] if you want to use this convenience attribute

node.default['blueprint']['maven_repsitory_url'] = 'file://localhost/maven-repo/'
node.default['blueprint']['tomcat']['jmx_remote_authenticate'] = false
node.default['blueprint']['tomcat']['jmx_remote_server_name'] = node['blueprint']['hostname']

# by setting this to nothing and including the blueprint-postgresql::_single-node between the server and the databases recipe
# we make sure, that the service is only restarted once and not during delayed phase, which might conflict with the asynchronous
# initialization of webapps started by tomcat or the content import
node.default['postgresql']['server']['config_change_notify'] = :nothing

# allow automatic schemamigration in this environment. If you allow it also in production or staging, you need to set
# these attributes in those environments too
node.default['blueprint']['webapps']['content-management-server']['application.properties']['sql.schema.alterTable'] = true
node.default['blueprint']['webapps']['master-live-server']['application.properties']['sql.schema.alterTable'] = true
node.default['blueprint']['webapps']['replication-live-server']['application.properties']['sql.schema.alterTable'] = true
