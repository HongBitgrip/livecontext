# Description

This is an application cookbook, it provides recipes to set up a webserver (apache httpd) for the CoreMedia Blueprint stack.
# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* blueprint-tomcat
* coremedia-proxy (~> 0.3.2)

# Attributes

* `node['blueprint']['proxy']['cms_host']` - convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead. Defaults to `node['fqdn']`.
* `node['blueprint']['proxy']['virtual_host']['studio']['host']` -  Defaults to `node['blueprint']['proxy']['cms_host']`.
* `node['blueprint']['proxy']['virtual_host']['studio']['port']` -  Defaults to `41080`.
* `node['blueprint']['proxy']['virtual_host']['studio']['context']` -  Defaults to `studio`.
* `node['blueprint']['proxy']['virtual_host']['studio']['server_name']` -  Defaults to `studio.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['studio']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['proxy']['virtual_host']['studio']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['proxy']['virtual_host']['preview']['host']` -  Defaults to `node['blueprint']['proxy']['cms_host']`.
* `node['blueprint']['proxy']['virtual_host']['preview']['port']` -  Defaults to `40980`.
* `node['blueprint']['proxy']['virtual_host']['preview']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['proxy']['virtual_host']['preview']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['proxy']['virtual_host']['preview']['server_name']` -  Defaults to `preview.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['preview']['server_aliases']` -  Defaults to `{ ... }`.
* `node['blueprint']['proxy']['virtual_host']['preview']['live_servlet_context']` -  Defaults to `blueprint`.
* `node['blueprint']['proxy']['virtual_host']['preview']['default_site']` -  Defaults to `corporate`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['host']` -  Defaults to `node['blueprint']['proxy']['cms_host']`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['port']` -  Defaults to `41380`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['context']` -  Defaults to `editor-webstart`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['server_name']` -  Defaults to `sitemanager.#{node['blueprint']['hostname']}`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['cms_ior_url']` -  Defaults to `http://#{node['blueprint']['proxy']['cms_host']}:41080/coremedia/ior`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['wfs_ior_url']` -  Defaults to `http://#{node['blueprint']['proxy']['cms_host']}:43080/workflow/ior`.
* `node['blueprint']['proxy']['virtual_host']['sitemanager']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['proxy']['overview_template']['cookbook']` - The cookbook from which to load the test system overview template. Defaults to `blueprint-proxy`.
* `node['blueprint']['proxy']['overview_template']['source']` - The source parameter of the overview template resource. Defaults to `overview/overview.html.erb`.

# Recipes

* blueprint-proxy::candy-preview-proxy
* blueprint-proxy::candy-studio-proxy
* [blueprint-proxy::default](#blueprint-proxydefault) - This recipe wraps all recipes of this cookbook for apache running in front of tomcat.
* [blueprint-proxy::delivery](#blueprint-proxydelivery) - This recipe installs virtual hosts for the CoreMedia Live CAE.
* [blueprint-proxy::overview](#blueprint-proxyoverview)
* [blueprint-proxy::preview](#blueprint-proxypreview) - This recipe installs virtual hosts for the CoreMedia Preview CAE.
* [blueprint-proxy::sitemanager](#blueprint-proxysitemanager) - This recipe installs virtual hosts for the CoreMedia Sitemanager.
* [blueprint-proxy::studio](#blueprint-proxystudio) - This recipe installs virtual hosts for the CoreMedia Studio.

## blueprint-proxy::default

This recipe wraps all recipes of this cookbook for apache running in front of tomcat. To work with websphere add websphere recipe before this recipe.

## blueprint-proxy::delivery

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


## blueprint-proxy::overview

creates a simple overview page with all necessary links at `overview.<hostname>` for dev systems

## blueprint-proxy::preview

This recipe installs virtual hosts for the CoreMedia Preview CAE.

The recipe accepts an attribute hash of the following form:

```
"blueprint": {
  "proxy": {
    "virtual_host": {
      "preview": {
        "rewrite_log_level": "trace1",
        "context": "blueprint",
        "default_site": "asite"
        "host": "blue.blueprint.com",
        "port": "40980"
        "server_name": "preview.blueprint.com",
        "server_aliases": {
          "asite": "preview-asite.blueprint.com"
        }
      }
    }
  }
}
```

In the example above, the preview virtual host `preview.blueprint.com` is being created. It will have the alias
`preview-asite.blueprint.com`.


## blueprint-proxy::sitemanager

This recipe installs virtual hosts for the CoreMedia Sitemanager.

## blueprint-proxy::studio

This recipe installs virtual hosts for the CoreMedia Studio.

# Author

Author:: Your Name (<your_name@domain.com>)
