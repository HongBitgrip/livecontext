=begin
#<
This resource encapsulates some repository actions often used in test systems. Do not use this resource in production, it
is not a resource as chef intended to. It does not care about idempotency.

You should also be aware, that the publish actions always publish the whole repository. There is no way to publish only changed
resources.

@action import_content Imports all content from the given `content_dir` parameter or the path given as the resource name.
@action publishall_content Publishes all content from the content management server repository using the `publishall` server tool. This action only works with an empty master live server repository. This action consumes considerably less memory than the `bulkpublish_content` action.
@action bulkpublish_content Publishes all content from the content management server repository using the `bulkpublish` server tool.
@action import_users Imports all users from the given array of user files filenames.
@action upload_workflows Upload all workflows given by the arguments `builtin_workflows` and `custom_workflows`

@section Content User Directory Layoyt

This resource expects a content_dir directory to have the following layout

```
|- content
|     |- some-content.xml
|     `- some-blob.jpg
`- users
     `- some-users.xml
```

The content below `content` may be structured arbitrary but must be a valid serverexport dump. User files should be placed
directly below the `users` directory.

@section Guards

All actions are guarded so content is only imported once. The guards are achieved by using marker files.

* If content has already been imported, a marker file `CONTENT_IMPORTED` is placed at the root of the content dir.
* If content has been published, a marker file `CONTENT_PUBLISHED` is placed at the root of the content dir.
* If builtin workflows have been uploaded, a marker file `BUILTIN_WORKFLOWS_UPLOADED` is placed at the root of the content dir. If this resource is used multiple times, this may lead to multiple imports so take care using the upload actions.
* If custom workflows have been uploaded, a marker file `<workflow-defintion file>_WORKFLOW_UPLOADED` is being placed aside the workflow defintion file.
* If a users file has been imported, a marker file `<users file>_USERS_IMPORTED` is being placed beside the users file.

@section Examples

```ruby
blueprint_dev_tooling_content '/some/path/to/a/content/dir' do
 builtin_workflows ['two-step-publication.xml']
 custom_workflows ['/some/path/to/a/workflow/definition.xml']
 action [:import_content, :import_user]
end
```

#>
=end

actions :import_content, :publishall_content, :bulkpublish_content, :import_users, :upload_workflows
default_action :nothing

# <> @attribute content_dir The directory containing the content. See above description about the expected layout.
attribute :content_dir, kind_of: String, name_attribute: true
# <> @attribute serverimport_extra_args An array of extra args passed to the contentimport call.
attribute :serverimport_extra_args, kind_of: Array, default: []
# <> @attribute builtin_workflows An array of workflow names from the builtin workflows to upload.
attribute :builtin_workflows, kind_of: Array, default: []
# <> @attribute custom_workflows An array of paths to custom workflow definitions to upload.
attribute :custom_workflows, kind_of: Array, default: []
# <> @attribute cms_ior The ior of the content management server. Only necessary if publishall_content is being used.
attribute :cms_ior, kind_of: String, default: 'http://localhost:40180/coremedia/ior'
# <> @attribute cms_tools_dir The path of the directory, where the server tools of the content management server are installed.
attribute :cms_tools_dir, kind_of: String, default: lazy { node['blueprint']['tools']['content-management-server']['dir'] }
# <> @attribute mls_ior The ior of the master live server. Only necessary if publishall_content is being used.
attribute :mls_ior, kind_of: String, default: 'http://localhost:40280/coremedia/ior'
# <> @attribute mls_tools_dir The path of the directory, where the server tools of the master live server are installed.
attribute :mls_tools_dir, kind_of: String, default: lazy { node['blueprint']['tools']['master-live-server']['dir'] }
# <> @attribute wfs_tools_dir The path of the directory, where the server tools of the workflow server are installed.
attribute :wfs_tools_dir, kind_of: String, default: lazy { node['blueprint']['tools']['workflow-server']['dir'] }
# <> @attribute user The username to use for execute and filesystem resources.
attribute :user, kind_of: String, default: lazy { node['blueprint']['user'] }
# <> @attribute group The group to use for execute and filesystem resources.
attribute :group, kind_of: String, default: lazy { node['blueprint']['group'] }
# <> @attribute timeout The timeout for all execute resource blocks.
attribute :timeout, kind_of: Integer, default: 2_400
# <> @attribute publishall_contentquery The contentquery to determine which content should not be published. Only effective on publishall_content action.
attribute :publishall_contentquery, kind_of: String, default: 'NOT BELOW PATH \'/Home\''
# <> @attribute publishall_threads The number of concurrent threads for replicating content
attribute :publishall_threads, kind_of: Integer, default: 1
