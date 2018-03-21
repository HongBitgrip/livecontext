=begin
#<
This recipe uses [FPM](https://github.com/jordansissel/fpm) to create RPMs from the files installed with the recipes from the
blueprint-tomcat and blueprint-tools cookbooks.

During this deployment no services will get started.

You can use this recipe within your CI or locally to create your deployment packages.

The concept is that you configure your deployment using chef attributes as described in the blueprint-* cookbooks
and then use this recipe to create rpms out of it.

This way you can completely configure all aspects that are configurable in the chef deployment approach. See the README.md files
of:

* blueprint-base
* blueprint-tomcat
* blueprint-tools

Because FPM supports many packaging types this approach could be extended for deb, solaris and other package formats.
Currently we only support RPM.

When the recipe finishes, all rpms should lie in the directory defined by `['blueprint']['dev']['rpm']['dir']`.
#>
=end

# install the fpm tool
include_recipe 'fpm-tng::default'
# set target dir on fpm resource
node.default['fpm_tng']['package_dir'] = node['blueprint']['dev']['rpm']['dir']
# create target dir
directory node['blueprint']['dev']['rpm']['dir']
package_prefix = node['blueprint']['dev']['rpm']['package_prefix']
package_group = node['blueprint']['group']
package_user = node['blueprint']['user']
package_version = node['blueprint']['dev']['rpm']['version']

# delete the working dir TODO: may be obsolete
directory node['fpm_tng']['build_dir'] do
  action :delete
  recursive true
end
# we need the rpm-build tool
package 'rpm-build'

# the version of tomcat to derive the tomcat dir from.
tomcat_dir = "apache-tomcat-#{node['blueprint']['tomcat']['version']}"
# an array of service confs. Each item is a hash with the keys name and config_files and optional recipe_name
# the config_files key points to an array of relative paths to files that should be marked in the rpm as config files.
services = [{ name: 'content-management-server', config_files: ['content-management-server.properties'] },
            { name: 'workflow-server', config_files: ['workflow-server.properties'] },
            { name: 'content-feeder', config_files: ['content-feeder.properties'] },
            { name: 'user-changes', config_files: ['user-changes.properties'] },
            { name: 'elastic-worker', config_files: ['elastic-worker.properties'] },
            { name: 'caefeeder-preview', config_files: ['caefeeder-preview.properties'] },
            { name: 'cae-preview', config_files: ['cae-preview.properties'] },
            { name: 'studio', config_files: ['studio.properties'] },
            { name: 'sitemanager', config_files: %w(editor.properties capclient.properties workflowclient.properties).map { |props| "#{tomcat_dir}/webapps/editor-webstart/webstart/properties/corem/#{props}" } },
            { name: 'master-live-server', config_files: ['master-live-server.properties'] },
            { name: 'caefeeder-live', config_files: ['caefeeder-live.properties'] },
            { name: 'cae-live-1', recipe_name: 'cae-live', config_files: ['cae-live-1.properties'] }]
# config files common to all tomcat services
common_tomcat_config_files = ['jmxremote.password',
                              'jmxremote.access',
                              "#{tomcat_dir}/conf/server.xml",
                              "#{tomcat_dir}/bin/setenv.sh",
                              "#{tomcat_dir}/conf/catalina.properties",
                              "#{tomcat_dir}/conf/logging.properties",
                              "#{tomcat_dir}/conf/web.xml",
                              "#{tomcat_dir}/conf/context.xml"]

# iterate over all services and
# 1. install service using the recipes from blueprint-tomcat cookbook
# 2. create a pre-install script to create service users and groups
# 3. create the rpm using fpm LWRP
# deactivate service starts
node.override['blueprint']['tomcat']['start_service'] = false
services.each do |service_conf|
  service_name = service_conf[:name]
  service_dir = "#{node['blueprint']['base_dir']}/#{service_name}"
  include_recipe "blueprint-tomcat::#{service_conf[:recipe_name].nil? ? service_name : service_conf[:recipe_name]}"

  execute "chown -R #{service_name}:#{package_group} #{service_dir}"

  preinstall = template "/tmp/preinstall-#{service_name}.sh" do
    source 'preinstall.sh.erb'
    variables(service_name: service_name,
              service_user: service_name,
              service_group: package_group,
              service_group_user: package_user,
              service_dir: service_dir,
              restart_services: [service_name],
              base_dir: node['blueprint']['base_dir'])
  end

  postinstall = template "/tmp/postinstall-#{service_name}.sh" do
    source 'postinstall.sh.erb'
    variables(install_user: service_name,
              install_group: package_group,
              install_dir: service_dir,
              restart_services: [service_name])
  end

  config_files = (common_tomcat_config_files + service_conf[:config_files]).map { |path| "#{service_dir}/#{path}" }

  args = []
  args << "--before-install #{preinstall.path}"
  args << "--after-install #{postinstall.path}"
  config_files.each do |config_file|
    args << "--config-files #{config_file}"
  end
  args << "#{service_dir} /etc/init.d/#{service_name} /var/log/coremedia/#{service_name}"

  fpm_tng_package "#{package_prefix}#{service_name}" do
    output_type 'rpm'
    version package_version
    input_args args
  end
end

# An array of tool configurations. Each item has a key mame that must match the recipe name in blueprint-tools. There must
# also be a key config_files that maps to an array of relative paths of files that should be marked as config files for the rpm.
# An optional key recipe name may be set to define a tool that comes from a recipe not named like the tool.
tools = [{ name: 'content-management-server-tools', config_files: ['properties/corem/capclient.properties'] },
         { name: 'master-live-server-tools', config_files: ['properties/corem/capclient.properties'] },
         { name: 'replication-live-server-tools', config_files: ['properties/corem/capclient.properties'] },
         { name: 'workflow-server-tools', config_files: ['properties/corem/capclient.properties'] },
         { name: 'theme-importer-tools', config_files: ['properties/corem/theme-importer.properties', 'properties/corem/capclient.properties'] },
         { name: 'caefeeder-preview-tools', config_files: ['properties/corem/resetcaefeeder.properties'] },
         { name: 'caefeeder-live-tools', config_files: ['properties/corem/resetcaefeeder.properties'] }]
common_tools_config_files = ['bin/pre-config.jpif']

# Iterate over the tools map and:
# 1. install the tool using the recipe from the blueprint-tools cookbook
# 2. create a preinstall script to create the global group and user
# 3. create the rpm using the fpm tool
tools.each do |tool_conf|
  tool_name = tool_conf[:name]
  tool_dir = "/opt/coremedia/#{tool_name}"
  include_recipe "blueprint-tools::#{tool_conf[:recipe_name].nil? ? tool_name : tool_conf[:recipe_name]}"

  preinstall = template "/tmp/preinstall-#{tool_name}.sh" do
    source 'preinstall.sh.erb'
    variables(service_group: package_group,
              service_group_user: package_user,
              base_dir: node['blueprint']['base_dir'])
  end

  postinstall = template "/tmp/postinstall-#{tool_name}.sh" do
    source 'postinstall.sh.erb'
    variables(install_user: package_user,
              install_group: package_group,
              install_dir: tool_dir)
  end

  config_files = (common_tools_config_files + tool_conf[:config_files]).map { |path| "#{tool_dir}/#{path}" }
  args = []
  args << "--before-install #{preinstall.name}"
  args << "--after-install #{postinstall.path}"
  args << "--rpm-user #{package_user}"
  args << "--rpm-group #{package_group}"
  config_files.each do |config_file|
    args << "--config-files #{config_file}"
  end
  args << tool_dir

  fpm_tng_package "#{package_prefix}#{tool_name}" do
    output_type 'rpm'
    version package_version
    input_args args
  end
end
