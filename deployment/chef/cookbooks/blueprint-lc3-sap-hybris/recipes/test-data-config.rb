=begin
#<
This recipe sets properties only necessary if the test content is being used
#>
=end
node.default['blueprint']['webapps']['cae-live']['application.properties']['blueprint.site.mapping.apparel'] = "https://apparel.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['studio']['application.properties']['blueprint.site.mapping.apparel'] = "https://preview.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-preview']['application.properties']['blueprint.site.mapping.apparel'] = "https://preview.#{node['blueprint']['hostname']}"
