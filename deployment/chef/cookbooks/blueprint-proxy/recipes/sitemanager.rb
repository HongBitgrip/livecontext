=begin
#<
This recipe installs virtual hosts for the CoreMedia Sitemanager.
#>
=end

include_recipe 'coremedia-proxy'

cms_ior_url = node['blueprint']['proxy']['virtual_host']['sitemanager']['cms_ior_url']
wfs_ior_url = node['blueprint']['proxy']['virtual_host']['sitemanager']['wfs_ior_url']
cms_context = URI(cms_ior_url).path.chomp('/ior')[1..-1]
wfs_context = URI(wfs_ior_url).path.chomp('/ior')[1..-1]

coremedia_proxy_webapp 'sitemanager' do
  server_name node['blueprint']['proxy']['virtual_host']['sitemanager']['server_name']
  server_aliases node['blueprint']['proxy']['virtual_host']['sitemanager']['server_aliases']
  servlet_context node['blueprint']['proxy']['virtual_host']['sitemanager']['context']
  servlet_host node['blueprint']['proxy']['virtual_host']['sitemanager']['host']
  servlet_port node['blueprint']['proxy']['virtual_host']['sitemanager']['port']
  cms_ior_url cms_ior_url
  cms_context cms_context
  wfs_ior_url wfs_ior_url
  wfs_context wfs_context
  rewrite_log_level node['blueprint']['proxy']['virtual_host']['sitemanager']['rewrite_log_level']
  proxy_template 'proxy/sitemanager.erb'
  proxy_template_cookbook 'blueprint-proxy'
end
