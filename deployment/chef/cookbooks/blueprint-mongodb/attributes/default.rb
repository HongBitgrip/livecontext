#<> define ulimits for mongod
default['ulimit'] = {
  users: {
    mongod: {
      filehandle_limit: 655_36,
      process_limit: 655_36
    }
  }
}

#<> Disable Transparent Huge Pages (THP)
default['mongodb3']['mongod']['disable-transparent-hugepages'] = false

default['mongodb']['yum']['mirrorlist'] = nil
default['mongodb']['yum']['baseurl'] = 'https://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/3.2/x86_64'

#<> fixed version
default['mongodb3']['version'] = '3.4.9'

#<> fixed configuration
default['mongodb3']['config']['mongod']['net']['bindIp'] = '0.0.0.0'
default['mongodb3']['config']['mongod']['net']['http']['RESTInterfaceEnabled'] = true
default['mongodb3']['config']['mongod']['security']['authorization'] = nil
default['mongodb3']['config']['mongod']['storage']['mmapv1']['smallFiles'] = nil
# default['mongodb3']['config']['mongod']['storage']['journal']['enabled'] =  true
