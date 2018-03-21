=begin
#<
This recipe installs and configures postgresql.
#>
=end

include_recipe 'blueprint-postgresql::_base'
include_recipe 'postgresql::config_initdb'
include_recipe 'postgresql::config_pgtune'
include_recipe 'postgresql::server'
