# Splitting linked settings and resource bundles

Up to Blueprint release 7.5.40 resource bundles and technical settings have
been mixed up in the linkedSettings property of the sites' root channels.  In
7.5.41 we split off the resource bundles into a dedicated resourceBundle
property.  This tool helps you to migrate the content.

The resourcebundle-migration tool is a standard CMS command line client to be
executed with cm (or cm64.exe, respectively).  It should be run as admin in
order to avoid permission problems.  It features a -s (--simulate) option to
log what would be done only, rather than changing any content.

The tool itself is simple and only suitable if you strictly adhere to the
structure of our example content.  Therefore you should review and eventually
adapt the ResourceBundleMigration code, esp. the methods

* fetchSites
* isResourceBundle

and run the tool in simulation mode first to verify the results to be expected.