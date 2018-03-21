=begin
#<

Downloads a Maven artifact from a local or remote Maven repository.

If attribute `nexus_url` and `nexus_repo` is set, the artifact will be retrieved using the rest API of nexus and the version
of the artifact may be set to `RELEASE`, `LATEST` or `X-SNAPSHOT` and the versions will be resolved accordingly.

@action install Install an artifact

@section Examples

```ruby
coremedia_maven '/var/tmp/solr.war' do
  group_id 'org.apache.solr'
  artifact_id 'solr'
  version '4.5.0'
  owner 'coremedia'
  group 'coremedia'
  mode 0644
  extract_to '/usr/local/tomcat/webapps/solr'
  notify :restart, 'tomcat[solr]', :immediately
end
```
#>
=end

actions :install
default_action :install

attr_accessor :path, :group_id, :artifact_id, :version, :packaging, :classifier, :repository_url, :extract_to, :owner, :group

#<> @attribute path The path to download the artifact to. Defaults to the name of the resource.
attribute :path, :kind_of => String, :name_attribute => true
#<> @attribute group_id Maven groupId.
attribute :group_id, :kind_of => String, :required => true
#<> @attribute artifact_id Maven artifactId.
attribute :artifact_id, :kind_of => String, :required => true
#<> @attribute version Maven version.
attribute :version, :kind_of => String, :required => true
#<> @attribute packaging Maven packaging type, defaults to the file extension of the path attribute.
attribute :packaging, :kind_of => String, :default => lazy { |r| ::File.extname(r.path)[1..-1] }
#<> @attribute classifier Maven classifier, defaults to not set.
attribute :classifier, :kind_of => String, :default => nil
#<> @attribute checksum The SHA-256 checksum of the artifact
attribute :checksum, :kind_of => String, :default => nil
#<> @attribute repository_url The Url of the maven repository, supports all protocols of the remote_file resource. Defaults to Maven Central.
attribute :repository_url, :kind_of => String, :default => 'http://repo1.maven.org/maven2/'
#<> @attribute username The username for the Maven repository, defaults to not set.
attribute :username, :kind_of => String, :default => nil
#<> @attribute password The password for the Maven repository, defaults to not set.
attribute :password, :kind_of => String, :default => nil
#<> @attribute owner User ownership (linux only), defaults to "root".
attribute :owner, :kind_of => String, :default => 'root'
#<> @attribute group Group membership (linux only), defaults to "root".
attribute :group, :kind_of => String, :default => 'root'
#<> @attribute mode File permissions, defaults to "00755".
attribute :mode, :kind_of => String, :default => 00755
#<> @attribute extract_to Extract to folder path if downloaded artifact has changed. Extraction is skipped if no folder path is set.
attribute :extract_to, :kind_of => String, :default => nil
#<> @attribute extract_force_clean Set this to true to force deletion of target folder upfront.
attribute :extract_force_clean, :kind_of => [TrueClass, FalseClass], :default => false
#<> @attribute backup Number of version to backup in the Chef cache. This works only with non file:// urls. Set to false to disable backup.
attribute :backup, :kind_of => [Integer, FalseClass], :default => false
#<> @attribute nexus_url Set this to the base url of your nexus to use the REST API for artifact resolution. If set this method has precedence to the repository_url attribute.
attribute :nexus_url, :kind_of => String, :default => nil
#<> @attribute nexus_repo The repo name from which to resolve artifacts.
attribute :nexus_repo, :kind_of => String, :default => 'releases'
