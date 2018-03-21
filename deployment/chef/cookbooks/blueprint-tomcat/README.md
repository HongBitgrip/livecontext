# Description

This is the application cookbook to deploy CoreMedia Blueprint Webapps using Apache Tomcat.

# Filesystem layout

```
<path>
    |-apache-tomcat-<version>
    |-current   -> <path>/apache-tomcat-<version>
    |-server-lib
    |-common-lib
    |-webapps
    |       |- <webapp context> (in case of an exploded webapp)
    |       `- <webapp context>.war (original artifact)
    |-<name>.properties
    |-jmx-remote.access
    `-jmx-remote.password

/etc/init.d/<name>
/var/log/coremedia/<name> -> <path>/current/logs
```

# Portschema

```
<3-Digit Prefix><2-Digit Suffix>
```

The suffixes are fixed and cannot be changed.

| Suffix  | Semantic      |
| ------- | ------------- |
| 05      | Shutdown      |
| 06      | JDWP Debug    |
| 09      | AJP           |
| 43      | HTTPS         |
| 80      | HTTP          |
| 98      | JMX Server    |
| 99      | JMX Registry  |

The prefixes however are completely configurable, it can be an integer between `20` and `400`, in case the default ephemeral port range is not decreased.

| Webapp Key                | Prefix  | Context         |
| ------------------------- | ------- | ----------------|
| content-management-server | 401     | coremedia       |
| master-live-server        | 402     | coremedia       |
| workflow-server           | 403     | workflow        |
| content-feeder            | 404     | contentfeeder   |
| user-changes              | 405     | user-changes    |
| elastic-worker            | 406     | elastic-worker  |
| caefeeder-preview         | 407     | caefeeder       |
| caefeeder-live            | 408     | caefeeder       |
| cae-preview               | 409     | blueprint       |
| studio                    | 410     | studio          |
| sitemanager               | 413     | editor-webstart |
| replication-live-server   | 420     | coremedia       |
| cae-live                  | 421     | blueprint       |

The cae-live webapp can be installed multiple times on the same node by setting `node['blueprint']['tomcat']['cae-live']['instances']`
to a number greater than 1. The port prefix will then be equal 420 + instance_number.

## Application configuration

All webapps that instantiate a component based Spring context can be configured using a key/value attribute hash. The hash
is located at `node['blueprint']['webapps'][<WEBAPP_KEY>]['application.properties']`. The webapp keys are the ones listed above
in the port prefix section on the right side of the dash. For the live cae there can be more than one instance per node. In that
case each instance can be configured using the webapp key suffixed by a dash and the number of the instance, i.e.
`node['blueprint']['webapps']['cae-live-1']['application.properties']`


## JMX

JMX address = `service:jmx:rmi://<HOST>:<PREFIX>98/jndi/rmi://<HOST>:<PREFIX>99/jmxrmi`
JMX Login (readonly) = (monitor / monitor)
JMX Login (readwrite) = (control / control)

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* coremedia_tomcat (~> 2.2.0)
* coremedia_maven (~> 2.0.4)
* chef-sugar (~> 3.0)

# Attributes

* `node['blueprint']['tomcat']['source']` - The download url to the tomcat zip, make sure the version attribute matches. Set to nil to use the default url based on the version attribute. Defaults to `http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.82/bin/apache-tomcat-7.0.82.zip`.
* `node['blueprint']['tomcat']['source_checksum']` - The SHA-256 checksum of the tomcat installation zip. Defaults to `db399beb82d19e08285e628f4c728cf1756bcfda4df74c25faff5ba0668a2281`.
* `node['blueprint']['tomcat']['version']` - The version of tomcat to install. Defaults to `7.0.82`.
* `node['blueprint']['tomcat']['java_home']` - The path to the java home for the tomcat services. Defaults to `/usr/lib/jvm/java`.
* `node['blueprint']['tomcat']['catalina_opts']['agent']` - Global jvm agent opts. Use this to instrument the jvm for monitoring. Defaults to ``.
* `node['blueprint']['tomcat']['catalina_opts']['gc']` - Global jvm garbage collection flags. Defaults to `-XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSClassUnloadingEnabled -XX:+UseMembar`.
* `node['blueprint']['tomcat']['catalina_opts']['network']` - Global jvm network system properties, the defaults disable IPv4 because IPv6 loopback over localhost currently does not work behind an apache. Defaults to `-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses`.
* `node['blueprint']['tomcat']['catalina_opts']['out_of_memory']` - Global jvm option to handle OutOfMemoryError, the default stops the process using 'kill -9'. With Oracle JDK 8u92 or above, you could also use -XX:+ExitOnOutOfMemoryError. Defaults to `-XX:OnOutOfMemoryError=\'kill -9 %p\'`.
* `node['blueprint']['tomcat']['cae-preview']['catalina_opts']['libjpeg']` -  Defaults to `-Djava.library.path=#{node['blueprint']['libjpeg_turbo_path']}`.
* `node['blueprint']['tomcat']['cae-live']['catalina_opts']['libjpeg']` -  Defaults to `-Djava.library.path=#{node['blueprint']['libjpeg_turbo_path']}`.
* `node['blueprint']['tomcat']['studio']['catalina_opts']['libjpeg']` -  Defaults to `-Djava.library.path=#{node['blueprint']['libjpeg_turbo_path']}`.
* `node['blueprint']['tomcat']['jmx_remote']` - A flag to enable/disable the jmx remote connector. Defaults to `true`.
* `node['blueprint']['tomcat']['jmx_remote_jar_source']` - The download url to the jar, make sure the version attribute matches. Set to nil to use the default url based on the version attribute. Defaults to `http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.82/bin/extras/catalina-jmx-remote.jar`.
* `node['blueprint']['tomcat']['jmx_remote_jar_source_checksum']` - The SHA-256 checksum of the catalina-jmx-remote.jar. Defaults to `684c3f7ab4a21cfccbbf760b4576554a0219898d289fe1345a0e746d5f82b425`.
* `node['blueprint']['tomcat']['jmx_remote_authenticate']` - A flag to enable/disable remote jmx authentication. Defaults to `true`.
* `node['blueprint']['tomcat']['jmx_remote_server_name']` - The server name under which the rmi server is registered. Set it to localhost and create a ssh tunnel(recommended) or set it to the actual hostname and open the ports and configure security and ssl. Defaults to `node['fqdn']`.
* `node['blueprint']['tomcat']['jmx_remote_monitor_password']` - The password for the monitoring jmx role. Defaults to `monitor`.
* `node['blueprint']['tomcat']['jmx_remote_control_password']` - The password for the control (modify) jmx role. Defaults to `control`.
* `node['blueprint']['tomcat']['start_service']` - Start Services can be overridden using a more specific key, i.e. default['blueprint']['tomcat']['workflow-server']['start_service'] = false. Defaults to `true`.
* `node['blueprint']['tomcat']['shutdown_force']` - This flag will force tomcat to kill the process using -KILL when shutdown_wait threshold is reached. Defaults to `true`.
* `node['blueprint']['tomcat']['shutdown_wait']` - The time to wait for tomcat to shut down. Defaults to `40`.
* `node['blueprint']['tomcat']['clean_log_dir_on_start']` - Set to true to delete logs before start, this only works if the log appender file is set. Defaults to `false`.
* `node['blueprint']['tomcat']['keep_old_instances']` - Set to true to delete old tomcat instances on tomcat update. Defaults to `true`.
* `node['blueprint']['tomcat']['context_config']['listener']['shutdown_listener']['className']` - The failed context shutdown listener to stop tomcat if the context failed. Defaults to `com.coremedia.tomcat.FailedContextShutdownServerListener`.
* `node['blueprint']['tomcat']['context_config']['session_cookie_name']` - The global session cookie name. Defaults to `CM_SESSIONID`.
* `node['blueprint']['tomcat']['logback_config']['logger']['com.coremedia']` - The default log level for all loggers beneath 'com.coremedia', you may override this on service level. Defaults to `info`.
* `node['blueprint']['tomcat']['logback_config']['logger']['hox.corem']` - The default log level for all loggers beneath 'hox.corem', you may override this on service level. Defaults to `info`.
* `node['blueprint']['tomcat']['logback_config']['logger']['org.springframework']` - The default log level for all loggers beneath 'org.springframework', you may override this on service level. Defaults to `warn`.
* `node['blueprint']['tomcat']['logback_config']['includes']` - The default logback configuration to include from the classpath. Defaults to `[ ... ]`.
* `node['blueprint']['tomcat']['logback_config']['appender']` - The default appender, possible other values depend on the includes. Defaults to `[ ... ]`.
* `node['blueprint']['tomcat']['logback_config']['properties']['log.pattern']` - The default log pattern, because ruby escapes the backslash we need to use 4 backslashes here. Defaults to `%d{yyyy-MM-dd HH:mm:ss} %-7([%level]) %logger [%X{tenant}] - %message \\\\(%thread\\\\)%n`.
* `node['blueprint']['tomcat']['content-management-server']['heap']` -  Defaults to `512m`.
* `node['blueprint']['tomcat']['workflow-server']['heap']` -  Defaults to `384m`.
* `node['blueprint']['tomcat']['master-live-server']['heap']` -  Defaults to `384m`.
* `node['blueprint']['tomcat']['replication-live-server']['heap']` -  Defaults to `256m`.
* `node['blueprint']['tomcat']['user-changes']['heap']` -  Defaults to `256m`.
* `node['blueprint']['tomcat']['elastic-worker']['heap']` -  Defaults to `256m`.
* `node['blueprint']['tomcat']['content-feeder']['heap']` -  Defaults to `256m`.
* `node['blueprint']['tomcat']['studio']['heap']` -  Defaults to `512m`.
* `node['blueprint']['tomcat']['cae-preview']['heap']` -  Defaults to `1024m`.
* `node['blueprint']['tomcat']['cae-live']['heap']` -  Defaults to `1024m`.
* `node['blueprint']['tomcat']['caefeeder-preview']['heap']` -  Defaults to `256m`.
* `node['blueprint']['tomcat']['caefeeder-live']['heap']` -  Defaults to `256m`.
* `node['blueprint']['tomcat']['sitemanager']['heap']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['content-management-server']['perm']` -  Defaults to `128m`.
* `node['blueprint']['tomcat']['workflow-server']['perm']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['master-live-server']['perm']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['replication-live-server']['perm']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['user-changes']['perm']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['elastic-worker']['perm']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['content-feeder']['perm']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['studio']['perm']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['cae-preview']['perm']` -  Defaults to `128m`.
* `node['blueprint']['tomcat']['cae-live']['perm']` -  Defaults to `128m`.
* `node['blueprint']['tomcat']['caefeeder-preview']['perm']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['caefeeder-live']['perm']` -  Defaults to `92m`.
* `node['blueprint']['tomcat']['sitemanager']['perm']` -  Defaults to `64m`.
* `node['blueprint']['tomcat']['solr']['port_prefix']` -  Defaults to `400`.
* `node['blueprint']['tomcat']['content-management-server']['port_prefix']` -  Defaults to `401`.
* `node['blueprint']['tomcat']['master-live-server']['port_prefix']` -  Defaults to `402`.
* `node['blueprint']['tomcat']['workflow-server']['port_prefix']` -  Defaults to `403`.
* `node['blueprint']['tomcat']['content-feeder']['port_prefix']` -  Defaults to `404`.
* `node['blueprint']['tomcat']['user-changes']['port_prefix']` -  Defaults to `405`.
* `node['blueprint']['tomcat']['elastic-worker']['port_prefix']` -  Defaults to `406`.
* `node['blueprint']['tomcat']['caefeeder-preview']['port_prefix']` -  Defaults to `407`.
* `node['blueprint']['tomcat']['caefeeder-live']['port_prefix']` -  Defaults to `408`.
* `node['blueprint']['tomcat']['cae-preview']['port_prefix']` -  Defaults to `409`.
* `node['blueprint']['tomcat']['studio']['port_prefix']` -  Defaults to `410`.
* `node['blueprint']['tomcat']['sitemanager']['port_prefix']` -  Defaults to `413`.
* `node['blueprint']['tomcat']['replication-live-server']['port_prefix']` -  Defaults to `420`.
* `node['blueprint']['tomcat']['cae-live']['port_prefix']` -  Defaults to `421`.
* `node['blueprint']['tomcat']['content-management-server']['start_priority']` -  Defaults to `81`.
* `node['blueprint']['tomcat']['master-live-server']['start_priority']` -  Defaults to `81`.
* `node['blueprint']['tomcat']['workflow-server']['start_priority']` -  Defaults to `82`.
* `node['blueprint']['tomcat']['replication-live-server']['start_priority']` -  Defaults to `82`.
* `node['blueprint']['tomcat']['user-changes']['start_priority']` -  Defaults to `82`.
* `node['blueprint']['tomcat']['elastic-worker']['start_priority']` -  Defaults to `82`.
* `node['blueprint']['tomcat']['content-feeder']['start_priority']` -  Defaults to `82`.
* `node['blueprint']['tomcat']['studio']['start_priority']` -  Defaults to `83`.
* `node['blueprint']['tomcat']['cae-preview']['start_priority']` -  Defaults to `82`.
* `node['blueprint']['tomcat']['cae-live']['start_priority']` -  Defaults to `83`.
* `node['blueprint']['tomcat']['caefeeder-preview']['start_priority']` -  Defaults to `82`.
* `node['blueprint']['tomcat']['caefeeder-live']['start_priority']` -  Defaults to `83`.
* `node['blueprint']['tomcat']['sitemanager']['start_priority']` -  Defaults to `83`.
* `node['blueprint']['tomcat']['cae-live']['instances']` - number of live caes to install on this node. Defaults to `1`.
* `node['blueprint']['tomcat']['cae-live-1']['sitemap']['enabled']` - There should only be one cae generating the sitemaps, by default this is cae-live-1. Defaults to `true`.
* `node['blueprint']['tomcat']['cae-live-1']['sitemap']['start_time']` - The time when the sitemap should be created, see blueprint sitemap documentation for property blueprint.sitemap.starttime. Defaults to `+200`.
* `node['blueprint']['tomcat']['common_libs']['coremedia-tomcat.jar']` -  Defaults to `node['blueprint']['common_libs']['coremedia-tomcat.jar']`.

# Recipes

* [blueprint-tomcat::cae-live](#blueprint-tomcatcae-live) - This recipe installs and configures the CoreMedia Blueprint Live CAE.
* [blueprint-tomcat::cae-preview](#blueprint-tomcatcae-preview) - This recipe installs and configures the CoreMedia Blueprint Preview CAE.
* [blueprint-tomcat::caefeeder-live](#blueprint-tomcatcaefeeder-live) - This recipe installs and configures the CoreMedia Blueprint Live CAE Feeder.
* [blueprint-tomcat::caefeeder-preview](#blueprint-tomcatcaefeeder-preview) - This recipe installs and configures the CoreMedia Blueprint Preview CAE Feeder.
* [blueprint-tomcat::content-feeder](#blueprint-tomcatcontent-feeder) - This recipe installs and configures the CoreMedia Blueprint Content Feeder.
* [blueprint-tomcat::content-management-server](#blueprint-tomcatcontent-management-server) - This recipe installs and configures the CoreMedia Blueprint Content Management Server.
* [blueprint-tomcat::default](#blueprint-tomcatdefault) - This recipe installs all services.
* [blueprint-tomcat::elastic-worker](#blueprint-tomcatelastic-worker) - This recipe installs and configures the CoreMedia Blueprint Elastic Worker.
* [blueprint-tomcat::master-live-server](#blueprint-tomcatmaster-live-server) - This recipe installs and configures the CoreMedia Blueprint Master Live Server.
* [blueprint-tomcat::replication-live-server](#blueprint-tomcatreplication-live-server) - This recipe installs and configures the CoreMedia Blueprint Master Live Server.
* [blueprint-tomcat::sitemanager](#blueprint-tomcatsitemanager) - This recipe installs and configures the CoreMedia Sitemanager WebStart App.
* [blueprint-tomcat::studio](#blueprint-tomcatstudio) - This recipe installs and configures the CoreMedia Blueprint Studio.
* [blueprint-tomcat::user-changes](#blueprint-tomcatuser-changes) - This recipe installs and configures the CoreMedia Blueprint User Changes Webapp.
* [blueprint-tomcat::workflow-server](#blueprint-tomcatworkflow-server) - This recipe installs and configures the CoreMedia Blueprint Workflow Server.

## blueprint-tomcat::cae-live

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


### Sitemap generation
Because the sitemap should only be generated on one cae, you can disable sitemap generation for any cae on a node by setting
`node['blueprint']['webapps']['cae-live']['sitemap-cae']` to `nil` on that nodes environment, role or role recipe.


## blueprint-tomcat::cae-preview

This recipe installs and configures the CoreMedia Blueprint Preview CAE.

## blueprint-tomcat::caefeeder-live

This recipe installs and configures the CoreMedia Blueprint Live CAE Feeder.

## blueprint-tomcat::caefeeder-preview

This recipe installs and configures the CoreMedia Blueprint Preview CAE Feeder.

## blueprint-tomcat::content-feeder

This recipe installs and configures the CoreMedia Blueprint Content Feeder.

## blueprint-tomcat::content-management-server

This recipe installs and configures the CoreMedia Blueprint Content Management Server.

## blueprint-tomcat::default

This recipe installs all services.

## blueprint-tomcat::elastic-worker

This recipe installs and configures the CoreMedia Blueprint Elastic Worker.

## blueprint-tomcat::master-live-server

This recipe installs and configures the CoreMedia Blueprint Master Live Server.

## blueprint-tomcat::replication-live-server

This recipe installs and configures the CoreMedia Blueprint Master Live Server.

## blueprint-tomcat::sitemanager

This recipe installs and configures the CoreMedia Sitemanager WebStart App.

## blueprint-tomcat::studio

This recipe installs and configures the CoreMedia Blueprint Studio.

## blueprint-tomcat::user-changes

This recipe installs and configures the CoreMedia Blueprint User Changes Webapp.

## blueprint-tomcat::workflow-server

This recipe installs and configures the CoreMedia Blueprint Workflow Server.

# Definitions

* [blueprint_tomcat_service](#blueprint_tomcat_service) - This definition installs apache tomcat, configures it, downloads a webapp and deploy it.

## blueprint_tomcat_service

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

### Parameters

- skip_lifecycle: Set thist to true to skip the lifecycle at the end of this definition. See lifecycle section for mode details about this paramter..
- base_service_name: The service key from which to get the default component configuration before merging overrides using this service key. Defaults to: nil

### Lifecycle

By default this definition will deploy a webapp as it comes out of the maven repository, if you need to add or modify resources within the
webapp, you need to set `skip_lifecycle` to true add your resources after your call to the definition, notify the webapps update action and
create a new lifecycle, passing in tomcat and the webapp.

To get tomcat and the webapp resource, you can use the helper methods:

* cm_webapp(<resource_name>)
* cm_tomcat(<resource_name>)

because the resource_name is by this definition equal to the name of the definition, it should be straight forward.

### Nexus artifacts

If you want to use metaversions like `X-SNAPSHOT`, `RELEASE` or `LATEST`,  you need to configure this definition to use the nexus REST API.
Set `node.default['blueprint']['nexus_url']` and `node.default['blueprint']['nexus_repo']` accordingly. Of course, you can set
individual `nexus_repo` repository names for each individual webapp and for the common_lib and shared_lib libraries.

### Logging

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

### example

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
# Author

Author:: Your Name (<your_name@domain.com>)
