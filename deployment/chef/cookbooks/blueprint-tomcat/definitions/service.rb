=begin
#<
This definition installs apache tomcat, configures it, downloads a webapp and deploy it.  Because this definition wraps
the resources and definitions from the `coremedia_tomcat` cookbook, please see that cookbooks documentation for
further details if you need to alter it.

This definition builds on the convenience concept, that there may always be a global default for the tomcat config and the webapps.
Therfore the cm_tomcat_default method has been added to the library, the method gets a node attribute either defined at
node['blueprint']['tomcat'] or node['blueprint']['tomcat'][service_name] with precedence to the service specific one.

By default all simple values are derived automatically by the precendence described above, wheras complex configuration elements (hashes) are
always merged.

1. globals first i.e. `['blueprint']['tomcat'][attribute_name]`
2. if a `base_service_name` is defined we merge (override) them, i.e.  `['blueprint']['tomcat'][base_service_name][attribute_name]`
3. now the service specific attributes, i.e. `['blueprint']['tomcat'][service_name][attribute_name]`

This approach is applied to the following attribute paths:

* `catalina_opts`

```
['blueprint']['tomcat'][service_name]['catalina_opts']
    -> `['blueprint']['tomcat'][service_base_name]['catalina_opts']
        -> `['blueprint']['tomcat']['catalina_opts']`
```

* `context_config` for tomcat

```
['blueprint']['tomcat'][service_name]['context_config']
    -> `['blueprint']['tomcat'][service_base_name]['context_config']
        -> `['blueprint']['tomcat']['context_config']`
```

* `context_config` for webapp

```
['blueprint']['webapps'][service_name]['context_config']
    -> `['blueprint']['webapps'][service_base_name]['context_config']
```

* `application.properties` for webapp

```
['blueprint']['webapps'][service_name]['application.properties']
    -> `['blueprint']['webapps'][service_base_name][''application.properties'']
```

@param skip_lifecycle Set thist to true to skip the lifecycle at the end of this definition. See lifecycle section for mode details about this paramter.
@param base_service_name The service key from which to get the default component configuration before merging overrides using this service key

@section Lifecycle

By default this definition will deploy a webapp as it comes out of the maven repository, if you need to add or modify resources within the
webapp, you need to set `skip_lifecycle` to true add your resources after your call to the definition, notify the webapps update action and
create a new lifecycle, passing in tomcat and the webapp.

To get tomcat and the webapp resource, you can use the helper methods:

* cm_webapp(<resource_name>)
* cm_tomcat(<resource_name>)

because the resource_name is by this definition equal to the name of the definition, it should be straight forward.

@section Nexus artifacts

If you want to use metaversions like `X-SNAPSHOT`, `RELEASE` or `LATEST`,  you need to configure this definition to use the nexus REST API.
Set `node.default['blueprint']['nexus_url']` and `node.default['blueprint']['nexus_repo']` accordingly. Of course, you can set
individual `nexus_repo` repository names for each individual webapp and for the common_lib and shared_lib libraries.

@section Logging

If you want to manage the logging with chef, there is a `logback.xml.erb` template. By default you can set logger configurations
using a config hash, i.e.

```ruby
# on global tomcat level
node.default['blueprint']['tomcat']['logback_config']['logger']['org.springframework'] = 'warn'
# on base service level. Used in the cae-live recipe
node.default['blueprint']['tomcat']['cae-live']['logback_config']['logger']['org.springframework'] = 'info'
# on service level
node.default['blueprint']['tomcat']['cae-live-2']['logback_config']['logger']['org.springframework'] = 'debug'
# the result will be warn for all tomcats, info for all cae-live instances except instance 2 that has level debug.
```

@section example

```ruby
# installs everything but won't deploy the webapp or start the service
blueprint_tomcat_service 'content-management-server' do
  skip_lifecycle true
end

# we use the our convenience library to get the resource
webapp_res = cm_webapp('content-management-server')
tomcat_res = cm_tomcat('content-management-server')

template "#{webapp_res.path}/WEB-INF/properties/corem/secret.properties" do
  source 'properties.erb'
  owner webapp_res.owner
  group webapp_res.group
  variables :props => { 'a.key' => 'a.value' }
  notifies :update, webapp_res, :immediately
end

coremedia_tomcat_lifecycle 'content-management-server' do
  # with this line you honor the convenience flag to disable service start at all.
  start_service cm_tomcat_default('content-management-server', 'start_service')
  webapps [ webapp_res ]
  tomcat tomcat_res
end

```

#>
=end

define :blueprint_tomcat_service, skip_lifecycle: false, base_service_name: nil do
  # include base recipe to make sure all dirs are set up
  include_recipe 'blueprint-tomcat::_base'
  service_name = params[:name]
  base_service_name = params[:base_service_name]
  service_user = service_name
  service_group = node['blueprint']['group']
  service_dir = "#{node['blueprint']['base_dir']}/#{service_name}"

  # get defaults for all simple attributes
  tomcat_version = cm_tomcat_default(service_name, 'version', base_service_name)
  tomcat_server_libs = cm_tomcat_default(service_name, 'server_libs', base_service_name)
  tomcat_common_libs = cm_tomcat_default(service_name, 'common_libs', base_service_name)
  tomcat_catalina_properties = cm_tomcat_default(service_name, 'catalina_properties', base_service_name)
  tomcat_session_timeout = cm_tomcat_default(service_name, 'jvm_route', base_service_name)
  tomcat_java_home = cm_tomcat_default(service_name, 'java_home', base_service_name)
  tomcat_startup_wait = cm_tomcat_default(service_name, 'startup_wait', base_service_name)
  tomcat_start_levels = cm_tomcat_default(service_name, 'start_levels', base_service_name)
  tomcat_start_priority = cm_tomcat_default(service_name, 'start_priority', base_service_name)
  tomcat_max_threads = cm_tomcat_default(service_name, 'max_threads', base_service_name)
  tomcat_min_threads = cm_tomcat_default(service_name, 'min_threads', base_service_name)
  tomcat_debug = cm_tomcat_default(service_name, 'debug', base_service_name)
  tomcat_jmx_remote = !cm_tomcat_default(service_name, 'jmx_remote', base_service_name).nil?
  tomcat_jmx_remote_server_name = cm_tomcat_default(service_name, 'jmx_remote_server_name', base_service_name)
  tomcat_jmx_remote_use_local_ports = cm_tomcat_default(service_name, 'jmx_remote_use_local_ports', base_service_name)
  tomcat_jmx_remote_authenticate = cm_tomcat_default(service_name, 'jmx_remote_authenticate', base_service_name)
  tomcat_jmx_remote_ssl = cm_tomcat_default(service_name, 'jmx_remote_ssl', base_service_name)
  tomcat_jmx_remote_monitor_user = cm_tomcat_default(service_name, 'jmx_remote_monitor_user', base_service_name)
  tomcat_jmx_remote_monitor_password = cm_tomcat_default(service_name, 'jmx_remote_monitor_password', base_service_name)
  tomcat_jmx_remote_control_user = cm_tomcat_default(service_name, 'jmx_remote_control_user', base_service_name)
  tomcat_jmx_remote_control_password = cm_tomcat_default(service_name, 'jmx_remote_control_password', base_service_name)
  tomcat_start_service = cm_tomcat_default(service_name, 'start_service', base_service_name)
  tomcat_heap = cm_tomcat_default(service_name, 'heap', base_service_name)
  tomcat_perm = cm_tomcat_default(service_name, 'perm', base_service_name)
  tomcat_shutdown_force = cm_tomcat_default(service_name, 'shutdown_force', base_service_name)
  tomcat_shutdown_wait = cm_tomcat_default(service_name, 'shutdown_wait', base_service_name)
  tomcat_clean_log_dir_on_start = cm_tomcat_default(service_name, 'clean_log_dir_on_start', base_service_name)
  tomcat_keep_old_instances = cm_tomcat_default(service_name, 'keep_old_instances', base_service_name)

  webapp_group_id = node.deep_fetch('blueprint', 'webapps', service_name, 'group_id')
  webapp_group_id ||= node.deep_fetch('blueprint', 'webapps', base_service_name, 'group_id') if base_service_name
  webapp_artifact_id = node.deep_fetch('blueprint', 'webapps', service_name, 'artifact_id')
  webapp_artifact_id ||= node.deep_fetch('blueprint', 'webapps', base_service_name, 'artifact_id') if base_service_name
  webapp_version = node.deep_fetch('blueprint', 'webapps', service_name, 'version')
  webapp_version ||= node.deep_fetch('blueprint', 'webapps', base_service_name, 'version') if base_service_name
  webapp_classifier = node.deep_fetch('blueprint', 'webapps', service_name, 'classifier')
  webapp_classifier ||= node.deep_fetch('blueprint', 'webapps', base_service_name, 'classifier') if base_service_name
  webapp_context = node.deep_fetch('blueprint', 'webapps', service_name, 'context')
  webapp_context ||= node.deep_fetch('blueprint', 'webapps', base_service_name, 'context') if base_service_name
  webapp_explode = node.deep_fetch('blueprint', 'webapps', service_name, 'explode')
  webapp_explode ||= node.deep_fetch('blueprint', 'webapps', base_service_name, 'explode') if base_service_name
  webapp_nexus_repo = node.deep_fetch('blueprint', 'webapps', service_name, 'nexus_repo')
  webapp_nexus_repo ||= node.deep_fetch('blueprint', 'webapps', base_service_name, 'nexus_repo') if base_service_name
  webapp_nexus_repo ||= node['blueprint']['nexus_repo']
  webapp_checksum = node.deep_fetch('blueprint', 'webapps', base_service_name, 'checksum') if base_service_name

  # create service user
  coremedia_tomcat_service_user service_user do
    home service_dir
    group service_group
  end

  # create directory to store the webapps in. This is also the dir where webapps will be exploded if explode flag is set.
  webapps_dir = directory "#{service_dir}/webapps" do
    owner service_user
    group service_group
  end

  # now we get the defaults for all complex types. This will be done by merging.
  # ATTENTION: you cannot merge arrays but you should avoid arrays as attributes anyway as they can only be overridden
  # all or nothing. Arrays should only be used if order is important.
  # merge component config
  component_config_hash = Mash.new
  component_config_hash = Chef::Mixin::DeepMerge.hash_only_merge!(component_config_hash, node['blueprint']['webapps'][base_service_name]['application.properties']) if base_service_name && node.deep_fetch('blueprint', 'webapps', base_service_name, 'application.properties')
  component_config_hash = Chef::Mixin::DeepMerge.hash_only_merge!(component_config_hash, node['blueprint']['webapps'][service_name]['application.properties']) if node.deep_fetch('blueprint', 'webapps', service_name, 'application.properties')
  component_config_path = "#{service_dir}/#{service_name}.properties"
  node.override['blueprint']['tomcat'][service_name]['context_config']['env_entries']['propertieslocations']['value'] = "file://#{component_config_path}" unless component_config_hash.empty?

  # logging bootstrap
  node.override['blueprint']['tomcat'][service_name]['catalina_opts']['logback_config_file'] = "-Dlogging.config=file://#{service_dir}/logback.xml"
  node.override['blueprint']['tomcat'][service_name]['catalina_opts']['logging_dir'] = "-Dlog.dir=#{node['blueprint']['log_dir']}/#{service_name}"

  # merge catalina opts
  catalina_opts = Mash.new
  catalina_opts = Chef::Mixin::DeepMerge.hash_only_merge!(catalina_opts, node['blueprint']['tomcat']['catalina_opts']) if node.deep_fetch('blueprint', 'tomcat', 'catalina_opts')
  catalina_opts = Chef::Mixin::DeepMerge.hash_only_merge!(catalina_opts, node['blueprint']['tomcat'][base_service_name]['catalina_opts']) if base_service_name && node.deep_fetch('blueprint', 'tomcat', base_service_name, 'catalina_opts')
  catalina_opts = Chef::Mixin::DeepMerge.hash_only_merge!(catalina_opts, node['blueprint']['tomcat'][service_name]['catalina_opts']) if node.deep_fetch('blueprint', 'tomcat', service_name, 'catalina_opts')

  # logback config
  logback_config_hash = Mash.new
  logback_config_hash = Chef::Mixin::DeepMerge.hash_only_merge!(logback_config_hash, node['blueprint']['tomcat']['logback_config']) if node.deep_fetch('blueprint', 'tomcat', 'logback_config')
  logback_config_hash = Chef::Mixin::DeepMerge.hash_only_merge!(logback_config_hash, node['blueprint']['tomcat'][base_service_name]['logback_config']) if base_service_name && node.deep_fetch('blueprint', 'tomcat', base_service_name, 'logback_config')
  logback_config_hash = Chef::Mixin::DeepMerge.hash_only_merge!(logback_config_hash, node['blueprint']['tomcat'][service_name]['logback_config']) if node.deep_fetch('blueprint', 'tomcat', service_name, 'logback_config')
  logback_config_hash = Chef::Mixin::DeepMerge.hash_only_merge!(logback_config_hash, 'properties' => { 'application.name' => service_name, 'application.version' => node['blueprint']['webapps'][service_name]['version'] }) unless logback_config_hash.empty?

  # merge the tomcat context config
  tomcat_context_config = Mash.new
  tomcat_context_config = Chef::Mixin::DeepMerge.hash_only_merge!(tomcat_context_config, node['blueprint']['tomcat']['context_config']) if node.deep_fetch('blueprint', 'tomcat', 'context_config')
  tomcat_context_config = Chef::Mixin::DeepMerge.hash_only_merge!(tomcat_context_config, node['blueprint']['tomcat'][base_service_name]['context_config']) if base_service_name && node.deep_fetch('blueprint', 'tomcat', base_service_name, 'context_config')
  tomcat_context_config = Chef::Mixin::DeepMerge.hash_only_merge!(tomcat_context_config, node['blueprint']['tomcat'][service_name]['context_config']) if node.deep_fetch('blueprint', 'tomcat', service_name, 'context_config')

  # merge the webapp context config
  webapp_context_config = Mash.new
  webapp_context_config = Chef::Mixin::DeepMerge.hash_only_merge!(webapp_context_config, node['blueprint']['webapps'][base_service_name]['context_config']) if base_service_name && node.deep_fetch('blueprint', 'webapps', base_service_name, 'context_config')
  webapp_context_config = Chef::Mixin::DeepMerge.hash_only_merge!(webapp_context_config, node['blueprint']['webapps'][service_name]['context_config']) if node.deep_fetch('blueprint', 'webapps', service_name, 'context_config')

  # create the tomcat instance, we save it in a variable, for easier referencing in notifications. This prevents typos!
  tomcat = coremedia_tomcat service_name do
    source node['blueprint']['tomcat']['source'] unless node['blueprint']['tomcat']['source'].nil?
    source_checksum node['blueprint']['tomcat']['source_checksum'] unless node['blueprint']['tomcat']['source_checksum'].nil?
    jmx_remote_jar_source node['blueprint']['tomcat']['jmx_remote_jar_source'] unless node['blueprint']['tomcat']['jmx_remote_jar_source'].nil?
    jmx_remote_jar_source_checksum node['blueprint']['tomcat']['jmx_remote_jar_source_checksum'] unless node['blueprint']['tomcat']['jmx_remote_jar_source_checksum'].nil?
    path service_dir
    user service_user
    group service_group
    version tomcat_version
    port_prefix node['blueprint']['tomcat'][service_name]['port_prefix']
    server_libs tomcat_server_libs unless tomcat_server_libs.nil?
    common_libs tomcat_common_libs unless tomcat_common_libs.nil?
    catalina_properties tomcat_catalina_properties unless tomcat_catalina_properties.nil?
    catalina_opts catalina_opts.values.join(' ')
    heap tomcat_heap
    perm tomcat_perm
    session_timeout tomcat_session_timeout unless tomcat_session_timeout.nil?
    java_home tomcat_java_home unless tomcat_java_home.nil?
    startup_wait tomcat_startup_wait unless tomcat_startup_wait.nil?
    start_levels tomcat_start_levels unless tomcat_start_levels.nil?
    start_priority tomcat_start_priority unless tomcat_start_priority.nil?
    max_threads tomcat_max_threads unless tomcat_max_threads.nil?
    min_threads tomcat_min_threads unless tomcat_min_threads.nil?
    debug tomcat_debug unless tomcat_debug.nil?
    maven_repository_url node['blueprint']['maven_repository_url']
    nexus_url node['blueprint']['nexus_url'] if node['blueprint']['nexus_url']
    nexus_repo node['blueprint']['nexus_repo']
    shutdown_force unless tomcat_shutdown_force.nil?
    shutdown_wait unless tomcat_shutdown_wait.nil?
    jmx_remote tomcat_jmx_remote unless tomcat_jmx_remote.nil?
    jmx_remote_server_name tomcat_jmx_remote_server_name unless tomcat_jmx_remote_server_name.nil?
    jmx_remote_use_local_ports tomcat_jmx_remote_use_local_ports
    jmx_remote_authenticate tomcat_jmx_remote_authenticate unless tomcat_jmx_remote_authenticate.nil?
    jmx_remote_ssl tomcat_jmx_remote_ssl unless tomcat_jmx_remote_ssl.nil?
    jmx_remote_monitor_user tomcat_jmx_remote_monitor_user unless tomcat_jmx_remote_monitor_user.nil?
    jmx_remote_monitor_password tomcat_jmx_remote_monitor_password unless tomcat_jmx_remote_monitor_password.nil?
    jmx_remote_control_user tomcat_jmx_remote_control_user unless tomcat_jmx_remote_control_user.nil?
    jmx_remote_control_password tomcat_jmx_remote_control_password unless tomcat_jmx_remote_control_password.nil?
    log_dir "#{node['blueprint']['log_dir']}/#{service_name}"
    context_config tomcat_context_config
    clean_log_dir_on_start tomcat_clean_log_dir_on_start
    keep_old_instances tomcat_keep_old_instances
  end

  # download the webapp and outfit it with a context if desired.
  # again, we save it in a variable, for easier referencing in notifications. This prevents typos!
  webapp = coremedia_tomcat_webapp service_name do
    group_id webapp_group_id
    artifact_id webapp_artifact_id
    version webapp_version
    classifier webapp_classifier
    checksum webapp_checksum
    # unless the exploded flag is set, we do unexploded deployment.
    path "#{webapps_dir.path}/#{service_name}#{webapp_explode ? '' : '.war'}"
    owner service_user
    group service_group
    context webapp_context
    context_config webapp_context_config
    maven_repository_url node['blueprint']['maven_repository_url']
    nexus_url node['blueprint']['nexus_url'] if node['blueprint']['nexus_url']
    nexus_repo webapp_nexus_repo
  end

  # write the application config we reference in the env entry propertieslocations.
  # if we want to improve the definition to be able to deploy multiple webapps, we will use the context of the webapp
  # to configure that env entry.
  template "#{service_name}-application-config" do
    path component_config_path
    source 'properties.erb'
    cookbook 'blueprint-tomcat'
    variables props: component_config_hash
    owner service_user
    group service_group
    sensitive true
    # now we notify the tomcat to change its updated_by_last_action flag. See how we use the stored reference, otherwise
    # we would have written 'notifies :update, "coremedia_tomcat[#{service_name}]", :immediately
    notifies :update, tomcat, :immediately
    # only create the config file if the webapp can be configured this way. Exception is sitemanager.
    not_if { component_config_hash.empty? }
  end

  template "#{service_name}-logging-config" do
    path "#{service_dir}/logback.xml"
    cookbook 'blueprint-tomcat'
    owner service_user
    group service_group
    source 'logback.xml.erb'
    variables config: logback_config_hash
    not_if { logback_config_hash.empty? }
  end

  # if we need to add further files that affect the lifecycle, we need to skip the lifecycle and define it later to close the installation
  # after our changes.
  # ignore foodcritic FC023 here
  unless params[:skip_lifecycle] # ~FC023
    coremedia_tomcat_service_lifecycle service_name do
      tomcat tomcat
      webapps [webapp]
      # if the flag ['blueprint']['tomcat'][service_name]['start_service'] or its global default is false, we skip the start.
      start_service tomcat_start_service
      undeploy_unmanaged false
    end
  end
end
