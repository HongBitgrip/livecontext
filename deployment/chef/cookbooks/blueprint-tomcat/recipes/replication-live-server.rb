=begin
#<
This recipe installs and configures the CoreMedia Blueprint Master Live Server.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'replication-live-server'

# use default_unless to allow configuration in recipes run prior to this one
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['cap.server.http.port'] = "#{node['blueprint']['tomcat'][service_name]['port_prefix']}80"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['replicator.publicationIorUrl'] = "#{cm_webapp_url('master-live-server')}/ior"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['cap.server.ORBServerHost'] = node['blueprint']['hostname']
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['cap.server.ORBServerPort'] = "#{node['blueprint']['tomcat'][service_name]['port_prefix']}83"

blueprint_tomcat_service service_name

# in case cae-live and replicator are installed on the same node we set the default
node.force_default['blueprint']['webapps']['cae-live']['application.properties']['repository.url'] = "#{cm_webapp_url('replication-live-server')}/ior"
