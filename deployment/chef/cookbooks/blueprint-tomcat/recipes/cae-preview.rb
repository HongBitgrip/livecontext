=begin
#<
This recipe installs and configures the CoreMedia Blueprint Preview CAE.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'cae-preview'
cache_dir = "#{node['blueprint']['cache_dir']}/#{service_name}"

# use default_unless to allow configuration in recipes run prior to this one
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.collection.cae'] = 'preview'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['elastic.solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.heapCacheSize'] = 100 * 1024 * 1024
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.blobCacheSize'] = 10 * 1024 * 1024 * 1024
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.blobStreamingSizeThreshold'] = -1
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.blobStreamingThreads'] = -1
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.maxCachedBlobSize'] = -1
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['link.urlPrefixType'] = 'preview'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['themeImporter.themeDeveloperGroups'] = 'developer'
# The path where the transformed blobs should be saved persistently. If not set, then the feature is deactivated,
# and all transformed blobs are saved in memory
node.override['blueprint']['webapps'][service_name]['application.properties']['com.coremedia.transform.blobCache.basePath'] = "#{cache_dir}/persistent-transformed-blobcache"

node.override['blueprint']['webapps'][service_name]['application.properties']['repository.blobCachePath'] = cache_dir

blueprint_tomcat_service service_name
tomcat = cm_tomcat(service_name)
directory cache_dir do
  owner tomcat.user
  group tomcat.group
  subscribes :create, tomcat, :immediately
end
