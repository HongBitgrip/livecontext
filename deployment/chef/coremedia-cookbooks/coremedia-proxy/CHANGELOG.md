coremedia-proxy Cookbook CHANGELOG
======================
This file is used to list changes made in each version of the coremedia-proxy cookbook.

0.3.4
-----
- bugfix release to avoid bugs introduced in 0.3.3

0.3.3
-----
- default configuration of <modulename> can now be enabled and disabled
 - example: ```node['apache']['mods']['default_config'][<modulename>] = true```
- currently supported values for <modulename>: autoindex, deflate, expires, headers, mime, rewrite, cors

0.3.2
-----
- add `text/javascript` to `deflate.conf.erb` template
- if cluster size is `1` also render proxy params

0.3.1
-----
- fix virtual host template. Add `NameVirtualHost *:<PORT>` directive. With the update to `apache2` version `>=3.2.0` the `NameVirtualHost` directive
is no longer set globally in the `/etc/httpd/ports.conf`

0.3.0
----
- added `woff2` fonts extension to headers config.
- added `svg` extension to headers config.
- updated `apache2` cookbook to `~> 3.2.0`, see [apache2 changelog](https://supermarket.chef.io/cookbooks/apache2#changelog) for details.
- updated kitchen infrastructure.

0.2.6
-----
- add additional modules to enable gzip of content when supported (mod_deflate)
- add more ouput to test recipe to be able to test gzip
- add switch to disable gzip ETAG (Remove `-gzip` or `-deflate` suffix from incoming ETag values)_

0.2.5
------
- use `@application_name` for cluster name
- fix proxy config if context is empty

0.2.4
------
- add missing newline

0.2.3
------
- improve documentation of definition.
- make ssl configuration optional. Default is true.

0.2.2
------
- inject definition parameter virtual_host template into the param hash so all partialtemplates can access them the same way via `@params[:<key>]`

0.2.1
------
- beautify indentation.

0.2.0
--------
- rename definition `coremedia_proxy_clustered_webapp` into `coremedia_proxy_webapp`
- rename parameter `rewrite_rules_template` to `rewrite_template`
- rename parameter `rewrite_rules_template_cookbook` to `rewrite_template_cookbook`
- definition should not fail if `servlet_context` is not defined. If a custom `proxy_template` is defined it may not be used at all.
- remove default for `default_servlet` argument so it can be omitted if desired.

0.1.0
--------

- initial commit.
