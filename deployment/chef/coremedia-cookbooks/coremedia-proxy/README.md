# Description

This is a wrapper cookbook for the apache2 cookbook with a library cookbook style. The cookbook sets up a webserver
(apache httpd) and provides you with convenience definitions to set up virtual host configurations.
# Requirements

## Platform:

* redhat
* centos
* amazon

## Cookbooks:

* apache2 (~> 3.2.0)
* chef-sugar (~> 3.0)

# Attributes

*No attributes defined*

# Recipes

* [coremedia-proxy::default](#coremedia-proxydefault) - This recipe installs Apache HTTPD and installs modules as well as configuring global settings.
* [coremedia-proxy::ssl](#coremedia-proxyssl) - This recipe installs Apache HTTPD and installs modules as well as configuring global settings.

## coremedia-proxy::default

This recipe installs Apache HTTPD and installs modules as well as configuring global settings.

## coremedia-proxy::ssl

This recipe installs Apache HTTPD and installs modules as well as configuring global settings. It also configures mod_ssl.

# Definitions

* [coremedia_proxy_webapp](#coremedia_proxy_webapp) - This definition creates an opinionated virtual host setup.

## coremedia_proxy_webapp

This definition creates an opinionated virtual host setup. The setup consists of three different templates that are setup
to render one virtual host configuration file. The base template is configuable using the `virtual_host_template` parameter and
the default should work with most standard CoreMedia webapps. Within this template a `rewrite_template` and a `proxy_template`
are rendered as partial templates. The `proxy_template` has a default, whereas the `rewrite_template` does not.

Each template parameter has a sibling parameter to configure the cookbook the template comes from. Because the `rewrite_template`
is to be expected to reside in the cookbook from which you use the definition its `cookbook` sibling parameter has no default so you don't
need to define this parameter in most cases too. The `virtual_host_template` passes all parameter you set on this definition to the
proxy and rewrite templates so you can access all parameters you set to this definition via the `params[:<parameter name]` hash.

In the parameter list below all parameter, the default template in which they are used is noted. If thats the case and you use your own templates,
you can omit them.

DO NOT SET ONE OF THE FOLLOWING PARAMETER:

* `ssl`
* `parent_params`

### Parameters

- name: The name of the virtual host config file..
- virtual_host_template: The template to render the virtual host config frame. Defaults to `virtual-host.erb`..
- virtual_host_template_cookbook: The cookbook from which to load the virtual host template. Defaults to this cookbook..
- proxy_template: The path of the proxy configuration template. Defaults to `proxy/servlet.erb`..
- proxy_template_cookbook: The cookbook from which to load the proxy template. Defaults to this cookbook..
- rewrite_template: The path to the rewrite rules for this virtual host. Defaults to `rewrite/<name param>`..
- rewrite_template_cookbook: The cookbook from which to load the rewrite template. Defaults to the cookbook that declares this definition..
- enable_ssl: A flag to disable SSL configuration and rendering of ssl virtual hosts. Defaults to true.. Defaults to: true
- server_name: The name for the virtual host. Defaults to the name of the definition. (in default virtual_host_template).
- server_aliases: An array of aliases for the virtual host. (in default virtual_host_template) (optional).
- rewrite_log_level: The log level for the rewrite engine. (in default virtual_host_template) (optional).
- apache_log_level: The log level for the virtual host. (in default virtual_host_template) (optional).
- custom_conf: An optional array of custom directives. (in default virtual_host_template) (optional).
- protocol: The protocol to talk with the servlet container (`ajp` | `http` | `https` ) defaults to `http`. (in default proxy_template) (optional).
- default_servlet: The default servlet to route requests to. For coremedia Spring based webapps, set this to `servlet`. (in default proxy_template) (optional).
- servlet_context: The webapps context path. This definition assumes all webapps in one cluster use the same context path. (in default proxy_template).. Defaults to:
- cluster: A hash containing the balancer member configuration. (in default proxy_template) See cluster configuration section for more information.  (in default proxy_template).

### Cluster configuration
The cluster parameter defines a map with the following structure, where `CLUSTER NAME` is an arbitrary string and all keys must be of type String and not Symbols:

```ruby
{ '<CLUSTER NAME' => {
  'host' => '<SERVLET CONTAINER HOST>',
  'port' => '<SERVLET CONTAINER PORT>',
  'params' => {
    '<KEY>' => '<VALUE>',
    ...
  },
  ...
}
```

### Examples

```ruby
coremedia_proxy_webapp 'my.server.com' do
  server_aliases %w(alias.my.server.com)
  servlet_context 'myapp'
  cluster('default' => { 'host' => 'localhost', 'port' => '8080' })
          'other' => { 'host' => 'otherhost.server.com', 'port' => '8080' })
end
```
# Author

Author:: Felix Simmendinger (<felix.simmendinger@coremedia.com>)

Author:: Daniel Zabel (<daniel.zabel@coremedia.com>)
