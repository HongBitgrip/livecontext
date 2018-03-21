=begin
#<
This recipe installs and configures the CoreMedia Blueprint Studio.
#>
=end
include_recipe 'blueprint-tomcat::_base'
service_name = 'studio'
cache_dir = "#{node['blueprint']['cache_dir']}/#{service_name}"
# The API key store directory and its parents should only be writable by the studio user,
# so that other service cannot exchange the key store, if compromised. Therefore, the
# key store should not be located in a generic directory like /var/opt/coremedia.
api_key_store_dir = '/var/opt/coremedia-apiKeyStore'

# use default_unless to allow configuration in recipes run prior to this one
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.url'] = "#{cm_webapp_url('content-management-server')}/ior"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.collection.cae'] = 'preview'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['solr.collection.content'] = 'studio'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['caplist'] = 'com.coremedia.cotopaxi.list.elastic.mongodb.MongoCapListConnectorFactory'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['elastic.solr.url'] = cm_webapp_url('solr')
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.heapCacheSize'] = 100 * 1024 * 1024
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.blobCacheSize'] = 10 * 1024 * 1024 * 1024
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.blobStreamingSizeThreshold'] = -1
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.blobStreamingThreads'] = -1
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['repository.maxCachedBlobSize'] = -1
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['studio.previewUrlPrefix'] = "//preview.#{node['blueprint']['hostname']}"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['studio.previewUrlWhitelist'] = "*.#{node['blueprint']['hostname']}"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['es.cae.http.host'] = node['blueprint']['hostname']
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['es.cae.protocol'] = 'http'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['link.urlPrefixType'] = 'live'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['externalpreview.restUrl'] = "http://preview.#{node['blueprint']['hostname']}/blueprint/servlet/service/externalpreview"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['externalpreview.previewUrl'] = "https://preview.#{node['blueprint']['hostname']}/blueprint/externalpreview"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['externalpreview.urlPrefix'] = "https://preview.#{node['blueprint']['hostname']}"
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['themeImporter.themeDeveloperGroups'] = 'developer'
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['themeImporter.apiKeyStore.basePath'] = api_key_store_dir

# The path where the transformed blobs should be saved persistently. If not set, then the feature is deactivated,
# and all transformed blobs are saved in memory
node.default_unless['blueprint']['webapps'][service_name]['application.properties']['com.coremedia.transform.blobCache.basePath'] = "#{cache_dir}/persistent-transformed-blobcache"

# we create the dir so we force this value
node.override['blueprint']['webapps'][service_name]['application.properties']['repository.blobCachePath'] = cache_dir
blueprint_tomcat_service service_name

tomcat = cm_tomcat(service_name)
directory cache_dir do
  owner tomcat.user
  group tomcat.group
  subscribes :create, tomcat, :immediately
end

directory api_key_store_dir do
  owner tomcat.user
  group tomcat.group
  mode 0700
end
