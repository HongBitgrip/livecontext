name 'blueprint-mongodb'
maintainer 'Coremedia'
maintainer_email 'bodo.schulz@coremedia.com'
license 'Copyright (C) 2016, CoreMedia AG proprietary License, all rights reserved.'
description 'Installs/Configures blueprint-mongodb'
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version '1.0.0'

depends 'ulimit', '~> 0.3.0'
depends 'mongodb3', '~> 5.3.0'
