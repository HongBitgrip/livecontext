=begin
#<
This recipe installs and configures the CoreMedia Blueprint Content Feeder.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'content-feeder'

# use default_unless to allow configuration in recipes run prior to this one
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.collection.content'] = 'studio'

blueprint_tomcat_service service_name
