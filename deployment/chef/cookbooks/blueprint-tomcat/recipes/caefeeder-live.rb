=begin
#<
This recipe installs and configures the CoreMedia Blueprint Live CAE Feeder.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'caefeeder-live'
cache_dir = "#{node['blueprint']['cache_dir']}/#{service_name}"

# use default_unless to allow configuration in recipes run prior to this one
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('master-live-server')}/ior"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.collection.cae'] = 'live'
node.override['blueprint']['webapps'][service_name]['application.properties']['repository.blobCachePath'] = cache_dir

blueprint_tomcat_service service_name

tomcat = cm_tomcat(service_name)
directory cache_dir do
  owner tomcat.user
  group tomcat.group
  subscribes :create, tomcat, :immediately
end
