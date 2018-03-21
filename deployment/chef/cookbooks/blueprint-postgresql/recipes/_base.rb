=begin
#<
This recipe installs and configures postgresql and creates schemas for CoreMedia Blueprint.
#>
=end
require 'chef/version_constraint'

node.default['postgresql']['version'] = '9.3'
node.default['postgresql']['server']['service_name'] = 'postgresql-9.3'
node.default['postgresql']['enable_pgdg_yum'] = false
node.default['yum']['pgdg']['version'] = node['postgresql']['version']
node.default['postgresql']['server']['service_name'] = 'postgresql-9.3'
node.default['postgresql']['server']['packages'] = ['postgresql93-server']
node.default['postgresql']['client']['packages'] = ['postgresql93-devel']
node.default['postgresql']['dir'] = '/var/lib/pgsql/data'
node.default['postgresql']['password']['postgres'] = 'coremedia'
node.default['postgresql']['config']['listen_addresses'] = '*'
node.default['postgresql']['config']['log_directory'] = '/var/log/pgsql'
node.default['postgresql']['config_pgtune']['db_type'] = 'web'
# the amount of free memory from which pgtune algorithm calculates its values
node.default['postgresql']['config_pgtune']['total_memory'] = '2097152kB'
node.default['postgresql']['pg_hba'] = [{ 'type' => 'local', 'db' => 'all', 'user' => 'postgres', 'addr' => nil, 'method' => 'ident' },
                                        { 'type' => 'local', 'db' => 'all', 'user' => 'all', 'addr' => nil, 'method' => 'ident' },
                                        { 'type' => 'host', 'db' => 'all', 'user' => 'all', 'addr' => '127.0.0.1/32', 'method' => 'md5' },
                                        { 'type' => 'host', 'db' => 'all', 'user' => 'all', 'addr' => '::1/128', 'method' => 'md5' },
                                        { 'type' => 'host', 'db' => 'all', 'user' => 'all', 'addr' => '10.0.0.0/8', 'method' => 'md5' }]

# only for systemd
if Chef::VersionConstraint.new('>= 7.0.0').include?(node['platform_version'])
  node.default['postgresql']['setup_script'] = '/usr/pgsql-9.3/bin/postgresql93-setup'
end

include_recipe 'blueprint-base::default'
include_recipe 'blueprint-yum::default'
include_recipe 'yum-pgdg::default'
