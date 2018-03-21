use_inline_resources
require 'digest'
action :install do
  webapp_artifact = coremedia_maven artifact_path do
    group_id new_resource.group_id
    artifact_id new_resource.artifact_id
    version new_resource.version
    classifier new_resource.classifier unless new_resource.classifier.nil?
    packaging 'war'
    checksum new_resource.checksum unless new_resource.checksum.nil?
    repository_url new_resource.maven_repository_url
    nexus_url new_resource.nexus_url unless new_resource.nexus_url.nil?
    nexus_repo new_resource.nexus_repo
    username new_resource.nexus_username unless new_resource.nexus_username.nil?
    password new_resource.nexus_password unless new_resource.nexus_password.nil?
    group new_resource.group
    owner new_resource.owner
    backup 1
    extract_to new_resource.path if explode?
    extract_force_clean true
  end

  log "test checksum of #{artifact_path}" do
    action :nothing
    level :warn
    message lazy { "Missing checksum on resource coremedia_tomcat_webapp[#{new_resource.name}]. When deploying explicit versions, you should use a checksum. For #{new_resource.version} it is currently #{::Digest::SHA256.file(artifact_path).hexdigest}" }
    subscribes :write, webapp_artifact, :immediately unless new_resource.checksum.nil? || new_resource.version =~ /(RELEASE|SNAPSHOT|LATEST)/
  end

  if explode? && new_resource.context_template

    meta_inf_dir = directory "#{new_resource.path}/META-INF" do
      group new_resource.group
      owner new_resource.owner
    end

    template "#{meta_inf_dir.path}/context.xml" do
      source new_resource.context_template
      cookbook new_resource.context_template_cookbook
      mode 0644
      group new_resource.group
      owner new_resource.owner
      variables new_resource.context_config.nil? ? {} : new_resource.context_config
      only_if { explode? }
    end
  end
end

action :update do
  # this is just a marker action for resources that need to tell chef that the exploded war has changed
  new_resource.updated_by_last_action(true) if explode?
end

def explode?
  !new_resource.path.end_with?('.war')
end

def artifact_path
  explode? ? "#{new_resource.path}.war" : new_resource.path
end
