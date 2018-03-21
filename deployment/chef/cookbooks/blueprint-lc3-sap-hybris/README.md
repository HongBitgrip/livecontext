# Description

This is the wrapper cookbook to deploy CoreMedia Blueprint
Livecontext for SAP Hybris.

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* blueprint-tomcat
* blueprint-proxy
* coremedia-proxy (~> 0.3.2)

# Attributes

* `node['blueprint']['lc3-sap-hybris']['cms_host']` - convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead. Defaults to `localhost`.
* `node['blueprint']['lc3-sap-hybris']['host']` -  Defaults to `hybrishost`.
* `node['blueprint']['lc3-sap-hybris']['application.properties']['livecontext.hybris.host']` -  Defaults to `node['blueprint']['lc3-sap-hybris']['host']`.
* `node['blueprint']['lc3-sap-hybris']['application.properties']['livecontext.cookie.domain']` -  Defaults to `.#{node['fqdn']}`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['cluster']['default']['host']` -  Defaults to `node['blueprint']['lc3-sap-hybris']['cms_host']`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['cluster']['default']['port']` -  Defaults to `42180`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['sites']['apparel']['server_name']` -  Defaults to `apparel.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['sites']['apparel']['default_site']` -  Defaults to `apparelhomepage`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['delivery']['sites']['apparel']['site_id']` - The id property of the CMSite content associated with this site. Defaults to `Hybris-Apparel-UK-Site-ID`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['server_name']` -  Defaults to `shop-preview-apparel.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['time_travel_alias']` -  Defaults to `shop-preview-apparel.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['shop-preview']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['shop']['server_name']` -  Defaults to `shop-apparel.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['shop']['time_travel_alias']` -  Defaults to `shop-apparel.#{node['blueprint']['hostname']}`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['shop']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['lc3-sap-hybris']['virtual_host']['shop']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['lc3-sap-hybris']['ssl_proxy_verify']` -  Defaults to `true`.

# Recipes

* [blueprint-lc3-sap-hybris::cae-live-config](#blueprint-lc3-sap-hybriscae-live-config) - This recipe configures the CoreMedia Blueprint Live CAE.
* [blueprint-lc3-sap-hybris::cae-preview-config](#blueprint-lc3-sap-hybriscae-preview-config) - This recipe configures the CoreMedia Blueprint Preview CAE.
* blueprint-lc3-sap-hybris::candy-shop-preview-proxy
* [blueprint-lc3-sap-hybris::delivery-proxy](#blueprint-lc3-sap-hybrisdelivery-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.
* blueprint-lc3-sap-hybris::overview
* [blueprint-lc3-sap-hybris::shop-preview-proxy](#blueprint-lc3-sap-hybrisshop-preview-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Hybris Preview Shop.
* [blueprint-lc3-sap-hybris::shop-proxy](#blueprint-lc3-sap-hybrisshop-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Hybris Live Shop.
* [blueprint-lc3-sap-hybris::studio-config](#blueprint-lc3-sap-hybrisstudio-config) - This recipe configures the CoreMedia Blueprint Studio.
* [blueprint-lc3-sap-hybris::studio-proxy](#blueprint-lc3-sap-hybrisstudio-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint Studio.
* [blueprint-lc3-sap-hybris::test-data-config](#blueprint-lc3-sap-hybristest-data-config)

## blueprint-lc3-sap-hybris::cae-live-config

This recipe configures the CoreMedia Blueprint Live CAE.

## blueprint-lc3-sap-hybris::cae-preview-config

This recipe configures the CoreMedia Blueprint Preview CAE.

## blueprint-lc3-sap-hybris::delivery-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.

## blueprint-lc3-sap-hybris::shop-preview-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Hybris Preview Shop.

## blueprint-lc3-sap-hybris::shop-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Hybris Live Shop.

## blueprint-lc3-sap-hybris::studio-config

This recipe configures the CoreMedia Blueprint Studio.

## blueprint-lc3-sap-hybris::studio-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint Studio.

## blueprint-lc3-sap-hybris::test-data-config

This recipe sets properties only necessary if the test content is being used

# Author

Author:: Your Name (<your_name@domain.com>)
