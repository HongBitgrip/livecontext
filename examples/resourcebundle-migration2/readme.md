# Switch from CMSettings to CMResourceBundles

In the context of the CMTheme document type we also introduced CMResourceBundle,
and consequently we switched CMLinkable#resourceBundles from CMSettings to
CMResourceBundle.  This tool helps you with migrating existing content
repositories accordingly.

The resourcebundle-migration2 tool is a standard CMS command line client to be
executed with cm (or cm64.exe, respectively).  It should be run as admin in
order to avoid permission problems.  It features a -s (--simulate) option to
log what would be done only, without actually changing any content.

Before applying the migration on a productive repository, make sure to have
a backup, and ideally try it on a test repository first. 

As a precondition, you must add the new resourceBundles2 property to the
CMLinkable document type and restart the content servers.

The tool is designed only for small sets of content.  Since you will have
resource bundles mainly at root channels, it should suffice for typical 
productive repositories nonetheless.  Otherwise you should rework
ResourceBundleMigration2#fetchContentsWithBundles and run the migration for
smaller chunks of content.

While the actual migration should work flawlessly in any case, we cannot
guarantee for the publication and cleanup functionality.  It is a simple
implementation which assumes that the old resource bundles are linked
nowhere else.
