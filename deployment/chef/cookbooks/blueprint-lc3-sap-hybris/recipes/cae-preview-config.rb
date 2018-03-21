=begin
#<
This recipe configures the CoreMedia Blueprint Preview CAE.
#>
=end
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.hybris.host'] = node['blueprint']['lc3-sap-hybris']['host']
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.apache.hybris.host'] = "shop-preview-apparel.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.hybris.storeFrontUrl'] = "https://shop-preview-apparel.#{node['blueprint']['hostname']}/yacceleratorstorefront/"

# inject wcs configuration
node['blueprint']['lc3-sap-hybris']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-preview']['application.properties'][k] = v
end
