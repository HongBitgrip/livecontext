use_inline_resources

action :install do
  version = Chef::Version.new(new_resource.version)
  tomcat_url = new_resource.source.nil? ? "http://archive.apache.org/dist/tomcat/tomcat-#{version.major}/v#{version}/bin/apache-tomcat-#{version}.zip" : new_resource.source
  jmx_remote_url = new_resource.jmx_remote_jar_source.nil? ? "http://archive.apache.org/dist/tomcat/tomcat-#{version.major}/v#{version}/bin/extras/catalina-jmx-remote.jar" : new_resource.jmx_remote_jar_source
  tomcat_dir = "#{new_resource.path}/apache-tomcat-#{version}"
  current = "#{new_resource.path}/current"
  tomcat_zip = "#{Chef::Config[:file_cache_path]}/apache-tomcat-#{version}.zip"
  server_lib_dir = "#{new_resource.path}/server-lib"
  common_lib_dir = "#{new_resource.path}/common-lib"
  package 'unzip'

  directory new_resource.path do
    recursive true
    action :create
    owner new_resource.user
    group new_resource.group
    new_resource.updated_by_last_action(false)
  end

  directory "#{new_resource.path}/service-hooks" do
    recursive true
    action :create
    owner new_resource.user
    group new_resource.group
    new_resource.updated_by_last_action(false)
  end

  remote_file tomcat_zip do
    source tomcat_url
    checksum new_resource.source_checksum unless new_resource.source_checksum.nil?
    owner new_resource.user
    group new_resource.group
    action :create_if_missing
    backup false
    new_resource.updated_by_last_action(false)
  end

  execute "extract tomcat to #{new_resource.path}" do
    command "unzip -oqq #{tomcat_zip} -d #{new_resource.path} -x apache-tomcat-#{version}/webapps/**"
    user new_resource.user
    group new_resource.group
    not_if { ::File.exist?(tomcat_dir) }
    new_resource.updated_by_last_action(false)
  end

  link "#{new_resource.path}/current" do
    to tomcat_dir
  end

  execute "cleanup old tomcat installations for #{new_resource.name}" do
    command "find #{new_resource.path} -type d -name \"apache-tomcat-*\" -not -name \"apache-tomcat-#{new_resource.version}\" -prune -exec rm -rf {} \\;"
    user new_resource.user
    group new_resource.group
    new_resource.updated_by_last_action(false)
    action new_resource.keep_old_instances ? :nothing : :run
  end

  remote_file "#{new_resource.path}/current/lib/catalina-jmx-remote.jar" do
    source jmx_remote_url
    checksum new_resource.jmx_remote_jar_source_checksum unless new_resource.jmx_remote_jar_source_checksum.nil?
    owner new_resource.user
    group new_resource.group
    backup false
    mode 00555
    action :create_if_missing
  end

  template "#{new_resource.path}/jmxremote.access" do
    source 'jmxremote.access.erb'
    cookbook 'coremedia_tomcat'
    owner new_resource.user
    group new_resource.group
    mode 00500
    variables(:jmx_remote_monitor_user => new_resource.jmx_remote_monitor_user,
              :jmx_remote_control_user => new_resource.jmx_remote_control_user)
  end

  template "#{new_resource.path}/jmxremote.password" do
    source 'jmxremote.password.erb'
    cookbook 'coremedia_tomcat'
    owner new_resource.user
    group new_resource.group
    mode 00500
    sensitive true
    variables(:jmx_remote_monitor_user => new_resource.jmx_remote_monitor_user,
              :jmx_remote_control_user => new_resource.jmx_remote_control_user,
              :jmx_remote_monitor_password => new_resource.jmx_remote_monitor_password,
              :jmx_remote_control_password => new_resource.jmx_remote_control_password)
  end

  %w(catalina.sh startup.sh shutdown.sh).each do |exec_file|
    file "#{tomcat_dir}/bin/#{exec_file}" do
      mode 00755
      new_resource.updated_by_last_action(false)
    end
  end

  # fixes https://access.redhat.com/solutions/2067013 for REDHAT 7.2
  link "systemV-#{new_resource.name}" do
    to "#{current}/bin/init.sh"
    target_file "/etc/init.d/#{new_resource.name}"
    new_resource.updated_by_last_action(false)
    action :delete
    only_if { ::File.exist?("/etc/init.d/#{new_resource.name}") && ::File.symlink?("/etc/init.d/#{new_resource.name}") }
  end

  template "/etc/init.d/#{new_resource.name}" do
    source 'service.init.erb'
    cookbook 'coremedia_tomcat'
    user new_resource.user
    group new_resource.group
    mode 00755
    variables(:name => new_resource.name,
              :start_levels => new_resource.start_levels,
              :start_priority => new_resource.start_priority,
              :stop_priority => 100 - new_resource.start_priority,
              :user => new_resource.user,
              :description => new_resource.name,
              :tomcat_dir => current,
              :service_dir => new_resource.path,
              :shutdown_wait => new_resource.shutdown_wait,
              :shutdown_force => new_resource.shutdown_force,
              :log_dir => new_resource.log_dir,
              :clean_log_dir_on_start => new_resource.clean_log_dir_on_start)
  end

  directory "#{tomcat_dir}/conf/Catalina" do
    owner new_resource.user
    group new_resource.group
    new_resource.updated_by_last_action(false)
  end

  directory "#{tomcat_dir}/conf/Catalina/localhost" do
    owner new_resource.user
    group new_resource.group
    new_resource.updated_by_last_action(false)
  end

  manage_libs(new_resource.server_libs, server_lib_dir)
  manage_libs(new_resource.common_libs, common_lib_dir)

  template "#{tomcat_dir}/conf/catalina.properties" do
    source "#{version.major}.#{version.minor}/catalina.properties.erb"
    cookbook 'coremedia_tomcat'
    variables :catalina_properties => new_resource.catalina_properties
    owner new_resource.user
    group new_resource.group
  end

  template "#{tomcat_dir}/conf/server.xml" do
    source "#{version.major}.#{version.minor}/server.xml.erb"
    cookbook 'coremedia_tomcat'
    owner new_resource.user
    group new_resource.group
    variables(:shutdown_port => new_resource.shutdown_port,
              :max_threads => new_resource.max_threads,
              :min_threads => new_resource.min_threads,
              :http_port => new_resource.http_port,
              :ajp_port => new_resource.ajp_port,
              :jvm_route => new_resource.jvm_route,
              :jmx_remote => new_resource.jmx_remote,
              :jmx_remote_registry_port => new_resource.jmx_remote_registry_port,
              :jmx_remote_server_port => new_resource.jmx_remote_server_port,
              :jmx_remote_use_local_ports => new_resource.jmx_remote_use_local_ports,
              :access_log => new_resource.access_log)
  end

  template "#{tomcat_dir}/conf/web.xml" do
    source "#{version.major}.#{version.minor}/web.xml.erb"
    cookbook 'coremedia_tomcat'
    owner new_resource.user
    group new_resource.group
    variables :session_timeout => new_resource.session_timeout
  end

  template "#{tomcat_dir}/conf/context.xml" do
    source new_resource.context_template
    cookbook new_resource.context_template_cookbook
    owner new_resource.user
    group new_resource.group
    variables new_resource.context_config.nil? ? {} : new_resource.context_config
  end

  template "#{tomcat_dir}/bin/setenv.sh" do
    source 'setenv.sh.erb'
    cookbook 'coremedia_tomcat'
    owner new_resource.user
    group new_resource.group
    variables(:heap => new_resource.heap,
              :perm => new_resource.perm,
              :java_home => new_resource.java_home,
              :catalina_opts => new_resource.catalina_opts,
              :debug => new_resource.debug,
              :debug_opts => new_resource.debug_opts,
              :jmx_remote => new_resource.jmx_remote,
              :jmx_remote_authenticate => new_resource.jmx_remote_authenticate,
              :jmx_remote_server_name => new_resource.jmx_remote_server_name, #.nil? ? node['fqdn'] : new_resource.jmx_remote_server_name,
              :jmx_remote_ssl => new_resource.jmx_remote_ssl)
    mode 00755
  end

  link new_resource.log_dir do
    to "#{tomcat_dir}/logs"
  end
end

action :update do
  # this is just a marker action for resources that need to tell chef that we need to restart tomcat, in contrast to the
  # update action on the webapp, this action won't redeploy the webapp
  new_resource.updated_by_last_action(true)
end

def manage_libs(libs = {}, lib_dir = '')
  directory lib_dir do
    owner new_resource.user
    group new_resource.group
    new_resource.updated_by_last_action(false)
  end

  jars_found = Dir["#{lib_dir}/*.jar"].select do |jar|
    !libs.key?(::File.basename(jar))
  end

  jars_found.each do |jar_to_delete|
    file jar_to_delete do
      action :delete
    end
  end

  libs.each_key do |key|
    coremedia_maven "#{lib_dir}/#{key}" do
      group_id libs[key]['group_id']
      artifact_id libs[key]['artifact_id']
      version libs[key]['version']
      checksum libs[key]['checksum'] if libs[key]['checksum']
      repository_url new_resource.maven_repository_url if new_resource.maven_repository_url
      nexus_url new_resource.nexus_url unless new_resource.nexus_url.nil?
      nexus_repo libs[key]['nexus_repo'].nil? ? new_resource.nexus_repo : libs[key]['nexus_repo']
      packaging 'jar'
      owner new_resource.user
      group new_resource.group
      action [:install]
    end
  end
end
