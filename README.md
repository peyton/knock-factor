knock-factor-authentication
===========================

Using the Bluetooth Server
---------------------------

To start the Bluetooth Server, navigate to `CocoaHTTPServer/Samples/DynamicServer` and build `DynamicServer.xcodeproj`. Click the `Connect` button and connect a device.

The Bluetooth Server is then available at `http://localhost:12345`

### For Android

1. Send the UTF-8 string "knocked" when a knock is detected.
2. The host will send a UTF-8 encoded hostname, e.g. "www.dropbox.com"
3. Send the UTF-8 encoded passcode string.


### For Chrome

1. Poll `/knocked`. The response will be `no` if a knock has not been detected and `yes` if a knock has been detected.
2. Send a GET request to `/request` with the parameter `hostname` set to the hostname of the current site. E.g. `GET localhost:12345/request?hostname=www.dropbox.com`
3. Poll `/passstring`. The response will initially be "nothing yet". When the device responds with a passtring, the response will change to the passstring.
