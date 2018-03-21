remote_directory node['chef_handler']['handler_path'] do
  source 'handlers'
  mode '0755'
  recursive true
  action :nothing
end.run_action(:create)

chef_handler 'CoreMedia::ElapsedTimeReport' do
  source "#{node['chef_handler']['handler_path']}/elapsed_time_report.rb"
  action :nothing
end.run_action(:enable)

chef_handler 'CoreMedia::UpdatedResources' do
  source "#{node['chef_handler']['handler_path']}/updated_resources_report.rb"
  action :nothing
end.run_action(:enable)

chef_handler 'Coremedia::CookbookVersions' do
  source "#{node['chef_handler']['handler_path']}/cookbook_versions.rb"
  action :nothing
end.run_action(:enable)

if %w(kitchen development).include?(node.chef_environment) # ~FC023: ignored here because compile time decision necessary
  chef_handler 'Coremedia::DumpNode' do
    source "#{node['chef_handler']['handler_path']}/dump_node.rb"
    action :nothing
  end.run_action(:enable)
end
