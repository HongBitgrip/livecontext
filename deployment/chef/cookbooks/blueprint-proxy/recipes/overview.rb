=begin
#<
creates a simple overview page with all necessary links at `overview.<hostname>` for dev systems
#>
=end
include_recipe 'blueprint-base::default'
include_recipe 'coremedia-proxy'

www_dir = directory "#{node['blueprint']['base_dir']}/overview" do
  owner node['blueprint']['user']
  group node['blueprint']['group']
end

template "#{www_dir.path}/index.html" do
  source node['blueprint']['proxy']['overview_template']['source']
  cookbook node['blueprint']['proxy']['overview_template']['cookbook']
  owner node['blueprint']['user']
  group node['blueprint']['group']
  variables(other_links_source: node['blueprint']['proxy']['overview_template']['other_links_source'],
            other_links_cookbook: node['blueprint']['proxy']['overview_template']['other_links_cookbook'])
end

remote_directory "#{www_dir.path}/assets" do
  source 'overview/assets'
  owner node['blueprint']['user']
  group node['blueprint']['group']
end

template "#{www_dir.path}/jmx-java-mission-control.xml" do
  source 'overview/jmx-jmc.xml.erb'
  owner node['blueprint']['user']
  group node['blueprint']['group']
end

web_app '_overview' do
  application_name 'overview'
  server_name "overview.#{node['blueprint']['hostname']}"
  template 'overview/overview_vhost.conf.erb'
  docroot www_dir.path
end

template "#{www_dir.path}/candy-studio.properties" do
  variables(props: node['blueprint']['proxy']['candy_properties']['studio'])
  owner node['blueprint']['user']
  group node['blueprint']['group']
  source 'properties.erb'
  cookbook 'blueprint-tomcat'
  sensitive true
  only_if { node.recipe? 'blueprint-proxy::candy-studio-proxy' }
end

template "#{www_dir.path}/candy-preview.properties" do
  variables(props: node['blueprint']['proxy']['candy_properties']['preview'])
  owner node['blueprint']['user']
  group node['blueprint']['group']
  source 'properties.erb'
  cookbook 'blueprint-tomcat'
  sensitive true
  only_if { node.recipe? 'blueprint-proxy::candy-preview-proxy' }
end

template "#{www_dir.path}/candy-studio-preview.properties" do
  variables(props: node['blueprint']['proxy']['candy_properties']['studio-preview'])
  owner node['blueprint']['user']
  group node['blueprint']['group']
  source 'properties.erb'
  cookbook 'blueprint-tomcat'
  sensitive true
  only_if do
    node.recipe?('blueprint-proxy::candy-preview-proxy') &&
      node.recipe?('blueprint-proxy::candy-studio-proxy')
  end
end

# restart it immediately and not in delayed phase so we can use the overview before the content import has been finished.
log 'restart apache immediately' do
  notifies :restart, 'service[apache2]', :immediately
end
