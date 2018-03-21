=begin
#<
This recipe creates the blueprint user and sets its home directory and process ulimits. This user should be used to for login
and tools.
#>
=end

include_recipe 'sysctl::apply'
if node['java_se'] && node['java_se']['java_home']
  node.default_unless['blueprint']['tools']['java_home'] = node['java_se']['java_home']
  node.default_unless['blueprint']['tomcat']['java_home'] = node['java_se']['java_home']
end

user node['blueprint']['user'] do
  home '/opt/coremedia'
  not_if { node['blueprint']['user'] == 'root' }
  not_if { node['etc']['passwd'][node['blueprint']['user']] }
end

directory node['blueprint']['base_dir'] do
  owner node['blueprint']['user']
  group node['blueprint']['group']
  mode 0775
end

directory node['blueprint']['log_dir'] do
  owner node['blueprint']['user']
  group node['blueprint']['group']
  mode 0775
end

directory node['blueprint']['cache_dir'] do
  owner node['blueprint']['user']
  group node['blueprint']['group']
  mode 0775
end

directory node['blueprint']['temp_dir'] do
  owner node['blueprint']['user']
  group node['blueprint']['group']
  mode 0775
end

user_ulimit node['blueprint']['user'] do
  filehandle_limit 25_000
  process_limit 5000
end
