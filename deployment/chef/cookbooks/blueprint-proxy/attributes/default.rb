rewrite_log_level = node['apache']['version'] == '2.4' ? 'trace1' : 0
#<> convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead
default['blueprint']['proxy']['cms_host'] = node['fqdn']

default['blueprint']['proxy']['virtual_host']['studio']['host'] = node['blueprint']['proxy']['cms_host']
default['blueprint']['proxy']['virtual_host']['studio']['port'] = '41080'
default['blueprint']['proxy']['virtual_host']['studio']['context'] = 'studio'
default['blueprint']['proxy']['virtual_host']['studio']['server_name'] = "studio.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['studio']['rewrite_log_level'] = rewrite_log_level
default['blueprint']['proxy']['virtual_host']['studio']['server_aliases'] = []

default['blueprint']['proxy']['virtual_host']['preview']['host'] = node['blueprint']['proxy']['cms_host']
default['blueprint']['proxy']['virtual_host']['preview']['port'] = '40980'
default['blueprint']['proxy']['virtual_host']['preview']['context'] = 'blueprint'
default['blueprint']['proxy']['virtual_host']['preview']['rewrite_log_level'] = rewrite_log_level
default['blueprint']['proxy']['virtual_host']['preview']['server_name'] = "preview.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['preview']['server_aliases'] = {}
default['blueprint']['proxy']['virtual_host']['preview']['live_servlet_context'] = 'blueprint'
default['blueprint']['proxy']['virtual_host']['preview']['default_site'] = 'corporate'

default['blueprint']['proxy']['virtual_host']['sitemanager']['host'] = node['blueprint']['proxy']['cms_host']
default['blueprint']['proxy']['virtual_host']['sitemanager']['port'] = '41380'
default['blueprint']['proxy']['virtual_host']['sitemanager']['context'] = 'editor-webstart'
default['blueprint']['proxy']['virtual_host']['sitemanager']['server_name'] = "sitemanager.#{node['blueprint']['hostname']}"
default['blueprint']['proxy']['virtual_host']['sitemanager']['cms_ior_url'] = "http://#{node['blueprint']['proxy']['cms_host']}:41080/coremedia/ior"
default['blueprint']['proxy']['virtual_host']['sitemanager']['wfs_ior_url'] = "http://#{node['blueprint']['proxy']['cms_host']}:43080/workflow/ior"
default['blueprint']['proxy']['virtual_host']['sitemanager']['rewrite_log_level'] = rewrite_log_level

####################################
###     DEVELOPMENT ATTRIBUTES   ###
####################################
#<> The cookbook from which to load the test system overview template
default['blueprint']['proxy']['overview_template']['cookbook'] = 'blueprint-proxy'
#<> The source parameter of the overview template resource
default['blueprint']['proxy']['overview_template']['source'] = 'overview/overview.html.erb'
