# you need to include the chef_sugar recipe before you can use this helper
# i.e. include_recipe 'chef-sugar::default' like its done in the default recipe
class Chef
  class Recipe
    # this method may be removed in future versions, as it has some deep caveats:
    # in a chef server environment you will find the hostnames by searching for roles but here we have no mapping of
    # role to service, this knowledge is encapsulated solely in the role itself. Here this is just a poor mans convenience search.
    def cm_webapp_url(webapp_key, hostname = node['blueprint'].attribute?('loopback_hostname') ? node['blueprint']['loopback_hostname'] : node['fqdn'])
      port_prefix = node.deep_fetch('blueprint', 'tomcat', webapp_key, 'port_prefix')
      context = node.deep_fetch('blueprint', 'webapps', webapp_key, 'context')
      "http://#{hostname}:#{port_prefix}80/#{context}"
    end

    def cm_tomcat_default(webapp_key, attribute_key, base_service = nil)
      global_default = node.deep_fetch('blueprint', 'tomcat', attribute_key)
      service_default = node.deep_fetch('blueprint', 'tomcat', webapp_key, attribute_key)
      base_service_default = base_service.nil? ? nil : node.deep_fetch('blueprint', 'tomcat', base_service, attribute_key)
      result = global_default
      result = base_service_default unless base_service_default.nil?
      result = service_default unless service_default.nil?
      result
    end

    def cm_webapp(webapp_key)
      resources(coremedia_tomcat_webapp: webapp_key)
    end

    def cm_tomcat(webapp_key)
      resources(coremedia_tomcat: webapp_key)
    end
  end
end
