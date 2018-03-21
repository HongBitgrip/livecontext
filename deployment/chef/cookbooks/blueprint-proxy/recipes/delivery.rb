=begin
#<
This recipe installs virtual hosts for the CoreMedia Live CAE.

The recipe accepts an attribute hash of the following form:

```
"blueprint": {
  "proxy": {
    "virtual_host": {
      "delivery": {
        "rewrite_log_level": "trace1",
        "context": "blueprint",
        "cluster": {
          "blue-1": {
            "host": "blue.blueprint.com",
            "port": "42180"
          },
          "blue-2": {
            "host": "blue.blueprint.com",
            "port": "42280"
          },
          "green-1": {
            "host": "green.blueprint.com",
            "port": "42180"
          },
          "green-2": {
            "host": "green.blueprint.com",
            "port": "42280"
          }

        },
        "sites": {
          "corporate": {
            "server_name": "corporate.blueprint.com",
            "server_aliases": ["blueprint.com"],
            "default_site": "corporate"
          }
        }
      }
    }
  }
}
```

In the example above, a virtual host `corporate.blueprint.com` is being created backed by a cluster of 4 CAEs running on
two hosts.

#>
=end

include_recipe 'coremedia-proxy'

if node.deep_fetch('blueprint', 'proxy', 'virtual_host', 'delivery', 'sites')
  if node.deep_fetch('blueprint', 'tomcat', 'cae-live', 'instances')
    (1..node['blueprint']['tomcat']['cae-live']['instances']).to_a.each do |i|
      node.default['blueprint']['proxy']['virtual_host']['delivery']['cluster']["cae-live-#{i}"]['host'] = node['fqdn']
      node.default['blueprint']['proxy']['virtual_host']['delivery']['cluster']["cae-live-#{i}"]['port'] = "#{node['blueprint']['tomcat']["cae-live-#{i}"]['port_prefix']}80"
    end
  end

  node['blueprint']['proxy']['virtual_host']['delivery']['sites'].keys.each do |site|
    server_name = node['blueprint']['proxy']['virtual_host']['delivery']['sites'][site]['server_name']
    server_aliases = node['blueprint']['proxy']['virtual_host']['delivery']['sites'][site]['server_aliases']
    default_site = node['blueprint']['proxy']['virtual_host']['delivery']['sites'][site]['default_site']
    site_id = node['blueprint']['proxy']['virtual_host']['delivery']['sites'][site]['site_id']

    coremedia_proxy_webapp "delivery-#{site}" do
      server_name server_name
      server_aliases server_aliases
      servlet_context node['blueprint']['proxy']['virtual_host']['delivery']['context']
      default_servlet 'servlet'
      cluster node['blueprint']['proxy']['virtual_host']['delivery']['cluster']
      default_site default_site
      site_id site_id
      rewrite_template 'rewrite/delivery.erb'
      rewrite_log_level node['blueprint']['proxy']['virtual_host']['delivery']['rewrite_log_level']
    end
  end
end
