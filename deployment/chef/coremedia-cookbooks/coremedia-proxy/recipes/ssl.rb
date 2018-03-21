=begin
#<
This recipe installs Apache HTTPD and installs modules as well as configuring global settings. It also configures mod_ssl.
#>
=end
include_recipe 'coremedia-proxy::default'

#mod_ssl attributes
node.default['apache']['mod_ssl']['cipher_suite'] = 'ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-AES256-GCM-SHA384:DHE-RSA-AES128-GCM-SHA256:DHE-DSS-AES128-GCM-SHA256:kEDH+AESGCM:ECDHE-RSA-AES128-SHA256:ECDHE-ECDSA-AES128-SHA256:ECDHE-RSA-AES128-SHA:ECDHE-ECDSA-AES128-SHA:ECDHE-RSA-AES256-SHA384:ECDHE-ECDSA-AES256-SHA384:ECDHE-RSA-AES256-SHA:ECDHE-ECDSA-AES256-SHA:DHE-RSA-AES128-SHA256:DHE-RSA-AES128-SHA:DHE-DSS-AES128-SHA256:DHE-RSA-AES256-SHA256:DHE-DSS-AES256-SHA:DHE-RSA-AES256-SHA:AES128-GCM-SHA256:AES256-GCM-SHA384:AES128-SHA256:AES256-SHA256:AES128-SHA:AES256-SHA:AES:CAMELLIA:DES-CBC3-SHA:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!PSK:!aECDH:!EDH-DSS-DES-CBC3-SHA:!EDH-RSA-DES-CBC3-SHA:!KRB5-DES-CBC3-SHA'
node.default['apache']['mod_ssl']['honor_cipher_order'] = 'On'
node.default['apache']['mod_ssl']['protocol'] = 'all -SSLv2 -SSLv3'
node.default['apache']['mod_ssl']['directives']['SSLCertificateFile'] = '/etc/pki/tls/certs/localhost.crt'
#node.default['apache']['mod_ssl']['directives']['SSLCertificateChainFile'] =
node.default['apache']['mod_ssl']['directives']['SSLCertificateKeyFile'] = '/etc/pki/tls/private/localhost.key'
node.default['apache']['mod_ssl']['directives']['Header'] = 'always add Strict-Transport-Security "max-age=15768000"'

include_recipe 'apache2::mod_ssl'
