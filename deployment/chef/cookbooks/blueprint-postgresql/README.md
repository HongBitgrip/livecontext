# Description

This is the wrapper cookbook to install postgresql.



# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* blueprint-yum
* yum-pgdg (~> 2.0.1)
* postgresql (~> 6.1.1)

# Attributes

* `node['blueprint']['postgresql']['schemas']` - An array of schemas create when using the schema recipe. Defaults to `[ ... ]`.

# Recipes

* [blueprint-postgresql::default](#blueprint-postgresqldefault) - This recipe installs and configures postgresql and creates schemas for CoreMedia Blueprint.
* [blueprint-postgresql::schemas](#blueprint-postgresqlschemas) - This recipe installs and configures postgresql client and creates schemas.
* [blueprint-postgresql::server](#blueprint-postgresqlserver) - This recipe installs and configures postgresql.

## blueprint-postgresql::default

This recipe installs and configures postgresql and creates schemas for CoreMedia Blueprint.

## blueprint-postgresql::schemas

This recipe installs and configures postgresql client and creates schemas.

## blueprint-postgresql::server

This recipe installs and configures postgresql.

# Author

Author:: Your Name (<your_name@domain.com>)
