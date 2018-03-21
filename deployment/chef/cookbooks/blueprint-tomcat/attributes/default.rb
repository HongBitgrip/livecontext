#<> The download url to the tomcat zip, make sure the version attribute matches. Set to nil to use the default url based on the version attribute.
default['blueprint']['tomcat']['source'] = 'http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.82/bin/apache-tomcat-7.0.82.zip'
#<> The SHA-256 checksum of the tomcat installation zip
default['blueprint']['tomcat']['source_checksum'] = 'db399beb82d19e08285e628f4c728cf1756bcfda4df74c25faff5ba0668a2281'
#<> The version of tomcat to install
default['blueprint']['tomcat']['version'] = '7.0.82'
#<> The path to the java home for the tomcat services
default['blueprint']['tomcat']['java_home'] = '/usr/lib/jvm/java'
#<> Global jvm agent opts. Use this to instrument the jvm for monitoring
default['blueprint']['tomcat']['catalina_opts']['agent'] = ''
#<> Global jvm garbage collection flags
default['blueprint']['tomcat']['catalina_opts']['gc'] = '-XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSClassUnloadingEnabled -XX:+UseMembar'
#<> Global jvm network system properties, the defaults disable IPv4 because IPv6 loopback over localhost currently does not work behind an apache
default['blueprint']['tomcat']['catalina_opts']['network'] = '-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses'
#<> Global jvm option to handle OutOfMemoryError, the default stops the process using 'kill -9'. With Oracle JDK 8u92 or above, you could also use -XX:+ExitOnOutOfMemoryError
default['blueprint']['tomcat']['catalina_opts']['out_of_memory'] = '-XX:OnOutOfMemoryError=\'kill -9 %p\''

# webapps rendering using libjpeg_turbo
unless node['blueprint']['libjpeg_turbo_path'].nil?
  default['blueprint']['tomcat']['cae-preview']['catalina_opts']['libjpeg'] = "-Djava.library.path=#{node['blueprint']['libjpeg_turbo_path']}"
  default['blueprint']['tomcat']['cae-live']['catalina_opts']['libjpeg'] = "-Djava.library.path=#{node['blueprint']['libjpeg_turbo_path']}"
  default['blueprint']['tomcat']['studio']['catalina_opts']['libjpeg'] = "-Djava.library.path=#{node['blueprint']['libjpeg_turbo_path']}"
end

#<> A flag to enable/disable the jmx remote connector
default['blueprint']['tomcat']['jmx_remote'] = true
#<> The download url to the jar, make sure the version attribute matches. Set to nil to use the default url based on the version attribute.
default['blueprint']['tomcat']['jmx_remote_jar_source'] = 'http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.82/bin/extras/catalina-jmx-remote.jar'
#<> The SHA-256 checksum of the catalina-jmx-remote.jar
default['blueprint']['tomcat']['jmx_remote_jar_source_checksum'] = '684c3f7ab4a21cfccbbf760b4576554a0219898d289fe1345a0e746d5f82b425'
#<> A flag to enable/disable remote jmx authentication
default['blueprint']['tomcat']['jmx_remote_authenticate'] = true
#<> The server name under which the rmi server is registered. Set it to localhost and create a ssh tunnel(recommended) or set it to the actual hostname and open the ports and configure security and ssl.
default['blueprint']['tomcat']['jmx_remote_server_name'] = node['fqdn']
#<> The password for the monitoring jmx role.
default['blueprint']['tomcat']['jmx_remote_monitor_password'] = 'monitor'
#<> The password for the control (modify) jmx role.
default['blueprint']['tomcat']['jmx_remote_control_password'] = 'control'

#<> Start Services can be overridden using a more specific key, i.e. default['blueprint']['tomcat']['workflow-server']['start_service'] = false
default['blueprint']['tomcat']['start_service'] = true

#<> This flag will force tomcat to kill the process using -KILL when shutdown_wait threshold is reached.
default['blueprint']['tomcat']['shutdown_force'] = true
#<> The time to wait for tomcat to shut down
default['blueprint']['tomcat']['shutdown_wait'] = 40
#<> Set to true to delete logs before start, this only works if the log appender file is set
default['blueprint']['tomcat']['clean_log_dir_on_start'] = false
#<> Set to true to delete old tomcat instances on tomcat update
default['blueprint']['tomcat']['keep_old_instances'] = true

#<> The failed context shutdown listener to stop tomcat if the context failed.
default['blueprint']['tomcat']['context_config']['listener']['shutdown_listener']['className'] = 'com.coremedia.tomcat.FailedContextShutdownServerListener'
#<> The global session cookie name
default['blueprint']['tomcat']['context_config']['session_cookie_name'] = 'CM_SESSIONID'

# Logging
# logback configuration, will always be configured by chef, the logback.config file in the webapp will only be
# used if there is no logback_config hash at all

#<> The default log level for all loggers beneath 'com.coremedia', you may override this on service level
default['blueprint']['tomcat']['logback_config']['logger']['com.coremedia'] = 'info'
#<> The default log level for all loggers beneath 'hox.corem', you may override this on service level
default['blueprint']['tomcat']['logback_config']['logger']['hox.corem'] = 'info'
#<> The default log level for all loggers beneath 'org.springframework', you may override this on service level
default['blueprint']['tomcat']['logback_config']['logger']['org.springframework'] = 'warn'
#<> The default logback configuration to include from the classpath
default['blueprint']['tomcat']['logback_config']['includes'] = ['logging-common.xml']
#<> The default appender, possible other values depend on the includes
default['blueprint']['tomcat']['logback_config']['appender'] = ['file']
#<> The default log pattern, because ruby escapes the backslash we need to use 4 backslashes here
default['blueprint']['tomcat']['logback_config']['properties']['log.pattern'] = '%d{yyyy-MM-dd HH:mm:ss} %-7([%level]) %logger [%X{tenant}] - %message \\\\(%thread\\\\)%n'

# HEAP
default['blueprint']['tomcat']['content-management-server']['heap'] = '512m'
default['blueprint']['tomcat']['workflow-server']['heap'] = '384m'
default['blueprint']['tomcat']['master-live-server']['heap'] = '384m'
default['blueprint']['tomcat']['replication-live-server']['heap'] = '256m'
default['blueprint']['tomcat']['user-changes']['heap'] = '256m'
default['blueprint']['tomcat']['elastic-worker']['heap'] = '256m'
default['blueprint']['tomcat']['content-feeder']['heap'] = '256m'
default['blueprint']['tomcat']['studio']['heap'] = '512m'
default['blueprint']['tomcat']['cae-preview']['heap'] = '1024m'
default['blueprint']['tomcat']['cae-live']['heap'] = '1024m'
default['blueprint']['tomcat']['caefeeder-preview']['heap'] = '256m'
default['blueprint']['tomcat']['caefeeder-live']['heap'] = '256m'
default['blueprint']['tomcat']['sitemanager']['heap'] = '92m'

# PERM
default['blueprint']['tomcat']['content-management-server']['perm'] = '128m'
default['blueprint']['tomcat']['workflow-server']['perm'] = '92m'
default['blueprint']['tomcat']['master-live-server']['perm'] = '92m'
default['blueprint']['tomcat']['replication-live-server']['perm'] = '92m'
default['blueprint']['tomcat']['user-changes']['perm'] = '92m'
default['blueprint']['tomcat']['elastic-worker']['perm'] = '92m'
default['blueprint']['tomcat']['content-feeder']['perm'] = '92m'
default['blueprint']['tomcat']['studio']['perm'] = '92m'
default['blueprint']['tomcat']['cae-preview']['perm'] = '128m'
default['blueprint']['tomcat']['cae-live']['perm'] = '128m'
default['blueprint']['tomcat']['caefeeder-preview']['perm'] = '92m'
default['blueprint']['tomcat']['caefeeder-live']['perm'] = '92m'
default['blueprint']['tomcat']['sitemanager']['perm'] = '64m'

# Port Prefixes
default['blueprint']['tomcat']['solr']['port_prefix'] = 400
default['blueprint']['tomcat']['content-management-server']['port_prefix'] = 401
default['blueprint']['tomcat']['master-live-server']['port_prefix'] = 402
default['blueprint']['tomcat']['workflow-server']['port_prefix'] = 403
default['blueprint']['tomcat']['content-feeder']['port_prefix'] = 404
default['blueprint']['tomcat']['user-changes']['port_prefix'] = 405
default['blueprint']['tomcat']['elastic-worker']['port_prefix'] = 406
default['blueprint']['tomcat']['caefeeder-preview']['port_prefix'] = 407
default['blueprint']['tomcat']['caefeeder-live']['port_prefix'] = 408
default['blueprint']['tomcat']['cae-preview']['port_prefix'] = 409
default['blueprint']['tomcat']['studio']['port_prefix'] = 410
default['blueprint']['tomcat']['sitemanager']['port_prefix'] = 413
default['blueprint']['tomcat']['replication-live-server']['port_prefix'] = 420
# here its the starting port cae-live-2 will automatically have prefix 422
default['blueprint']['tomcat']['cae-live']['port_prefix'] = 421

# start_priority
default['blueprint']['tomcat']['content-management-server']['start_priority'] = 81
default['blueprint']['tomcat']['master-live-server']['start_priority'] = 81
default['blueprint']['tomcat']['workflow-server']['start_priority'] = 82
default['blueprint']['tomcat']['replication-live-server']['start_priority'] = 82
default['blueprint']['tomcat']['user-changes']['start_priority'] = 82
default['blueprint']['tomcat']['elastic-worker']['start_priority'] = 82
default['blueprint']['tomcat']['content-feeder']['start_priority'] = 82
default['blueprint']['tomcat']['studio']['start_priority'] = 83
default['blueprint']['tomcat']['cae-preview']['start_priority'] = 82
default['blueprint']['tomcat']['cae-live']['start_priority'] = 83
default['blueprint']['tomcat']['caefeeder-preview']['start_priority'] = 82
default['blueprint']['tomcat']['caefeeder-live']['start_priority'] = 83
default['blueprint']['tomcat']['sitemanager']['start_priority'] = 83

#<> number of live caes to install on this node
default['blueprint']['tomcat']['cae-live']['instances'] = 1
#<> There should only be one cae generating the sitemaps, by default this is cae-live-1
default['blueprint']['tomcat']['cae-live-1']['sitemap']['enabled'] = true
#<> The time when the sitemap should be created, see blueprint sitemap documentation for property blueprint.sitemap.starttime
default['blueprint']['tomcat']['cae-live-1']['sitemap']['start_time'] = '+200'

#################################
##     Common & Shared Libs    ##
#################################
default['blueprint']['tomcat']['common_libs']['coremedia-tomcat.jar'] = node['blueprint']['common_libs']['coremedia-tomcat.jar']
