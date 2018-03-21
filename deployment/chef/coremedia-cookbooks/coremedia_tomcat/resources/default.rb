=begin
#<

Installs Apache Tomcat configures it and integrates it in the native service registry.
The layout will be:

```
<path>
    |-service-hooks
    |-apache-tomcat-<version>
    |-current   -> <path>/apache-tomcat-<version>
    |-server-lib
    `-common-lib

/etc/init.d/<name>  -> <path>/current/bin/init.sh
```
@section Service Hooks
If you want to execute some scripts before or after Tomcat is started/stopped, you may place arbitrary scripts below
the `service-hooks` directory. To be picked up, the scripts must be named according to the regular expression
`<PHASE>.*.sh`,where `<PHASE>` may be either `pre-start`, `post-start`, `pre-stop` or `post-stop`.
In order for the scripts to be executed in a certain order, you should prefix the phase with an alphanumeric element i.e.
`pre-start-01.sh`.

@section Port Schema
If you set the `port_prefix` attribute, all ports and the debug_opts, which include a port will automatically be preset
with the following port schema. All ports can of course be set individually.

`<3-digit prefix><2-digit suffix>`

2-digit suffix
* `05` - SHUTDOWN
* `06` - JDWP Debug port will be used in debug_opts default, i.e. `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8006`
* `09` - AJP
* `43` - HTTPS
* `80` - HTTP
* `99` - JMX Registry
* `98` - JMX Server

@action install Install tomcat.
@action update Sets the `updated_by_last_action` flag of this resource to `true`. Use this to notify a service restart.

@section Examples

```ruby
coremedia_tomcat 'solr' do
  path '/opt/solr'
  version '7.0.63'
  user 'solr'
  group 'tomcat'
  port_prefix 88
  shutdown_wait 40
  shutdown_force false
  debug true
  server_libs('slf4j-api.jar' => { 'group_id' => 'org.slf4j',
                                   'artifact_id' => 'slf4j-api',
                                   'version' => '1.7.6'
                                   'nexus_repo' => 'central' # optional nexus repo name, that has precendence to the global nexus_repo attribute
  })
  log_dir '/var/log/solr'
  jmx_remote true
  jmx_remote_server_name node[:fqdn]
  jmx_remote_jar_source 'URL ot catalina-jmx-remote.jar'
  jmx_remote_use_local_ports false
  jmx_remote_authenticate false
  jmx_remote_monitor_user 'monitor'
  jmx_remote_monitor_password 'monitor'
  jmx_remote_control_user  'control'
  jmx_remote_control_password  'control'
  jmx_remote_ssl false
end
```
#>
=end

actions :install, :update
default_action :install

attr_accessor :path, :user, :group, :http_port, :ajp_port, :shutdown_port, :jvm_route, :port_prefix

#<> @attribute path The installation path of the tomcat service dir.
attribute :path, :kind_of => String, :required => true
#<> @attribute source The url of the tomcat zip archive. If you set this, you need to set a matching `version` attribute to determine the tomcat dir inside the archive.
attribute :source, :kind_of => String, :default => nil
#<> @attribute source_checksum The SHA-256 checksum of the tomcat zip artifact.
attribute :source_checksum, :kind_of => String, :default => nil
#<> @attribute version The version of tomcat.
attribute :version, :kind_of => String, :required => true
#<> @attribute start_levels The start_levels attribute for SystemV init script.
attribute :start_levels, :kind_of => Integer, :default => 2345
#<> @attribute start_priority The start priority attribute for SystemV init script.
attribute :start_priority, :kind_of => Integer, :default => 88
#<> @attribute user The owner of all files and the user to start the service with.
attribute :user, :kind_of => String, :default => 'root'
#<> @attribute group The group of all files.
attribute :group, :kind_of => String, :default => 'root'
#<> @attribute startup_wait The time in seconds to wait after starting tomcat.
attribute :startup_wait, :kind_of => Integer, :default => 10
#<> @attribute server_libs A hash of Maven GAV coordinate hashes for jars to be placed in the server loader (<path>/server-lib). The key for each hash is defining the filename of the jar.
attribute :server_libs, :kind_of => Hash, :default => {}
#<> @attribute server_libs A hash of Maven GAV coordinate hashes for jars to be placed in the common loader (<path>/common-lib). The key for each hash is defining the filename of the jar.
attribute :common_libs, :kind_of => Hash, :default => {}
#<> @attribute maven_repository_url The maven repository to retrieve the server_libs and common_libs jars
attribute :maven_repository_url, :kind_of => String
#<> @attribute nexus_url Set this to the base url of your nexus to use the REST API for artifact resolution. If set this method has precedence to the repository_url attribute.
attribute :nexus_url, :kind_of => String, :default => nil
#<> @attribute nexus_repo The repo name from which to resolve artifacts.
attribute :nexus_repo, :kind_of => String, :default => 'releases'
#<> @attribute catalina_properties A hash of properies to append to the `catalina.properties` file
attribute :catalina_properties, :kind_of => Hash, :default => {}
#<> @attibute port_prefix An integer which will be used as the prefix for all ports.
attribute :port_prefix, :kind_of => Integer, :default => 80
#<> @attribute shutdown_port The port where tomcat listens on shutdown commands.
attribute :shutdown_port, :kind_of => Integer, :default => lazy { |r| r.port_prefix * 100 + 5 }
#<> @attribute shutdown_wait The time to wait for tomcat to shut down
attribute :shutdown_wait, :kind_of => Integer, :default => 30
#<> @attribute shutdown_force Use -KILL when shutdown_wait threshold is reached.
attribute :shutdown_force, :kind_of => [FalseClass, TrueClass], :default => false
#<> @attribute max_threads The maximum number of threads for the connector executor pool .
attribute :max_threads, :kind_of => Integer, :default => 200
#<> @attribute min_threads The minimum number of threads for the connector executor pool .
attribute :min_threads, :kind_of => Integer, :default => 20
#<> @attribute http_port The port tomcat listens on for HTTP requests.
attribute :http_port, :kind_of => Integer, :default => lazy { |r| r.port_prefix * 100 + 80 }
#<> @attribute ajp_port The port tomcat listens on for AJP requests.
attribute :ajp_port, :kind_of => [FalseClass, Integer], :default => lazy { |r| r.port_prefix * 100 + 9 }
#<> @attribute debug A flag to enable/disable the debug_opts.
attribute :debug, :kind_of => [FalseClass, TrueClass], :default => false
#<> @attribute debug_opts The debug options to add to CATALINA_OPTS
attribute :debug_opts, :kind_of => String, :default => lazy { |r| "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=#{r.port_prefix * 100 + 6}" }
#<> @attribute jvm_route The route name in case of AJP load balancing.
attribute :jvm_route, :kind_of => String, :name_attribute => true
#<> @attribute session_timeout The timeout in minutes to kill sessions.
attribute :session_timeout, :kind_of => Integer, :default => 60
#<> @attribute heap The maximum memory for the JVM heap space. JVM notation is supported, i.e. 1G or 512m
attribute :heap, :kind_of => String, :default => '512m'
#<> @attribute perm The maximum memory for the JVM perm space. JVM notation is supported, i.e. 1G or 512m
attribute :perm, :kind_of => String, :default => '128m'
#<> @attribute catalina_opts Additional CATALINA_OPTS.
attribute :catalina_opts, :kind_of => String, :default => ''
#<> @attribute log_dir The path of the symlink to the logs directory. Set to empty string to not create a link.
attribute :log_dir, :kind_of => String, :default => lazy { |r| "/var/log/#{r.name}" }
#<> @attribute java_home The JVM to use.
attribute :java_home, :kind_of => String, :default => lazy { node['java']['home'] }
#<> @attribute jmx_remote Flag to enable or disable remote jmx.
attribute :jmx_remote, :kind_of => [FalseClass, TrueClass], :default => true
#<> @attribute jmx_remote_server_name The host name or ip that resolves to this node. If set to nil, node[:fqdn] will be used.
attribute :jmx_remote_server_name, :kind_of => String, :default => lazy { node['fqdn'] }
#<> @attribute jmx_remote_jar_source The url to the catalina-jmx-remote.jar. If set to nil, it will be derived.
attribute :jmx_remote_jar_source, :kind_of => String, :default => nil
#<> @attribute jmx_remote_jar_source The SHA-256 checksum of the jmx remote jar.
attribute :jmx_remote_jar_source_checksum, :kind_of => String, :default => nil
#<> @attribute jmx_remote_registry_port The port of the JMX registry.
attribute :jmx_remote_registry_port, :kind_of => Integer, :default => lazy { |r| r.port_prefix * 100 + 99 }
#<> @attribute jmx_remote_server_port The port of the JMX server
attribute :jmx_remote_server_port, :kind_of => Integer, :default => lazy { |r| r.port_prefix * 100 + 98 }
#<> @attribute jmx_remote_use_local_ports A flag to force local ports to connect to the JMX/RMI server.
attribute :jmx_remote_use_local_ports, :kind_of => [FalseClass, TrueClass], :default => false
#<> @attribute jmx_remote_authenticate A flag to disable jmx authentication.
attribute :jmx_remote_authenticate, :kind_of => [FalseClass, TrueClass], :default => true
#<> @attribute jmx_remote_ssl A flag to enable or disable jmx remote over ssl.
attribute :jmx_remote_ssl, :kind_of => [FalseClass, TrueClass], :default => false
#<> @attribute jmx_remote_monitor_user The user for readonly access.
attribute :jmx_remote_monitor_user, :kind_of => String, :default => 'monitor'
#<> @attribute jmx_remote_monitor_password The password for readonly access.
attribute :jmx_remote_monitor_password, :kind_of => String, :default => 'monitor'
#<> @attribute jmx_remote_control_user The user for readwrite access.
attribute :jmx_remote_control_user, :kind_of => String, :default => 'control'
#<> @attribute jmx_remote_control_password The password for readwrite access.
attribute :jmx_remote_control_password, :kind_of => String, :default => 'control'
#<> @attribute context_config A hash to pass to the context_template. See context section for the possible configuration keys in the default template.
attribute :context_config, :kind_of => Hash, :default => {}
#<> @attribute context_template The template name from which to create the context_file.
attribute :context_template, :kind_of => [FalseClass, String], :default => 'context.xml.erb'
#<> @attribute context_template_cookbook The cookbook, from which to load the context_template.
attribute :context_template_cookbook, :kind_of => String, :default => 'coremedia_tomcat'
#<> @attribute clean_log_dir_on_start Set this to true to clean the log directory on before starting tomcat
attribute :clean_log_dir_on_start, :kind_of => [FalseClass, TrueClass], :default => false
#<> @attribute access_log A toggle to switch on/off access logs
attribute :access_log, :kind_of => [FalseClass, TrueClass], :default => true
#<> @attribute keep_old_instances A toggle to clean up old tomcat istallations
attribute :keep_old_instances, :kind_of => [FalseClass, TrueClass], :default => false
