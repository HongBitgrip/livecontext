# CoreMedia Blueprint Extensions

## External Library Extension

### Description

Adds the external library functionality in Studio. The external library is a Studio utility that supports to 
view external content, for example from an RSS feed, in Studio and create CMS content from the external content.

### Documentation

Depending on the type, each external library provider configuration must provide different attributes in the
settings document 'ExternalLibraries'. The examples below show an example of how to configure an RSS or YouTube provider.

#### RSS

```
<Struct xmlns="http://www.coremedia.com/2008/struct" xmlns:xlink="http://www.w3.org/1999/xlink">
  <StructListProperty Name="externalLibraries">
    <Struct>
      <IntProperty Name="index">1</IntProperty>  //position inside the external library combobox
      <StringProperty Name="name">RSS Feed - CNN.com - Top Stories</StringProperty> //name of the provider inside the external library combobox 
      <StringProperty Name="dataUrl">http://rss.cnn.com/rss/edition.rss</StringProperty> //the RSS URL
      <StringProperty Name="providerId">rssProvider</StringProperty>  //fix provider id for RSS feeds
      <StringProperty Name="previewType">html</StringProperty> 
      <StringProperty Name="contentType">CMArticle</StringProperty>  //the document type to create for feed entries
      <BooleanProperty Name="markAsRead">true</BooleanProperty>
    </Struct>
    ... //additional provider configurations
  </StructListProperty>
</Struct>
```

#### YouTube

To enable the YouTube preview inside the Studio ensure that the 'studio.security.csp.frameSrc' allows
YouTube URLs to be shown as frame sources.

```
<Struct xmlns="http://www.coremedia.com/2008/struct" xmlns:xlink="http://www.w3.org/1999/xlink">
  <StructListProperty Name="externalLibraries">
    <Struct>
      <IntProperty Name="index">1</IntProperty> //position inside the external library combobox
      <StringProperty Name="name">YouTube - Your Channel Name</StringProperty> //name of the provider inside the external library combobox 
      <StringProperty Name="credentialsJson">[the credentials JSON to access the channel]</StringProperty>
      <StringProperty Name="channel.id">[the YouTube channel id]</StringProperty>
      <StringProperty Name="providerId">youTubeVideoProvider</StringProperty> //fix provider id for YouTube
      <StringProperty Name="previewType">video</StringProperty> //
      <StringProperty Name="contentType">CMVideo</StringProperty> //the document type to create for videos
      <BooleanProperty Name="markAsRead">true</BooleanProperty>
    </Struct>
    ... //additional provider configurations
  </StructListProperty>
</Struct>
```