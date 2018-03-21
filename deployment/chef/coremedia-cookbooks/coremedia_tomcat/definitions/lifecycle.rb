=begin
#<
This definition controls the lifecycle of webapps and its enclosing service. By injecting the tomcat resource as well as
all webapp resources as parameters into this definition. This definition can encapsulate the logic, when to restart the service
and when to redeploy the webapps. See description of the webapp LWRP for more details about how to notify changes.

@param tomcat A `coremedia_tomcat` resource.  If not set, the definition will look for a `coremedia_tomcat` resource with the same name as the definition.
@param webapps An array of `coremedia_tomcat_webapp` resources. If not set, the definition will look for a `coremedia_tomcat_webapp` resource with the same name as the definition.
@param undeploy_unmanaged Undeploy unmanaged webapps.
@param start_service Set this to false to skip service start.
@param enable_service Set this to false to not enable the service.

@section Examples

```ruby
tomcat_resource = coremedia_tomcat 'solr' do
...
end

solr_webapp_resource = coremedia_tomcat_webapp 'solr do
...
end

coremedia_tomcat_service_lifecycle 'solr' do
  tomcat tomcat_resource
  webapps [solr_webapp_resource]
end
```

#>
=end
define :coremedia_tomcat_service_lifecycle, :undeploy_unmanaged => true, :start_service => true, :enable_service => true do
  params[:tomcat] ||= resources(coremedia_tomcat: params[:name])
  params[:webapps] ||= [resources(coremedia_tomcat_webapp: params[:name])]

  log "lifecycle #{params[:name]} needs at least one webapp as a parameter" do
    level :error
    only_if { params[:webapps].empty? }
  end

  log "lifecycle #{params[:name]} needs at least the tomcat resource as a paramter" do
    level :error
    not_if { params[:tomcat] }
  end

  service "lifecycle #{params[:name]} - coremedia_tomcat service #{params[:tomcat].name} - stop" do
    service_name params[:tomcat].name
    supports :restart => true, :status => true
    action :stop
    only_if { params[:tomcat].updated_by_last_action? || params[:webapps].collect(&:updated_by_last_action?).any? }
  end

  app_base = "#{params[:tomcat].path}/current/webapps"
  webapp_paths = []
  params[:webapps].each do |webapp|
    doc_base = "#{app_base}/#{webapp.context}"
    directory "lifecycle #{params[:name]} - undeploy webapp #{webapp.name}" do
      path doc_base
      action :delete
      recursive true
      only_if { webapp.updated_by_last_action? }
    end

    if webapp.path.end_with?('.war')
      link "lifecycle #{params[:name]} - deploy webapp #{webapp.name} using a symlink" do
        target_file "#{doc_base}.war"
        to webapp.path
      end
      webapp_paths << "#{doc_base}.war"
    else
      execute "lifecycle #{params[:name]} - deploy webapp #{webapp.name} copying the exploded dir" do
        command "cp -r #{webapp.path} #{app_base}/#{webapp.context}"
        user params[:tomcat].user
        group params[:tomcat].group
        only_if { webapp.updated_by_last_action? || !::File.exist?("#{app_base}/#{webapp.context}") }
      end
      webapp_paths << "#{app_base}/#{webapp.context}"
    end
  end

  ruby_block "lifecycle #{params[:name]} - undeploy unmanaged webapps" do
    block do
      entries = Dir.entries(app_base)
      entries.delete('..')
      entries.delete('.')
      params[:webapps].each do |w|
        entries.delete(w.context)
        entries.delete("#{w.context}.war")
      end
      entries.each do |context|
        Chef::Log.info("Lifecycle #{params[:name]} - undeploy unmanaged webapp from context #{context}")
        ::FileUtils.rm_rf("#{app_base}/#{context}")
      end
    end
    only_if { params[:undeploy_unmanaged] }
  end
  actions = []
  actions << :start if params[:start_service]
  actions << :enable if params[:enable_service]
  service "lifecycle #{params[:name]} - coremedia_tomcat service #{params[:tomcat].name} - #{actions}" do
    service_name params[:tomcat].name
    supports :restart => true, :status => true
    action actions
  end
end
