include_recipe 'coremedia-proxy::default'
include_recipe 'blueprint-base::default'

coremedia_proxy_webapp 'candy-studio' do
  server_name "candy-#{node['blueprint']['proxy']['virtual_host']['studio']['server_name']}"
  servlet_context ''
  proxy_template 'proxy/candy.erb'
  proxy_template_cookbook 'blueprint-proxy'
  rewrite_template 'rewrite/studio.erb'
  local_webapp_port '41080'
  ssl_proxy_verify false
  rewrite_log_level node['blueprint']['proxy']['virtual_host']['studio']['rewrite_log_level']
end

coremedia_proxy_webapp 'candy-studio-client' do
  # hmm not optimal but for the start
  server_name "candy-studio-client.#{node['blueprint']['hostname']}"
  rest_api_host node['blueprint']['proxy']['cms_host']
  rewrite_template 'rewrite/studio.erb'
  proxy_template 'proxy/candy-studio-client.erb'
  proxy_template_cookbook 'blueprint-proxy'
  ssl_proxy_verify false
  rewrite_log_level node['blueprint']['proxy']['virtual_host']['studio']['rewrite_log_level']
end

studio_jmx_url = 'service:jmx:rmi://localhost:41098/jndi/rmi://localhost:41099/studio'
mongo_client_uri = "mongodb://#{node['fqdn']}:27017/"

node.default['blueprint']['proxy']['candy_properties']['studio']['management.server.remote.url'] = studio_jmx_url
node.default['blueprint']['proxy']['candy_properties']['studio']['mongoDb.clientURI'] = mongo_client_uri
node.default['blueprint']['proxy']['candy_properties']['studio']['repository.blobCachePath'] = ''
node.default['blueprint']['proxy']['candy_properties']['studio']['com.coremedia.transform.blobCache.basePath'] = ''

node.default['blueprint']['proxy']['candy_properties']['studio-preview']['externalpreview.restUrl'] = "//candy-preview.#{node['blueprint']['hostname']}/blueprint/servlet/service/externalpreview"
node.default['blueprint']['proxy']['candy_properties']['studio-preview']['externalpreview.previewUrl'] = "//candy-preview.#{node['blueprint']['hostname']}/blueprint/externalpreview"
node.default['blueprint']['proxy']['candy_properties']['studio-preview']['externalpreview.urlPrefix'] = "//candy-preview.#{node['blueprint']['hostname']}"
node.default['blueprint']['proxy']['candy_properties']['studio-preview']['studio.previewUrlPrefix'] = "//candy-preview.#{node['blueprint']['hostname']}"
node.default['blueprint']['proxy']['candy_properties']['studio-preview']['management.server.remote.url'] = studio_jmx_url
node.default['blueprint']['proxy']['candy_properties']['studio-preview']['mongoDb.clientURI'] = mongo_client_uri
node.default['blueprint']['proxy']['candy_properties']['studio-preview']['repository.blobCachePath'] = ''
node.default['blueprint']['proxy']['candy_properties']['studio-preview']['com.coremedia.transform.blobCache.basePath'] = ''

if node['blueprint']['webapps']['studio']['application.properties']
  node['blueprint']['webapps']['cae-preview']['application.properties'].each_key { |prop_key|
    node.default_unless['blueprint']['proxy']['candy_properties']['studio'][prop_key] = node['blueprint']['webapps']['cae-preview']['application.properties'][prop_key]
    node.default_unless['blueprint']['proxy']['candy_properties']['studio-preview'][prop_key] = node['blueprint']['webapps']['cae-preview']['application.properties'][prop_key]
  }
end
