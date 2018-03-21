coremedia_tomcat Cookbook CHANGELOG
======================
This file is used to list changes made in each version of the coremedia_tomcat cookbook.

v2.2.0
------
- add toggle `keep_old_instances` on the `coremedia_tomcat` resource to trigger cleanup of old Tomcat installations.

v.2.1.4
-------
- fix authentication on nexus access to download the webapp artifact.

v.2.1.2
------
- add `access_log` attribute to `coremedia_tomcat` resource to toggle the creation of access logs

v.2.1.1
-------
- guard if hook script is there
- add description to systemV init files

v.2.1.0
-------
- use `debug` flag to toggle the comment character for the debug opts instead of not rendering the complete line.
- the service init script now executes arbitrary hook scripts below a `service-hooks` directory. The scripts will
be executed prior and after Tomcat starts or stops and must match the pattern `<phase>.*\.sh`, where `<phase>` may be one
of `pre-start`, `post-start`, `pre-stop` or `post-stop`.
- fixed warning `property classifier is declared in both coremedia_maven...`

v.2.0.12
--------
- support for Tomcat `8.5.x`

v.2.0.11
------------
- add attribute `clean_log_dir_on_start` to `coremedia_tomcat` resource to allow cleaning the logs.

v.2.0.10
--------
- support for Tomcat `8.0.x`

v.2.0.9
-------
- add checksum option for `server_libs` and `common_libs` attributes. Simply add a key `checksum` to the libs hash

v.2.0.8
-------
- fix broken checksum test in `2.0.7`.

v.2.0.7
-------
- warn if `coremedia_tomcat_webapp` deploys a war using a metaversion but does not declare a checksum.

v.2.0.6
-------
- remove chef warnings from lifecycle definition.
- cache apache-tomcat zip in chef cache dir to prevent chef from downloading it multiple times when installing multiple tomcats.
- add `source_checksum` to and `jmx_remote_jar_source_checksum` parameter to `coremedia_tomcat` resource to test SHA-256 checksum on download.
- add `checksum` parameter to `coremedia_tomcat_webapp` resource to test SHA-256 checksum on download.

v.2.0.5
------
- allow `server_libs` and `common_libs` elements to define their own `nexus_repo` name to override the default. This is necessary
if snapshots are separated from release artifacts. Using

```ruby
server_libs('slf4j-api.jar' => { 'group_id' => 'org.slf4j',
                                 'artifact_id' => 'slf4j-api',
                                 'version' => '1.7.6'
                                 'nexus_repo' => 'central'})
```

will download the `slf4j-api` jar from the nexus repository `central` if `nexus_url` is set.

v.2.0.4
------
- fix rendering of `context.xml` using `resourceLink` in the `context_config` attribute.
- allow to set `nexus_url` and `nexus_repo` on `coremedia_tomcat` and `coremedia_tomcat_webapp` to use the rest features 
 of a Sonatype NEXUS repository to resolve shared libs and webapp war artifacts. If you do so, 
 versions like `1.2-SNAPSHOT`, `LATEST` and `RELEASE` can be resolved correctly. This feature requires `coremedia_maven` 
 cookbook `>= 2.0.2`.

v.2.0.3
------
- fix typo in `catalina.properties` template.

v.2.0.2
------
- use http urls to tomcat artifacts to allow caching of artifacts with a webproxy
- systemV init script at `/etc/init.d` is now a real file, not a symlink any more. This fixes
https://access.redhat.com/solutions/2067013, for RHEL 7.2.

v.2.0.1
------
- fix jmx remote connection.

v.2.0.0
------
- update `coremedia_maven` dependency to `~> 2.0`
- added `coremedia_tomcat_service_lifecycle` definition.
- added `coremedia_tomcat_webapp` LWRP.
- added `port_prefix` attribute to `coremedia_tomcat` LWRP. All other port attributes and the `debug_opts` attribute
will now determine their defaults based on the port prefix.
- added attribute `context_config` to the `coremedia_tomcat` LWRP. The attribute expects a hash to configure various aspects of the default
context config `conf/context.xml`. See [context configuration](./README.md#context-files) .

### Breaking Changes

- `coremedia_tomcat_service` definition has been removed.

- the attribute `catalina_opts` on the `coremedia_tomcat` LWRP is now of type `String` (was `Array<String>`).

- With this release the lifecycle of webapp redeployment has been made very robust. The new concept separates
a chef managed tomcat into three parts:

      * the tomcat installation using the 'coremedia_tomcat` LWRP.
      * multiple webapp installations using the 'coremedia_tomcat_webapp` LWRP.
      * lifecycle managing resources encapsulated in the `coremedia_tomcat_service_lifecycle` definition.

Between the webapp installation and the lifecycle definition, you can place further resources into the exploded webapp dir
and use standard notification syntax on the webapp LWRP with the `update` action that the webapp should be redeployed.
Later in the run the resources provided by the lifecycle definition will check the webapps state and initiate tomcat to
shutdown, redeploy the webapp and restart tomcat.

v.1.0.3
------
- rename resource name for downloaded war artifacts from pathname to pattern `<definition name>-<war artifact_id>`

v.1.0.2
------
- fix typo that reversed the `service_restart_hook` logic.

v.1.0.1
---------
- add parameter `service_restart_hook` to service definition. Set this parameter to false to disable the restart hook if
you want to manually restart the services.

v.1.0.0
---------
- The definition `coremedia_tomcat_service` was changed by removing the user/group creation and the service registration. This must now be done by 
the using recipe.
- A new definition `coremeida_tomcat_service_user` was added to create the service users.
- The `coremedia_tomcat_context` definition now can define environment entries and listeners using configuration hashes.

v.0.2.3
--------
- fix `shutdown_force` default not set. 

v.0.2.2
--------
- add `shutdown_wait` to `coremedia_tomcat` resource to configure the time for tomcat to shutdown gracefully before 
killing the process. The default will be set to 20 seconds.
- add `shutdown_force` to `coremedia_tomcat` resource to use `kill -KILL` to shutdown process after `shutdown_wait` timeout is
over. The default is `true`.


v.0.2.1
--------
- replace lifecycle notification logic in service definition. Now every resource that wants a restart should notify a 
 special `ruby_block` resource with timing `:immediately` i.e. 
 ```
 notifies :create, "ruby_block[restart_service_<SERVICE_NAME>}]", :immediately
 ```
 To make this work it is important, that the `service` resource is listed last.
  
 To function with the `context` definition, the ruby block must be named `restart_service_<SERVICE_NAME>`. 
 
 The benefit from this style is, that the service will only be restarted at the point where the `service` resource is
 called by chef. It is of course still possible to call delayed actions.

v.0.2.0
--------
- add jmx remote functionality. To configure jmx, the definition and the LWRP, both have new params or attributes. 
    - `jmx_remote` (default `true`)
    - `jmx_remote_server_name` (default `node[:fqdn]`)
    - `jmx_remote_jar_source` (default is apache tomcat url with the version set by the `version` attribute)
    - `jmx_remote_registry_port` (default is `port_prefix` + 99)
    - `jmx_remote_server_port` (default is `port_prefix` + 98) 
    - `jmx_remote_use_local_ports` (default false)                    
    - `jmx_remote_authenticate` (default false)
    - `jmx_remote_monitor_user` (default 'monitor')
    - `jmx_remote_monitor_password` (default 'monitor')
    - `jmx_remote_control_user` (default 'control') 
    - `jmx_remote_control_password` (default 'control') 
    - `jmx_remote_ssl` false


v.0.1.3
--------
- `log_dir` is now by default `/var/log/<RESOURCE_NAME>`
- fix set JAVA_HOME in `setenv.sh`.
- fix systemd compatibility of init script.

v.0.1.2
--------
- add valve `org.apache.catalina.valves.RemoteIpValve` with `protocolHeader="x-forwarded-proto"` to default host configuration to specify forwarded protocol by header.
- add pessimistic version constraint to `coremedia_maven` to `~> 1.0.0`

v.0.1.1
--------
- fix tomcat logging.
- service definition was lacking of `maven_repository_url` param

v.0.1.0
--------
 - Initial release
