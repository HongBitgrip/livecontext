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
