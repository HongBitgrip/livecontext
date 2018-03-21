=begin
#<
This recipe installs and configures the CoreMedia Sitemanager WebStart App.
#>
=end
# include base recipe to make sure all dirs are set up
include_recipe 'blueprint-tomcat::_base'
service_name = 'sitemanager'
# we cannot directly use the helper method in a definitions body, otherwise it gets evaluated too early
start_service = cm_tomcat_default(service_name, 'start_service')
# because webstart webapp is started on the client, using the downloaded configuration files, we need to make sure, that the urls are
# configured against urls where the host can be resolved anywhere
# use default_unless to allow configuration in recipes run prior to this one
node.default_unless['blueprint']['webapps'][service_name]['capclient.properties']['cap.client.server.ior.url'] = "#{cm_webapp_url('content-management-server', node['blueprint']['hostname'])}/ior"
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['editor.configuration'] = "#{cm_webapp_url(service_name, node['blueprint']['hostname'])}/webstart/properties/corem/editor.xml"
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['editor.startup.configuration'] = "#{cm_webapp_url(service_name, node['blueprint']['hostname'])}/webstart/properties/corem/editor-startup.xml"
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['editor.display.embedded.view'] = 'true'
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['usermanager.searchResultSize'] = '490'
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['user.manual.url'] = 'https://documentation.coremedia.com/editor-user/1801.2/editor-user-{0}.pdf'
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['editor.richtext.model.traditional'] = 'false'
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['show.content.change.popups'] = 'true'
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['login.username'] = ''
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['login.domain'] = ''
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['login.password'] = ''
node.default_unless['blueprint']['webapps'][service_name]['editor.properties']['login.immediate'] = ''
node.default_unless['blueprint']['webapps'][service_name]['workflowclient.properties']['cap.client.server.ior.url'] = "#{cm_webapp_url('content-management-server', node['blueprint']['hostname'])}/ior"
node.default_unless['blueprint']['webapps'][service_name]['workflowclient.properties']['workflow.client.server.ior.url'] = "#{cm_webapp_url('workflow-server', node['blueprint']['hostname'])}/ior"
node.default_unless['blueprint']['webapps'][service_name]['jnlp_token']['SITE_MANAGER_ORB_SOCKET_FACTORY'] = 'com.sun.corba.se.impl.legacy.connection.DefaultSocketFactory'
node.default_unless['blueprint']['webapps'][service_name]['jnlp_token']['SITE_MANAGER_CLEAR_TEXT_PORTS'] = '14300,14305'
node.default_unless['blueprint']['webapps'][service_name]['jnlp_token']['SITE_MANAGER_SSL_PORTS'] = '0'
node.default_unless['blueprint']['webapps'][service_name]['jnlp_token']['SITE_MANAGER_SSL_KEYSTORE_FILENAME'] = ''
node.default_unless['blueprint']['webapps'][service_name]['jnlp_token']['SITE_MANAGER_SSL_PASSPHRASE'] = ''

blueprint_tomcat_service service_name do
  skip_lifecycle true
end
webapp = cm_webapp(service_name)
tomcat = cm_tomcat(service_name)

jnlp_file = "#{webapp.path}/webstart/editor-webstart.jnlp"
%w(SITE_MANAGER_ORB_SOCKET_FACTORY SITE_MANAGER_CLEAR_TEXT_PORTS SITE_MANAGER_SSL_PORTS SITE_MANAGER_SSL_KEYSTORE_FILENAME SITE_MANAGER_SSL_PASSPHRASE).each do |token|
  execute "sed -i s/@#{token}@/#{node['blueprint']['webapps'][service_name]['jnlp_token'][token]}/ #{jnlp_file}" do
    user webapp.owner
    group webapp.group
    notifies :update, webapp, :immediately
    only_if "grep @#{token}@ #{jnlp_file}"
  end
end

# sitemanager configuration
%w(capclient editor workflowclient).each do |res|
  template "#{webapp.path}/webstart/properties/corem/#{res}.properties" do
    source 'properties.erb'
    owner webapp.owner
    group webapp.group
    variables props: node['blueprint']['webapps'][service_name]["#{res}.properties"]
    notifies :update, webapp, :immediately
  end
end

# now we can add the lifecycle
coremedia_tomcat_service_lifecycle service_name do
  tomcat tomcat
  webapps [webapp]
  undeploy_unmanaged false
  start_service start_service
end

node.default_unless['blueprint']['proxy']['virtual_host']['sitemanager']['http_port'] = "#{node['blueprint']['tomcat'][service_name]['port_prefix']}80"
