=begin
#<
This recipe configures the CoreMedia Blueprint Live CAE.
#>
=end

node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.hybris.host'] = node['blueprint']['lc3-sap-hybris']['host']
node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.apache.hybris.host'] = "shop-apparel.#{node['blueprint']['hostname']}"

# inject wcs configuration
node['blueprint']['lc3-sap-hybris']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-live']['application.properties'][k] = v
end
