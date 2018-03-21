=begin
#<
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

@param name The name of the virtual host config file.
@param virtual_host_template The template to render the virtual host config frame. Defaults to `virtual-host.erb`.
@param virtual_host_template_cookbook The cookbook from which to load the virtual host template. Defaults to this cookbook.
@param proxy_template The path of the proxy configuration template. Defaults to `proxy/servlet.erb`.
@param proxy_template_cookbook The cookbook from which to load the proxy template. Defaults to this cookbook.
@param rewrite_template The path to the rewrite rules for this virtual host. Defaults to `rewrite/<name param>`.
@param rewrite_template_cookbook The cookbook from which to load the rewrite template. Defaults to the cookbook that declares this definition.
@param enable_ssl A flag to disable SSL configuration and rendering of ssl virtual hosts. Defaults to true.
@param server_name The name for the virtual host. Defaults to the name of the definition. (in default virtual_host_template)
@param server_aliases An array of aliases for the virtual host. (in default virtual_host_template) (optional)
@param rewrite_log_level The log level for the rewrite engine. (in default virtual_host_template) (optional)
@param apache_log_level The log level for the virtual host. (in default virtual_host_template) (optional)
@param custom_conf An optional array of custom directives. (in default virtual_host_template) (optional)
@param protocol The protocol to talk with the servlet container (`ajp` | `http` | `https` ) defaults to `http`. (in default proxy_template) (optional)
@param default_servlet The default servlet to route requests to. For coremedia Spring based webapps, set this to `servlet`. (in default proxy_template) (optional)
@param servlet_context The webapps context path. This definition assumes all webapps in one cluster use the same context path. (in default proxy_template).
@param cluster A hash containing the balancer member configuration. (in default proxy_template) See cluster configuration section for more information.  (in default proxy_template)

@section Cluster configuration
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

@section Examples

```ruby
coremedia_proxy_webapp 'my.server.com' do
  server_aliases %w(alias.my.server.com)
  servlet_context 'myapp'
  cluster('default' => { 'host' => 'localhost', 'port' => '8080' })
          'other' => { 'host' => 'otherhost.server.com', 'port' => '8080' })
end
```

#>
=end
define :coremedia_proxy_webapp, :enable_ssl => true, :servlet_context => '' do
  include_recipe 'coremedia-proxy'
  include_recipe 'coremedia-proxy::ssl' if params[:enable_ssl]

  server_name ||= params[:server_name] ||= params[:name]
  server_aliases ||= params[:server_aliases] ||= []
  virtual_host_template ||= params[:virtual_host_template] ||= 'virtual-host.erb'
  virtual_host_template_cookbook ||= params[:virtual_host_template_cookbook] ||= 'coremedia-proxy'
  proxy_template ||= params[:proxy_template] ||= 'proxy/servlet.erb'
  proxy_template_cookbook ||= params[:proxy_template_cookbook] ||= 'coremedia-proxy'
  rewrite_template = params[:rewrite_template] ||= "rewrite/#{params[:name]}.erb"
  rewrite_template_cookbok ||= params[:rewrite_template_cookbok]
  rewrite_log_level ||= params[:rewrite_log_level] ||= node['apache']['version'] == '2.4' ? 'trace1' : 0
  name = params[:name]
  parent_params = params
  web_app name do
    cookbook virtual_host_template_cookbook
    template virtual_host_template
    port 80
    server_name server_name
    server_aliases server_aliases
    rewrite_template rewrite_template
    rewrite_template_cookbook rewrite_template_cookbok unless rewrite_template_cookbok.nil?
    proxy_template proxy_template
    proxy_template_cookbook proxy_template_cookbook
    parent_params parent_params
    rewrite_log_level rewrite_log_level
  end

  web_app "#{name}-ssl" do
    cookbook virtual_host_template_cookbook
    template virtual_host_template
    port 443
    server_name server_name
    server_aliases server_aliases
    rewrite_template rewrite_template
    rewrite_template_cookbook rewrite_template_cookbok unless rewrite_template_cookbok.nil?
    proxy_template proxy_template
    proxy_template_cookbook proxy_template_cookbook
    parent_params parent_params
    rewrite_log_level rewrite_log_level
    ssl true
  end
end
