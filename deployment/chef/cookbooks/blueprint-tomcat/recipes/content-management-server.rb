=begin
#<
This recipe installs and configures the CoreMedia Blueprint Content Management Server.
#>
=end

# include base recipe to make sure all dirs are set up
include_recipe 'blueprint-tomcat::_base'

service_name = 'content-management-server'
service_dir = "#{node['blueprint']['base_dir']}/#{service_name}"
cache_dir = "#{node['blueprint']['cache_dir']}/#{service_name}"
# we cannot directly use the helper method in a definitions body, otherwise it gets evaluated too early
start_service = cm_tomcat_default(service_name, 'start_service')

# use default_unless to allow configuration in recipes run prior to this one
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.collection.content'] = 'studio'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['publisher.target.ior.url'] = "#{cm_webapp_url('master-live-server')}/ior"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['cap.server.http.port'] = "#{node['blueprint']['tomcat'][service_name]['port_prefix']}80"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['cap.server.ORBServerHost'] = node['blueprint']['hostname']
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['cap.server.ORBServerPort'] = "#{node['blueprint']['tomcat'][service_name]['port_prefix']}83"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['am.blobstore.rootdir'] = "#{cache_dir}/blobstore-assets"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['cap.server.login.authentication'] = "#{service_dir}/jaas.conf"

jaas_conf = {}
if node.deep_fetch('blueprint', 'jaas', 'crowd', 'enabled')
  node.override['blueprint']['webapps'][service_name]['application.properties']['cap.server.ldap.1.class'] = 'com.coremedia.blueprint.userproviders.crowd.CrowdUserProvider'
  node.override['blueprint']['webapps'][service_name]['application.properties']['cap.server.ldap.1.properties'] = "#{service_dir}/jndi-crowd.properties"
  crowd_config = node['blueprint']['webapps'][service_name]['application.properties']['cap.server.ldap.1.properties']
  jaas_conf = { crowd: { config_file: crowd_config, domain: 'crowd' } }
elsif node.deep_fetch('blueprint', 'jaas', 'ldap', 'enabled')
  node.override['blueprint']['webapps'][service_name]['application.properties']['cap.server.ldap.1.class'] = ''
  node.override['blueprint']['webapps'][service_name]['application.properties']['cap.server.ldap.1.properties'] = ''
  jaas_conf = { ldap: { host: node['blueprint']['jaas']['ldap']['host'], port: node['blueprint']['jaas']['ldap']['port'], domain: node['blueprint']['jaas']['ldap']['domain'] } }
elsif node.deep_fetch('blueprint', 'jaas', 'cas', 'enabled')
  node.override['blueprint']['webapps'][service_name]['application.properties']['cap.server.ldap.1.class'] = ''
  node.override['blueprint']['webapps'][service_name]['application.properties']['cap.server.ldap.1.properties'] = ''
  jaas_conf = { cas: { validator_url: node['blueprint']['jaas']['cas']['validator_url'], cap_service_url: node['blueprint']['jaas']['cas']['cap_service_url'] } }
else
  # disable any of the above
  node.default['blueprint']['webapps'][service_name]['application.properties']['cap.server.ldap.1.class'] = ''
  node.default['blueprint']['webapps'][service_name]['application.properties']['cap.server.ldap.1.properties'] = ''
end

blueprint_tomcat_service service_name do
  # skip lifecycle for further configuring (writing the templates below)
  skip_lifecycle true
end

# get the tomcat to notify a restart, we do not need to notify the webapp as, the conifguration lies outside of the webapp path
tomcat = cm_tomcat(service_name)
webapp = cm_webapp(service_name)

directory node['blueprint']['webapps'][service_name]['application.properties']['am.blobstore.rootdir'] do
  recursive true
  owner service_name
  group node['blueprint']['group']
end

template "#{service_name} - crowd_config" do
  path crowd_config
  owner service_name
  group node['blueprint']['group']
  mode 0700
  sensitive true
  source 'properties.erb'
  variables props: node['blueprint']['jaas']['crowd']['properties']
  not_if { crowd_config.nil? }
  notifies :update, tomcat, :immediately if !crowd_config.nil? && ::File.exist?(crowd_config)
end

template node['blueprint']['webapps'][service_name]['application.properties']['cap.server.login.authentication'] do
  owner service_name
  group node['blueprint']['group']
  mode 0700
  sensitive true
  source 'jaas.conf.erb'
  variables(jaas_conf)
  notifies :update, tomcat, :immediately
end

coremedia_tomcat_service_lifecycle service_name do
  tomcat tomcat
  webapps [webapp]
  start_service start_service
  undeploy_unmanaged false
end
