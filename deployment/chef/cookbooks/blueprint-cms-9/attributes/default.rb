rewrite_log_level = node['apache']['version'] == '2.4' ? 'trace1' : 0
default['blueprint']['cms-9']['ssl_proxy_verify'] = true
#<> convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead
default['blueprint']['cms-9']['cms_host'] = 'localhost'

default['blueprint']['cms-9']['virtual_host']['delivery']['cluster']['default']['host'] = node['blueprint']['cms-9']['cms_host']
default['blueprint']['cms-9']['virtual_host']['delivery']['cluster']['default']['port'] = '42180'
default['blueprint']['cms-9']['virtual_host']['delivery']['context'] = 'blueprint'
default['blueprint']['cms-9']['virtual_host']['delivery']['rewrite_log_level'] = rewrite_log_level
default['blueprint']['cms-9']['virtual_host']['delivery']['sites']['corporate']['server_name'] = "corporate.#{node['blueprint']['hostname']}"
default['blueprint']['cms-9']['virtual_host']['delivery']['sites']['corporate']['default_site'] = 'corporate'
#<> The id property of the CMSite content associated with this site
default['blueprint']['cms-9']['virtual_host']['delivery']['sites']['corporate']['site_id'] = 'abffe57734feeee'
