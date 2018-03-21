=begin
#<
This recipe installs and configures the CoreMedia Blueprint Elastic Worker.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'elastic-worker'

# use default_unless to allow configuration in recipes run prior to this one
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['elastic.solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['elastic.social.mail.smtp.server'] = 'localhost'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['elastic.social.mail.smtp.port'] = 25

blueprint_tomcat_service service_name
