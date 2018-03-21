# Generator for Comments, Users, Complaints, Likes, Ratings and Blacklist entries

## Create Run Configuration in IDEA

1. Open in IDEA the `Maven Projects` View
2. navigate to `es-demodata-generator-webapp` > `Plugins` > `tomcat7`
3. right-click on `tomcat7:run` and
4. select `Create 'es-demodata-generato...'...`

Open the created run configuration and set the profiles: `test-system.coremedia`
This profile should fit the MongoDB server address, if you have a profile for this.

```
mvn tomcat7:run
```

```
<profile>
  <id>test-system.coremedia</id>
    <properties>
      <installation.host>test-system.coremedia.vm</installation.host>
      <wcs.host>test-system-wcs.coremedia.vm</wcs.host>
      <db.host>test-system.coremedia.vm</db.host>
      <mongoDb.host>test-system.coremedia.vm</mongoDb.host>             
      <skipZip>true</skipZip>
      <skipRpm>true</skipRpm>
    </properties>
</profile>
```

## Modify es-common-defaults.properties

1. Adapt Mongo DB: Replace *localhost* in *mongoDb.serverAddresses* with *mongo.db.host* from your Maven profile
2. Rebuild this module

## Start the demodata generator webapp with:

Via the previously create run configuration or via `mvn tomcat7:run`.

Within the console you should see something like: `Running war on http://localhost:43080/demodata-generator`.
This is the url prefix you have to use to generate data.

### Generate comments, user, complaints, likes, ratings and Blacklist entries

Call the following url

```
http://localhost:43080/demodata-generator/servlet/generate?start&interval=1
```

The following parameters are available:

`interval` (optional): Defines frequency of data generation in seconds. Default interval is 30 seconds. Can be applied
only on startup or after restart.

`stop` (optional): Stops the demodata generator for the given tenant.

`tenant` (optional): Starts the demodata generator for the given tenant. 
The tenant will be registered, if unknown. Default `tenant` is `helios`.

## Studio configuration

Select **Aurora Augmentation** as _Preferred Site_!

