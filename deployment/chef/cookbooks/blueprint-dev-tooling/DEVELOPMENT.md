## Build RPMs

Building RPMs was one of the key features introduced with the Blueprint years ago and it served well but it was hard to change the setup to a customers need. With the plain
Chef based setup changing the service orchestration or modifying its configuration is simple and straight forward. For customers that still want to build RPMs, the `blueprint-dev-tooling`
cookbook offers a simple solution. The `blueprint-dev-tooling::build-rpm` recipe installs all services using chef but it does not start them. Instead after the installation it uses
[fpm](https://github.com/jordansissel/fpm) to build rpms from the installed folders. This technique offers the whole flexibility of chef to build the rpms and the well known rpm installation
process. 
However using Chef to install the RPMs is not recommended and a plain Chef setup is always the preferrable solution.

To build RPMs, proceed as follows:

1. Configure the chef attributes to match your target system. You can do that by adding a recipe that sets all attributes accordingly and then includes the `build-rpm` recipe.
2. Execute in the `cookbooks/blueprint-dev-tooling` folder chef generate recipe `stage-env-rpms`. The recipe will be generated at 
   `blueprint-cookbook/blueprint-dev-tooling/recipes/stage-env-rpms.rb`.
3. Add attributes as you wish:

                node.default['blueprint']['webapps']['studio']['application.properties']['repository.url'] = 'http://myhost:40180/coremedia/ior'
                ...                    
4. Include the `rpb-build` recipe

                include_recipe 'blueprint-dev-tooling::rpm-build'
              
> By default the Chef will set `node['fqdn']` as its hostname so make sure you set all properties correctly. You could create a verification step that searchs the
> installation folder for the default name `<suite>-<platform>` and fail if still one found. You could use target systems host names and modify the `/etc/hosts` so that
> all app think they are talking remotely but don't. 
