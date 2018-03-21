=begin
#<

Installs a webapp and optionally adds a `META-INF/context.xml`. Whether the maven war artifact will be downloaded
or downloaded, exploded and enriched with the context file, depends upon the path attribute. If it does not end on
`.war` the artifact will be exploded if not it will just be downloaded. The exploded dir and the archive will be placed side
by side.

Beside the `install` action there is a second action called update. This is just a helper action to set the `updated_by_last_action`
state of this resource to `true`. The idea of this concept, is that the `coremedia_tomcat_service_lifecycle` definition, then
can depend on this resources state to decide, whether the service needs to be stopped and the `doc_base` of this war should be
updated. Because of this, you should not choose the tomcats `app_base` directory as the target directory for the `install` action.

To resolve the latest X-SNAPSHOT, RELEASE or LATEST version, you need to set `nexus_url` and `nexus_repo` attributes.

@action install Install the webapp in a directory which is not the `app_base` directory of tomcat.
@action update Sets the `updated_by_last_action` flag of this resource to `true`.

@section Examples

```ruby
coremedia_tomcat_webapp "/some/path/my-app do
  group_id 'org.myorg'
  artifact_id 'my-app'
  version '1.0.0'
  context 'myapp'
  context_config(
    :display_name => 'myapp',
    :env_entries => {
      'toggle' => {
        'description' => 'this does nothing',
        'value' => 'false',
        'type'  => 'java.lang.Boolean'
      }
    },
   :parameter => {
     'key1' => 'value1',
     'key2' => 'value2'
   },
   :resource_links => {
     'resourceLinkName' => {
        'description' => 'does nothing',
        'value' => 'resourceLinkValue',
        # defaults to java.lang.String if not set
        'type' => 'java.lang.String'
     }
   }
 )
  owner 'tomcat'
  group 'tomcat'
end
```
#>
=end
actions :install, :update
default_action :install

attr_accessor :path, :context

#<> @attribute path The path to the webapp, can be a file or directory path. In the latter case, the war gets exploded.
attribute :path, :kind_of => String, :required => true
#<> @attribute group_id Maven groupId.
attribute :group_id, :kind_of => String, :required => true
#<> @attribute artifact_id Maven artifactId.
attribute :artifact_id, :kind_of => String, :required => true
#<> @attribute version Maven version.
attribute :version, :kind_of => String, :required => true
#<> @attribute classifier Maven classifier, defaults to not set.
attribute :classifier, :kind_of => String, :default => nil
#<> @attribute checksum The SHA-256 checksum of the artifact.
attribute :checksum, :kind_of => String, :default => nil
#<> @attribute maven_repository_url The Url of the maven repository, supports all protocols of the remote_file resource. Defaults to Maven Central.
attribute :maven_repository_url, :kind_of => String, :default => 'http://repo1.maven.org/maven2/'
#<> @attribute nexus_url Set this to the base url of your nexus to use the REST API for artifact resolution. If set this method has precedence to the repository_url attribute.
attribute :nexus_url, :kind_of => String, :default => nil
#<> @attribute nexus_repo The repo name from which to resolve artifacts.
attribute :nexus_repo, :kind_of => String, :default => 'releases'
#<> @attribute nexus_username The user to access a protected nexus.
attribute :nexus_username, :kind_of => String, :default => nil
#<> @attribute nexus_password The password to access a protected nexus.
attribute :nexus_password, :kind_of => String, :default => nil
#<> @attribute owner User ownership (linux only), defaults to "root".
attribute :owner, :kind_of => String, :default => 'root'
#<> @attribute group Group membership (linux only), defaults to "root".
attribute :group, :kind_of => String, :default => 'root'
#<> @attribute context The context name under which to deploy the webapp, defaults to the name of the resource
attribute :context, :kind_of => String, :name_attribute => true
#<> @attribute context_template The template name from which to create the context_file (exploded mode only), set to false to skip the creation.
attribute :context_template, :kind_of => [FalseClass, String], :default => lazy { |r| r.path.end_with?('.war') ? false : 'context.xml.erb' }
#<> @attribute context_template_cookbook The cookbook, from which to load the context_template.
attribute :context_template_cookbook, :kind_of => String, :default => 'coremedia_tomcat'
#<> @attribute context_config A hash to pass to the context_template. See [context section](#context-files) for the possible configuration keys in the default template.
attribute :context_config, :kind_of => Hash, :default => {}
