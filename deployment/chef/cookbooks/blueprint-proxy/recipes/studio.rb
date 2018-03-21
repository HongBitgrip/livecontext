=begin
#<
This recipe installs virtual hosts for the CoreMedia Studio.
#>
=end

include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'studio' do
  server_name node['blueprint']['proxy']['virtual_host']['studio']['server_name']
  server_aliases node['blueprint']['proxy']['virtual_host']['studio']['server_aliases']
  servlet_context node['blueprint']['proxy']['virtual_host']['studio']['context']
  cluster('default' => { 'host' => node['blueprint']['proxy']['virtual_host']['studio']['host'], 'port' => node['blueprint']['proxy']['virtual_host']['studio']['port'] })
  rewrite_log_level node['blueprint']['proxy']['virtual_host']['studio']['rewrite_log_level']
end
