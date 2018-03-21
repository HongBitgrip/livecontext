# Description

This is the wrapper cookbook to deploy this CoreMedia Blueprint variant.

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* blueprint-tomcat
* blueprint-proxy
* coremedia-proxy (~> 0.3.2)

# Attributes

* `node['blueprint']['cms-9']['ssl_proxy_verify']` -  Defaults to `true`.
* `node['blueprint']['cms-9']['cms_host']` - convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead. Defaults to `localhost`.
* `node['blueprint']['cms-9']['virtual_host']['delivery']['cluster']['default']['host']` -  Defaults to `node['blueprint']['cms-9']['cms_host']`.
* `node['blueprint']['cms-9']['virtual_host']['delivery']['cluster']['default']['port']` -  Defaults to `42180`.
* `node['blueprint']['cms-9']['virtual_host']['delivery']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['cms-9']['virtual_host']['delivery']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['cms-9']['virtual_host']['delivery']['sites']['corporate']['server_name']` -  Defaults to `corporate.#{node['blueprint']['hostname']}`.
* `node['blueprint']['cms-9']['virtual_host']['delivery']['sites']['corporate']['default_site']` -  Defaults to `corporate`.
* `node['blueprint']['cms-9']['virtual_host']['delivery']['sites']['corporate']['site_id']` - The id property of the CMSite content associated with this site. Defaults to `abffe57734feeee`.

# Recipes

* [blueprint-cms-9::delivery-proxy](#blueprint-cms-9delivery-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.
* blueprint-cms-9::overview
* [blueprint-cms-9::test-data-config](#blueprint-cms-9test-data-config)

## blueprint-cms-9::delivery-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.

## blueprint-cms-9::test-data-config

This recipe sets properties only necessary if the test content is being used

# Author

Author:: Your Name (<your_name@domain.com>)
