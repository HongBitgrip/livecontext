name 'blueprint-proxy'
maintainer 'Felix Simmendinger'
maintainer_email 'felix.simmendinger@coremedia.com'
license 'Copyright (C) 2015, CoreMedia AG proprietary License, all rights reserved.'
description 'Installs and configures a webserver proxy'
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version '1.0.0'

depends 'blueprint-base'
depends 'blueprint-tomcat'
depends 'coremedia-proxy', '~> 0.3.2'
depends 'blueprint-tomcat'
