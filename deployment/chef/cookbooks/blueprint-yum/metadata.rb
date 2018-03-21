name 'blueprint-yum'
maintainer ''
maintainer_email ''
license 'all_rights'
description 'Configures yum repositories'
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version '1.0.0'
depends 'yum', '~> 4.2'
depends 'yum-centos', '~> 2.3.0'

supports 'redhat'
supports 'centos'
supports 'amazon'
