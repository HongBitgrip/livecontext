Webstart and signing
====================

In order to use the deployment rule set introduced with ```JRE 1.7.0_u51```, you can create a rule set to enforce webstart
security. To do so, you need to define the rules in the ```src/main/ruleset/ruleset.xml```. After the build ran, there is an
additional jar below the target directory called ```DeploymentRuleSet.jar```. This jar needs to be signed with an official
certificate before it can be deployed to a workstation:

* On Windows platforms, install the file in the ```<Windows-directory>\Sun\Java\Deployment``` directory, for example, ```c:\Windows\Sun\Java\Deployment```.
* On Solaris or Linux platforms, install the file in the ```/etc/.java/deployment``` directory.
* On Mac OS X platforms, install the file in the ```/Library/Application\ Support/Oracle/Java/Deployment/``` directory.

Further documentation:
----------------------

* To get started with deployment rulesets, see the official [introduction](https://blogs.oracle.com/java-platform-group/entry/introducing_deployment_rule_sets)
* To see how to configure this deployment ruleset, see the official [documentation](http://docs.oracle.com/javase/8/docs/technotes/guides/deploy/deployment_rules.html)
