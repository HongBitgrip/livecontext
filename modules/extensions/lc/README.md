CoreMedia Livecontext
=====================

This module contains the CoreMedia Livecontext extension.


Tests
-----

Some Tests require a REST interface which provides required data (e.g. from an eCommerce server). For tests this REST
interface is mocked by the [Betamax] framework which is able to record and replay data from a web resource to YAML files.
To do so [Betamax] starts a proxy server which intercepts outgoing network communication. For each request Betamax checks
if this request and its response have already been recorded on a tape. In this case the recorded answer will be returned
to the client else the request will be forwarded to the recipient and the answer will be recorded for future use.

The default mode of [Betamax] is` READ_WRITE` which beans that non recorded requests will be forwarded to the original
server and the response will be recorded to the appropriate tape. Recorded requests will be replayed directly from the
tape.

The [Betamax] Recorder object is responsible for recording and replaying. It has to be added to each test class using
jUnit rules. It is recommended to pass the system properties to the Recorder in order to be able to configure Betamax
from the command line. For configuration options please consider the Betamax documentation.

For unit tests it is also recommended to set the recording mode to `READ_ONLY` which will never forward not recorded
requests to the designated server but will return a HTTP 403. To record new tapes the property `etamax.deafultMode` has
to be set to `READ_WRITE` in order to enable recording new requests and responses to tapes. All the properties can be set
as Java properties at the command line.


[Betamax]: <http://freeside.co/betamax/> "Betamax"

###Documentation

See the "CoreMedia Blueprint - Functionality for Websites" chapter of the CoreMedia Developer Manual.