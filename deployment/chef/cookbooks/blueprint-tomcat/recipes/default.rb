=begin
#<
This recipe installs all services.
#>
=end

include_recipe 'blueprint-tomcat::content-management-server'
include_recipe 'blueprint-tomcat::workflow-server'
include_recipe 'blueprint-tomcat::content-feeder'
include_recipe 'blueprint-tomcat::user-changes'
include_recipe 'blueprint-tomcat::elastic-worker'
include_recipe 'blueprint-tomcat::caefeeder-preview'
include_recipe 'blueprint-tomcat::cae-preview'
include_recipe 'blueprint-tomcat::studio'
include_recipe 'blueprint-tomcat::sitemanager'
include_recipe 'blueprint-tomcat::master-live-server'
include_recipe 'blueprint-tomcat::caefeeder-live'
include_recipe 'blueprint-tomcat::cae-live'
