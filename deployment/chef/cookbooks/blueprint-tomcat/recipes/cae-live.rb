=begin
#<
This recipe installs and configures the CoreMedia Blueprint Live CAE.

The configuration hash is defined below `node['blueprint']['tomcat']['cae-live']`.
You can install multiple instances of the live cae on the same node by setting `node['blueprint']['tomcat']['cae-live']['instances']`.
By default one instance will be installed. All instances will be configured identically unless
you define a configuration hash at `node['blueprint']['tomcat']['cae-live-<INSTANCE_NUMBER>']['application.properties']`.

Other configurations possible are:
- `node['blueprint']['tomcat']['cae-live-X']['port_prefix']` - by default its "42<INSTANCE_NUMBER>" or 421 in case of just one instance.
- `node['blueprint']['tomcat']['cae-live-X']['heap']` - the amount of heap or 1024m if not set.
- `node['blueprint']['tomcat']['cae-live-X']['perm']` - the amount of perm or 128m if not set.
- `node['blueprint']['webapps']['cae-live-X']['group_id']` - the maven group_id of the artifact or node['blueprint']['webapps']['cae-live']['group_id'] if not set.
- `node['blueprint']['webapps']['cae-live-X']['artifact_id']` - the maven artifact_id of the artifact or node['blueprint']['webapps']['cae-live']['artifact_id'] if not set.

@section Sitemap generation
Because the sitemap should only be generated on one cae, you can disable sitemap generation for any cae on a node by setting
`node['blueprint']['webapps']['cae-live']['sitemap-cae']` to `nil` on that nodes environment, role or role recipe.

#>
=end

include_recipe 'blueprint-tomcat::_base'
base_service_name = 'cae-live'

node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('master-live-server')}/ior"
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['solr.collection.cae'] = 'live'
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['elastic.solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['repository.heapCacheSize'] = 100 * 1024 * 1024
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['repository.blobCacheSize'] = 10 * 1024 * 1024 * 1024
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['repository.blobStreamingSizeThreshold'] = -1
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['repository.blobStreamingThreads'] = -1
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['repository.maxCachedBlobSize'] = -1
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['cae.is.standalone'] = false
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['view.debug.enabled'] = false
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['blueprint.sitemap.target.root'] = "#{node['blueprint']['cache_dir']}/sitemap"
node.default_unless['blueprint']['webapps'][base_service_name]['application.properties']['link.urlPrefixType'] = 'live'
# by default disable periodic sitemap generation.
node.default['blueprint']['webapps'][base_service_name]['application.properties']['blueprint.sitemap.starttime'] = '-'
(1..node['blueprint']['tomcat'][base_service_name]['instances']).to_a.each do |i|
  service_name = "#{base_service_name}-#{i}"
  cache_dir = "#{node['blueprint']['cache_dir']}/#{service_name}"
  port_prefix = node.deep_fetch('blueprint', 'tomcat', service_name, 'port_prefix')
  unless port_prefix
    port_prefix = node.deep_fetch('blueprint', 'tomcat', base_service_name, 'port_prefix') + i - 1
    node.default['blueprint']['tomcat'][service_name]['port_prefix'] = port_prefix
  end
  if node.deep_fetch('blueprint', 'tomcat', service_name, 'sitemap', 'enabled')
    start_time = node.deep_fetch('blueprint', 'tomcat', service_name, 'sitemap', 'start_time')
    node.default_unless['blueprint']['webapps'][service_name]['application.properties']['blueprint.sitemap.starttime'] = start_time.nil? ? '+200' : start_time
    # Set the port correctly for the sitemap generation
    node.default_unless['blueprint']['webapps'][service_name]['application.properties']['blueprint.sitemap.cae.port'] = "#{port_prefix}80"
  end
  node.override['blueprint']['webapps'][service_name]['application.properties']['repository.blobCachePath'] = cache_dir

  # The path where the transformed blobs should be saved persistently. If not set, then the feature is deactivated,
  # and all transformed blobs are saved in memory
  node.override['blueprint']['webapps'][service_name]['application.properties']['com.coremedia.transform.blobCache.basePath'] = "#{cache_dir}/persistent-transformed-blobcache"

  blueprint_tomcat_service service_name do
    base_service_name base_service_name
  end

  tomcat = cm_tomcat(service_name)
  directory cache_dir do
    owner tomcat.user
    group tomcat.group
    subscribes :create, tomcat, :immediately
  end
end
