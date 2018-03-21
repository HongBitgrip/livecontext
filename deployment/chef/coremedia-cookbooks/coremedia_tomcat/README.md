# Description

This is a library cookbook. It provides a LWRP to install Apache Tomcat and install a service init script.
It also provides a LWRP to install a war artifact from a maven repository and optionally to update it with a context file.
Both LWRPs are complemented with two definitions, one to create a service user and group and one definition to glue tomcat, webapp
and a service resource together with a functioning lifecycle to stop the service, update the `doc_base` of the webapp and then restart
the service.

To accomplish the lifecycle above, the lifecycle definition gets the tomcat and the webapp resources as parameters. This can
 be done in a handy way, by creating variables and let them reference the resources.

### Example

```ruby

simple_home = '/opt/simple'

# create the service user
coremedia_tomcat_service_user 'simple' do
  home simple_home
  group 'tomcat'
end

tomcat = coremedia_tomcat 'simple' do
  path simple_home
  version '7.0.63'
  user 'simple'
  group 'tomcat'
  port_prefix 401
end

# unexploded webapp
webapp_unexploded = coremedia_tomcat_webapp "simple" do
  group_id 'org.codehaus.cargo'
  artifact_id 'simple-war'
  version '1.4.16'
  path "#{simple_home}/simple.war"
  owner tomcat.user
  group tomcat.group
  # context defaults to the resource name
  #context 'simple'
end

# exploded webapp, the war will be placed at /opt/simple/simple-exploded.war
webapp_exploded = coremedia_tomcat_webapp "simple-exploded" do
  group_id 'org.codehaus.cargo'
  artifact_id 'simple-war'
  version '1.4.16'
  path "#{simple_home}/simple-exploded"
  owner tomcat.user
  group tomcat.group
end

# add a file to the exploded dir and update the webapp
template "#{webapp_exploded.path}/index.html" do
  source 'my-index.html.erb'
  owner tomcat.user
  group tomcat.group
  variables :hello => "hello world from webapp #{webapp_exploded.artifact_id} with version #{webapp_exploded.version}"
  notifies :update, webapp_exploded, :immediately
end

# add more webapps here

# now lets close the lifecycle
coremedia_tomcat_service_lifecyle "simple" do
  # if the name of the definition equals the tomcat resources name the attribute can be omitted.
  # tomcat tomcat
  webapps [webapp_unexploded, webapp_exploded]
end
```

### Context files

Context files can either be placed as the default file in the `conf` directory or within a webapps `META-INF` directory if a
webapp is deployed as as an exploded path. In both cases the same default template is being used. The configuration hash can look like the
following example:

```ruby
{
  :display_name => 'myapp',                 # only allowed on webapp level
  :session_cookie_name => 'CM_SESSION_ID'
  :env_entries => {
    'toggle' => {                           # the name of the entry
      'description' => 'this does nothing', # description is optional
      'value' => 'false',                   # required to be set
      'type'  => 'java.lang.Boolean'        # defaults to java.lang.String
    }
  },
  :listener => {
    'my_listener' => {
      'className' => 'my.Klazz',
      'propertyname' => 'value'
    }
  },
  :resource_links => {
    'orb_link' => {
      'name' => 'ORB',
      'global' => 'ORB',
      'type' => 'org.omg.CORBA.ORB'
    }
  }
}
```
# Requirements

## Platform:

* redhat
* centos

## Cookbooks:

* coremedia_maven (~> 2.0)
* ulimit (~> 0.3)

# Attributes

*No attributes defined*

# Recipes

*No recipes defined*

# Definitions

* [coremedia_tomcat_service_lifecycle](#coremedia_tomcat_service_lifecycle) - This definition controls the lifecycle of webapps and its enclosing service.
* [coremedia_tomcat_service_user](#coremedia_tomcat_service_user) - This definition creates the service user.

## coremedia_tomcat_service_lifecycle

This definition controls the lifecycle of webapps and its enclosing service. By injecting the tomcat resource as well as
all webapp resources as parameters into this definition. This definition can encapsulate the logic, when to restart the service
and when to redeploy the webapps. See description of the webapp LWRP for more details about how to notify changes.

### Parameters

- tomcat: A `coremedia_tomcat` resource.  If not set, the definition will look for a `coremedia_tomcat` resource with the same name as the definition..
- webapps: An array of `coremedia_tomcat_webapp` resources. If not set, the definition will look for a `coremedia_tomcat_webapp` resource with the same name as the definition..
- undeploy_unmanaged: Undeploy unmanaged webapps.. Defaults to: true
- start_service: Set this to false to skip service start.. Defaults to: true
- enable_service: Set this to false to not enable the service.. Defaults to: true

### Examples

```ruby
tomcat_resource = coremedia_tomcat 'solr' do
...
end

solr_webapp_resource = coremedia_tomcat_webapp 'solr do
...
end

coremedia_tomcat_service_lifecycle 'solr' do
  tomcat tomcat_resource
  webapps [solr_webapp_resource]
end
```
## coremedia_tomcat_service_user

This definition creates the service user.

### Parameters

- : process_limit.
- filehandle_limit: . Defaults to: 25000
- process_limit: . Defaults to: 5000

### Examples

```ruby
coremedia_tomcat_service_user 'solr' do
  user  'solr'
  group 'tomcat'
  home '/opt/coremedia/solr'
  filehandle_limit 25_000
  process_limit 5000
end
```
# Resources

* [coremedia_tomcat](#coremedia_tomcat) - Installs Apache Tomcat configures it and integrates it in the native service registry.
* [coremedia_tomcat_webapp](#coremedia_tomcat_webapp) - Installs a webapp and optionally adds a `META-INF/context.xml`.

## coremedia_tomcat


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
### Actions

- install: Install tomcat. Default action.
- update: Sets the `updated_by_last_action` flag of this resource to `true`. Use this to notify a service restart.

### Attribute Parameters

- path: The installation path of the tomcat service dir.
- source: The url of the tomcat zip archive. If you set this, you need to set a matching `version` attribute to determine the tomcat dir inside the archive. Defaults to <code>nil</code>.
- source_checksum: The SHA-256 checksum of the tomcat zip artifact. Defaults to <code>nil</code>.
- version: The version of tomcat.
- start_levels: The start_levels attribute for SystemV init script. Defaults to <code>2345</code>.
- start_priority: The start priority attribute for SystemV init script. Defaults to <code>88</code>.
- user: The owner of all files and the user to start the service with. Defaults to <code>"root"</code>.
- group: The group of all files. Defaults to <code>"root"</code>.
- startup_wait: The time in seconds to wait after starting tomcat. Defaults to <code>10</code>.
- server_libs: A hash of Maven GAV coordinate hashes for jars to be placed in the common loader (<path>/common-lib). The key for each hash is defining the filename of the jar. Defaults to <code>{}</code>.
- common_libs:  Defaults to <code>{}</code>.
- maven_repository_url: The maven repository to retrieve the server_libs and common_libs jars
- nexus_url: Set this to the base url of your nexus to use the REST API for artifact resolution. If set this method has precedence to the repository_url attribute. Defaults to <code>nil</code>.
- nexus_repo: The repo name from which to resolve artifacts. Defaults to <code>"releases"</code>.
- catalina_properties: A hash of properies to append to the `catalina.properties` file Defaults to <code>{}</code>.
- port_prefix:  Defaults to <code>80</code>.
- shutdown_port: The port where tomcat listens on shutdown commands. Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- shutdown_wait: The time to wait for tomcat to shut down Defaults to <code>30</code>.
- shutdown_force: Use -KILL when shutdown_wait threshold is reached. Defaults to <code>false</code>.
- max_threads: The maximum number of threads for the connector executor pool . Defaults to <code>200</code>.
- min_threads: The minimum number of threads for the connector executor pool . Defaults to <code>20</code>.
- http_port: The port tomcat listens on for HTTP requests. Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- ajp_port: The port tomcat listens on for AJP requests. Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- debug: A flag to enable/disable the debug_opts. Defaults to <code>false</code>.
- debug_opts: The debug options to add to CATALINA_OPTS Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- jvm_route: The route name in case of AJP load balancing.
- session_timeout: The timeout in minutes to kill sessions. Defaults to <code>60</code>.
- heap: The maximum memory for the JVM heap space. JVM notation is supported, i.e. 1G or 512m Defaults to <code>"512m"</code>.
- perm: The maximum memory for the JVM perm space. JVM notation is supported, i.e. 1G or 512m Defaults to <code>"128m"</code>.
- catalina_opts: Additional CATALINA_OPTS. Defaults to <code>""</code>.
- log_dir: The path of the symlink to the logs directory. Set to empty string to not create a link. Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- java_home: The JVM to use. Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- jmx_remote: Flag to enable or disable remote jmx. Defaults to <code>true</code>.
- jmx_remote_server_name: The host name or ip that resolves to this node. If set to nil, node[:fqdn] will be used. Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- jmx_remote_jar_source: The SHA-256 checksum of the jmx remote jar. Defaults to <code>nil</code>.
- jmx_remote_jar_source_checksum:  Defaults to <code>nil</code>.
- jmx_remote_registry_port: The port of the JMX registry. Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- jmx_remote_server_port: The port of the JMX server Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- jmx_remote_use_local_ports: A flag to force local ports to connect to the JMX/RMI server. Defaults to <code>false</code>.
- jmx_remote_authenticate: A flag to disable jmx authentication. Defaults to <code>true</code>.
- jmx_remote_ssl: A flag to enable or disable jmx remote over ssl. Defaults to <code>false</code>.
- jmx_remote_monitor_user: The user for readonly access. Defaults to <code>"monitor"</code>.
- jmx_remote_monitor_password: The password for readonly access. Defaults to <code>"monitor"</code>.
- jmx_remote_control_user: The user for readwrite access. Defaults to <code>"control"</code>.
- jmx_remote_control_password: The password for readwrite access. Defaults to <code>"control"</code>.
- context_config: A hash to pass to the context_template. See context section for the possible configuration keys in the default template. Defaults to <code>{}</code>.
- context_template: The template name from which to create the context_file. Defaults to <code>"context.xml.erb"</code>.
- context_template_cookbook: The cookbook, from which to load the context_template. Defaults to <code>"coremedia_tomcat"</code>.
- clean_log_dir_on_start: Set this to true to clean the log directory on before starting tomcat Defaults to <code>false</code>.
- access_log: A toggle to switch on/off access logs Defaults to <code>true</code>.
- keep_old_instances: A toggle to clean up old tomcat istallations Defaults to <code>false</code>.

### Service Hooks
If you want to execute some scripts before or after Tomcat is started/stopped, you may place arbitrary scripts below
the `service-hooks` directory. To be picked up, the scripts must be named according to the regular expression
`<PHASE>.*.sh`,where `<PHASE>` may be either `pre-start`, `post-start`, `pre-stop` or `post-stop`.
In order for the scripts to be executed in a certain order, you should prefix the phase with an alphanumeric element i.e.
`pre-start-01.sh`.

### Port Schema
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

### Examples

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

@attibute port_prefix An integer which will be used as the prefix for all ports.
## coremedia_tomcat_webapp


Installs a webapp and optionally adds a `META-INF/context.xml`. Whether the maven war artifact will be downloaded
or downloaded, exploded and enriched with the context file, depends upon the path attribute. If it does not end on
`.war` the artifact will be exploded if not it will just be downloaded. The exploded dir and the archive will be placed side
by side.

Beside the `install` action there is a second action called update. This is just a helper action to set the `updated_by_last_action`
state of this resource to `true`. The idea of this concept, is that the `coremedia_tomcat_service_lifecycle` definition, then
can depend on this resources state to decide, whether the service needs to be stopped and the `doc_base` of this war should be
updated. Because of this, you should not choose the tomcats `app_base` directory as the target directory for the `install` action.

To resolve the latest X-SNAPSHOT, RELEASE or LATEST version, you need to set `nexus_url` and `nexus_repo` attributes.

### Actions

- install: Install the webapp in a directory which is not the `app_base` directory of tomcat. Default action.
- update: Sets the `updated_by_last_action` flag of this resource to `true`.

### Attribute Parameters

- path: The path to the webapp, can be a file or directory path. In the latter case, the war gets exploded.
- group_id: Maven groupId.
- artifact_id: Maven artifactId.
- version: Maven version.
- classifier: Maven classifier, defaults to not set. Defaults to <code>nil</code>.
- checksum: The SHA-256 checksum of the artifact. Defaults to <code>nil</code>.
- maven_repository_url: The Url of the maven repository, supports all protocols of the remote_file resource. Defaults to Maven Central. Defaults to <code>"http://repo1.maven.org/maven2/"</code>.
- nexus_url: Set this to the base url of your nexus to use the REST API for artifact resolution. If set this method has precedence to the repository_url attribute. Defaults to <code>nil</code>.
- nexus_repo: The repo name from which to resolve artifacts. Defaults to <code>"releases"</code>.
- nexus_username: The user to access a protected nexus. Defaults to <code>nil</code>.
- nexus_password: The password to access a protected nexus. Defaults to <code>nil</code>.
- owner: User ownership (linux only), defaults to "root". Defaults to <code>"root"</code>.
- group: Group membership (linux only), defaults to "root". Defaults to <code>"root"</code>.
- context: The context name under which to deploy the webapp, defaults to the name of the resource
- context_template: The template name from which to create the context_file (exploded mode only), set to false to skip the creation. Defaults to <code>Lazy Evaluator</code>, see LWRP code for default.
- context_template_cookbook: The cookbook, from which to load the context_template. Defaults to <code>"coremedia_tomcat"</code>.
- context_config: A hash to pass to the context_template. See [context section](#context-files) for the possible configuration keys in the default template. Defaults to <code>{}</code>.

### Examples

```ruby
coremedia_tomcat_webapp "/some/path/my-app do
  group_id 'org.myorg'
  artifact_id 'my-app'
  version '1.0.0'
  context 'myapp'
  context_config(
    :display_name => 'myapp',
    :env_entries => {
      'toggle' => {
        'description' => 'this does nothing',
        'value' => 'false',
        'type'  => 'java.lang.Boolean'
      }
    },
   :parameter => {
     'key1' => 'value1',
     'key2' => 'value2'
   },
   :resource_links => {
     'resourceLinkName' => {
        'description' => 'does nothing',
        'value' => 'resourceLinkValue',
        # defaults to java.lang.String if not set
        'type' => 'java.lang.String'
     }
   }
 )
  owner 'tomcat'
  group 'tomcat'
end
```

# Author

Author:: Felix Simmendinger (<felix.simmendinger@coremedia.com>)
