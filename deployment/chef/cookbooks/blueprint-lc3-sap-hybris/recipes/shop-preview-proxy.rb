=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Hybris Preview Shop.
#>
=end
include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'sap-hybris-commerce-shop-preview' do
  server_name node['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['server_name']
  server_aliases node['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['server_aliases']
  time_travel_alias node['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['time_travel_alias']
  hybris_host node['blueprint']['lc3-sap-hybris']['host']
  rewrite_log_level node['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-lc3-sap-hybris'
  site_server_name node['blueprint']['proxy']['virtual_host']['preview']['server_name']
  preview true
  headers node['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['headers']
end
