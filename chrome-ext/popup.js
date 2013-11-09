var connectToDevice = function(result) {
  if (chrome.runtime.lastError) {
    console.log('Error searching for a device to connect to.');
    return;
  }
  if (result.length == 0) {
    console.log('No devices found to connect to.');
    return;
  }
  for (var i in result) {
    var device = result[i];
    log('Connecting to device: ' + device.name + ' @ ' + device.address);
    chrome.bluetooth.connect(
        {deviceAddress: device.address, serviceUuid: kUUID}, connectCallback);
    }
};

var connectCallback = function(socket) {
  if (socket) {
    console.log('Connected!  Socket ID is: ' + socket.id + ' on service ' +
        socket.serviceUuid);
  } else {
    console.log('Failed to connect.');
  }
};

var devicesAvailable = function(devices) {
  console.log(devices.name);
}

console.log('Starting device...');
chrome.bluetooth.startDiscovery({deviceCallback:devicesAvailable}, (function(){console.log("end")})());
// chrome.bluetooth.getDevices({uuid: kUUID}, connectToDevice);

