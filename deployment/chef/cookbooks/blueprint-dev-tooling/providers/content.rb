use_inline_resources

CONTENT_IMPORTED = 'CONTENT_IMPORTED'.freeze
CONTENT_PUBLISHED = 'CONTENT_PUBLISHED'.freeze
BUILTIN_WORKFLOWS_UPLOADED = 'BUILTIN_WORKFLOWS_UPLOADED'.freeze
CUSTOM_WORKFLOW_UPLOADED = 'WORKFLOW_UPLOADED'.freeze
USERS_IMPORTED = 'USERS_IMPORTED'.freeze

action :import_content do
  raise "Cannot import content, content dir does not exist at #{new_resource.content_dir}" unless ::File.exist?(new_resource.content_dir)
  raise "Cannot import content, Content Management Server Tools not found at #{new_resource.cms_tools_dir}" unless ::File.exist?("#{new_resource.cms_tools_dir}/bin/serverimport.jpif")
  import_marker_file = "#{new_resource.content_dir}/#{CONTENT_IMPORTED}"
  workaround_log "CONTENT ALREADY IMPORTED, DELETE #{new_resource.content_dir} TO REIMPORT" if ::File.exist?(import_marker_file)

  content_import = execute "import content from #{new_resource.content_dir}" do
    command "#{new_resource.cms_tools_dir}/bin/cm serverimport -r -u admin -p admin --no-validate-xml #{new_resource.serverimport_extra_args.join(' ')} #{new_resource.content_dir}/content"
    timeout new_resource.timeout
    user new_resource.user
    group new_resource.group
    not_if { ::File.exist?(import_marker_file) }
  end

  file import_marker_file do
    owner new_resource.user
    group new_resource.group
    action :nothing
    subscribes :create, content_import, :immediately
  end
end

action :publishall_content do
  raise "Cannot publish content, content dir does not exist at #{new_resource.content_dir}" unless ::File.exist?(new_resource.content_dir)
  raise "Cannot publish content, Content Management Server Tools not found at #{new_resource.cms_tools_dir}" unless ::File.exist?("#{new_resource.cms_tools_dir}/bin/publishall.jpif")
  raise "Cannot publish content, Content Management Server Tools not found at #{new_resource.cms_tools_dir}" unless ::File.exist?("#{new_resource.cms_tools_dir}/bin/bulkpublish.jpif")
  publish_marker_file = "#{new_resource.content_dir}/#{CONTENT_PUBLISHED}"
  workaround_log 'CONTENT ALREADY PUBLISHED, DROP MASTER DB TO REPUBLISH' if ::File.exist?(publish_marker_file)

  execute "check in all content from #{new_resource.content_dir}" do
    command "#{new_resource.cms_tools_dir}/bin/cm bulkpublish -u admin -p admin -c"
    timeout new_resource.timeout
    user new_resource.user
    group new_resource.group
    not_if { ::File.exist?(publish_marker_file) }
  end

  content_publish = execute "publish content from #{new_resource.content_dir}" do
    command "#{new_resource.cms_tools_dir}/bin/cm publishall -a -cq \"#{new_resource.publishall_contentquery}\" -t #{new_resource.publishall_threads} #{new_resource.cms_ior} admin admin #{new_resource.mls_ior} admin admin"
    timeout new_resource.timeout
    user new_resource.user
    group new_resource.group
    not_if { ::File.exist?(publish_marker_file) }
  end

  file publish_marker_file do
    owner new_resource.user
    group new_resource.group
    action :nothing
    subscribes :create, content_publish, :immediately
  end
end
  
action :bulkpublish_content do
  raise "Cannot publish content, content dir does not exist at #{new_resource.content_dir}" unless ::File.exist?(new_resource.content_dir)
  raise "Cannot publish content, Content Management Server Tools not found at #{new_resource.cms_tools_dir}" unless ::File.exist?("#{new_resource.cms_tools_dir}/bin/publishall.jpif")
  raise "Cannot publish content, Content Management Server Tools not found at #{new_resource.cms_tools_dir}" unless ::File.exist?("#{new_resource.cms_tools_dir}/bin/bulkpublish.jpif")
  publish_marker_file = "#{new_resource.content_dir}/#{CONTENT_PUBLISHED}"
  workaround_log 'CONTENT ALREADY PUBLISHED, DROP MASTER DB TO REPUBLISH' if ::File.exist?(publish_marker_file)

  content_publish = execute 'publish content' do
    command "#{new_resource.cms_tools_dir}/bin/cm bulkpublish -u admin -p admin -a -b -c"
    timeout new_resource.timeout
    user new_resource.user
    group new_resource.group
    not_if { ::File.exist?(publish_marker_file) }
  end

  file publish_marker_file do
    owner new_resource.user
    group new_resource.group
    action :nothing
    subscribes :create, content_publish, :immediately
  end
end

action :import_users do
  raise "Cannot import users, content dir does not exist at #{new_resource.content_dir}/users" unless ::File.exist?("#{new_resource.content_dir}/users")
  raise "Cannot import users, Content Management Server Tools not found at #{new_resource.cms_tools_dir}" unless ::File.exist?("#{new_resource.cms_tools_dir}/bin/restoreusers.jpif")

  Dir.glob("#{new_resource.content_dir}/users/*.xml") do |user_file|
    user_marker_file = "#{user_file}_#{USERS_IMPORTED}"
    workaround_log "USERS FROM #{user_file} ALREADY IMPORTED, DELETE #{user_file}_#{USERS_IMPORTED} TO REIMPORT" if ::File.exist?(user_marker_file)
    user_import = execute "import users from #{user_file}" do
      command "#{new_resource.cms_tools_dir}/bin/cm restoreusers -u admin -p admin -f #{user_file}"
      timeout new_resource.timeout
      user node['blueprint']['user']
      group node['blueprint']['group']
      not_if { ::File.exist?(user_marker_file) }
    end

    file user_marker_file do
      owner new_resource.user
      group new_resource.group
      action :nothing
      subscribes :create, user_import, :immediately
    end
  end
end

action :upload_workflows do
  raise "Cannot upload workflows, content dir does not exist at #{new_resource.content_dir}" unless ::File.exist?(new_resource.content_dir)
  raise "Cannot upload workflows, Content Management Server Tools not found at #{new_resource.wfs_tools_dir}" unless ::File.exist?("#{new_resource.wfs_tools_dir}/bin/upload.jpif")
  builtin_marker_file = "#{new_resource.content_dir}/#{BUILTIN_WORKFLOWS_UPLOADED}"
  # builtin workflows
  workaround_log "BUILTIN WORKFLOWS ALREADY UPLOADED, DELETE #{new_resource.content_dir}/#{BUILTIN_WORKFLOWS_UPLOADED} TO REUPLOAD" if ::File.exist?(builtin_marker_file)
  workflows_builtin = execute 'import builtin workflows' do
    command "#{new_resource.wfs_tools_dir}/bin/cm upload -u admin -p admin  -n #{new_resource.builtin_workflows.join(' ')}"
    timeout new_resource.timeout
    user new_resource.user
    group new_resource.group
    not_if { ::File.exist?(builtin_marker_file) }
  end

  file builtin_marker_file do
    owner new_resource.user
    group new_resource.group
    subscribes :create, workflows_builtin, :immediately
  end

  # custom workflows
  new_resource.custom_workflows.each do |workflow_definition|
    raise "Cannot upload workflow from #{workflow_definition}, file does not exist" unless ::File.exist?(workflow_definition)
    custom_marker_file = "#{new_resource.content_dir}/#{::File.basename(workflow_definition)}_#{CUSTOM_WORKFLOW_UPLOADED}"
    workaround_log "WORKFLOW FROM #{workflow_definition} ALREADY UPLOADED, DELETE #{workflow_definition}_#{CUSTOM_WORKFLOW_UPLOADED} TO REUPLOAD" if ::File.exist?(custom_marker_file)

    workflow_custom = execute "import workflow from #{workflow_definition}" do
      command "#{new_resource.wfs_tools_dir}/bin/cm upload -u admin -p admin -f #{workflow_definition}"
      timeout new_resource.timeout
      user new_resource.user
      group new_resource.group
      not_if { ::File.exist?(custom_marker_file) }
    end

    file custom_marker_file do
      owner new_resource.user
      group new_resource.group
      subscribes :create, workflow_custom, :immediately
    end
  end
end

def workaround_log(message)
  # this is a workaround to also print the log message using kitchen. By default kitchen log level is warn and cannot be changed, see https://github.com/test-kitchen/test-kitchen/issues/529.
  if Chef::Config[:file_cache_path].include?('kitchen')
    puts message
  else
    log message do
      new_resource.updated_by_last_action(false)
    end
  end
end
