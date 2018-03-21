rewrite_log_level = node['apache']['version'] == '2.4' ? 'trace1' : 0
#<> convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead
default['blueprint']['lc3-sap-hybris']['cms_host'] = 'localhost'
# Hybris configuration
default['blueprint']['lc3-sap-hybris']['host'] = 'hybrishost'
default['blueprint']['lc3-sap-hybris']['application.properties']['livecontext.hybris.host'] = node['blueprint']['lc3-sap-hybris']['host']
default['blueprint']['lc3-sap-hybris']['application.properties']['livecontext.cookie.domain'] = ".#{node['fqdn']}"

default['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['cluster']['default']['host'] = node['blueprint']['lc3-sap-hybris']['cms_host']
default['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['cluster']['default']['port'] = '42180'
default['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['context'] = 'blueprint'
default['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['rewrite_log_level'] = rewrite_log_level
default['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['sites']['apparel']['server_name'] = "apparel.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['sites']['apparel']['default_site'] = 'apparelhomepage'
#<> The id property of the CMSite content associated with this site
default['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['sites']['apparel']['site_id'] = 'Hybris-Apparel-UK-Site-ID'

default['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['server_name'] = "shop-preview-apparel.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['time_travel_alias'] = "shop-preview-apparel.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['server_aliases'] = []
default['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['rewrite_log_level'] = rewrite_log_level

default['blueprint']['lc3-sap-hybris']['virtual_host']['shop']['server_name'] = "shop-apparel.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sap-hybris']['virtual_host']['shop']['time_travel_alias'] = "shop-apparel.#{node['blueprint']['hostname']}"
default['blueprint']['lc3-sap-hybris']['virtual_host']['shop']['server_aliases'] = []
default['blueprint']['lc3-sap-hybris']['virtual_host']['shop']['rewrite_log_level'] = rewrite_log_level

# set this to true to disable SSLProxyVerify, SSLProxyCheckPeerCN, SSLProxyCheckPeerName
default['blueprint']['lc3-sap-hybris']['ssl_proxy_verify'] = true
