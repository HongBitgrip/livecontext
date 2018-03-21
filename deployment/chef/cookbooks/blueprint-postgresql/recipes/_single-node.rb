template_file = '/etc/sysconfig/pgsql/postgresql-9.3'
if !::File.exist?(template_file) && node['postgresql']['server']['config_change_notify'] == :nothing
  run_context.delayed_notification_collection.delete("template[#{template_file}]")
  log 'restart postgres' do
    notifies :restart, 'service[postgresql]', :immediately
  end
end
