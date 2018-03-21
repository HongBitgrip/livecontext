=begin
#<
This recipe installs Apache HTTPD and installs modules as well as configuring global settings.
#>
=end

include_recipe 'chef-sugar::default'
include_recipe 'apache2::default'
include_recipe 'apache2::mod_proxy'
include_recipe 'apache2::mod_status'
include_recipe 'apache2::mod_deflate'
include_recipe 'apache2::mod_expires'
include_recipe 'apache2::mod_headers'
include_recipe 'apache2::mod_mime'
include_recipe 'apache2::mod_filter'
include_recipe 'apache2::mod_setenvif'
include_recipe 'apache2::mod_proxy_balancer'
include_recipe 'apache2::mod_lbmethod_byrequests' if version(node['apache']['version']).satisfies?('>= 2.3')
include_recipe 'apache2::mod_proxy_http'
include_recipe 'apache2::mod_proxy_ajp'

apache_conf 'global-settings' do
  source 'global-settings.conf.erb'
  cookbook 'coremedia-proxy'
end

# load default apache config for defined mods
node.default_unless['apache']['mods']['default_config']['autoindex'] = true
node.default_unless['apache']['mods']['default_config']['deflate'] = true
node.default_unless['apache']['mods']['default_config']['expires'] = true
node.default_unless['apache']['mods']['default_config']['headers'] = true
node.default_unless['apache']['mods']['default_config']['mime'] = true
node.default_unless['apache']['mods']['default_config']['rewrite'] = true
node.default_unless['apache']['mods']['default_config']['cors'] = true

node['apache']['mods']['default_config'].each_pair do |mod, enabled|
  apache_conf mod do
    source "mods/#{mod}.conf.erb"
    cookbook 'coremedia-proxy'
    enable enabled
  end
  # because apache_conf definition only enables configuration, we need to call
  # apache_config separatelyto disable if needed
  apache_config mod do
    enable enabled
  end
end
