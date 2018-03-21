=begin
#<
this is a recipe to test the rpms created with the rpm-build recipe. This is not a recipe to be used in production.
#>
=end
directory '/var/tmp/rpm-repo'
execute "cp -u #{node['blueprint']['dev']['rpm']['dir']}/* /var/tmp/rpm-repo"

include_recipe 'blueprint-yum::default'
include_recipe 'blueprint-yum::local'

# tools to be installed
%w(content-management-server-tools theme-importer-tools master-live-server-tools replication-live-server-tools workflow-server-tools caefeeder-preview-tools caefeeder-live-tools).each do |tool|
  package "#{node['blueprint']['dev']['rpm']['package_prefix']}#{tool}"
end

# install and start order
services = ['content-management-server',
            'workflow-server',
            'content-feeder',
            'user-changes',
            'elastic-worker',
            'caefeeder-preview',
            'cae-preview',
            'studio',
            'sitemanager',
            'master-live-server',
            'caefeeder-live',
            'cae-live-1']

# install rpm and wait 30 seconds before installing the next, services automatically get started
services.each do |tomcat|
  wait_block = ruby_block "wait_before_starting_#{tomcat}" do
    block do
      sleep(10)
    end
    action :nothing
  end
  package "#{node['blueprint']['dev']['rpm']['package_prefix']}#{tomcat}" do
    notifies :run, wait_block, :immediately
  end
end

# now we enable the services
services.each do |tomcat|
  service tomcat do
    action [:enable, :start]
    supports restart: true, status: true
  end
end
