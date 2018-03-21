=begin
#<
This recipe installs and configures the CoreMedia Blueprint Workflow Server.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'workflow-server'

# use default_unless to allow configuration in recipes run prior to this one
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['cap.client.server.ior.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['workflow.server.ORBServerHost'] = node['blueprint']['hostname']

blueprint_tomcat_service service_name
