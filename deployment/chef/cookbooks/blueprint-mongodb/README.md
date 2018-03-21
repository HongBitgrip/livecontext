# Description

This is the wrapper cookbook to install mongodb3.

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* ulimit (~> 0.3.0)
* mongodb3 (~> 5.3.0)

# Attributes

* `node['ulimit']` - define ulimits for mongod. Defaults to `{ ... }`.
* `node['mongodb3']['mongod']['disable-transparent-hugepages']` - Disable Transparent Huge Pages (THP). Defaults to `false`.
* `node['mongodb']['yum']['mirrorlist']` -  Defaults to `nil`.
* `node['mongodb']['yum']['baseurl']` -  Defaults to `https://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/3.2/x86_64`.
* `node['mongodb3']['version']` - fixed version. Defaults to `3.4.9`.
* `node['mongodb3']['config']['mongod']['net']['bindIp']` - fixed configuration. Defaults to `0.0.0.0`.
* `node['mongodb3']['config']['mongod']['net']['http']['RESTInterfaceEnabled']` -  Defaults to `true`.
* `node['mongodb3']['config']['mongod']['security']['authorization']` -  Defaults to `nil`.
* `node['mongodb3']['config']['mongod']['storage']['mmapv1']['smallFiles']` -  Defaults to `nil`.

# Recipes

* blueprint-mongodb::default
* blueprint-mongodb::disable-thp

# Author

Author:: Bodo Schulz (<bodo.schulz@coremedia.com>)

