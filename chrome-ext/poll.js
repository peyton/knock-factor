 /**
  * @type {string}
  */

var twoFactor = {
  /**
   * @type {string}
   * @private
   */
  polling_: 'http://localhost:12345/knocked',
  send_headers_: 'http://localhost:12345/request?hostname=',
  pass_string_: 'http://localhost:12345/passstring',
   
  /**
   * Checks to see if the current website is one with two factor-auth
   *
   * @public
   */
  getHostname: function(url) {
    
    var link = document.createElement('a');

    var url_full = url;
    link.href = url;

    console.log(link.hostname);

    var hostname = link.hostname;
    var host_f = '';

    if (hostname.indexOf("dropbox") != -1) {
      host_f = "dropbox"; 
    } else if (hostname.indexOf("github") != -1) {
      host_f = "github";
    } else if (hostname.indexOf("mail.google") != -1) {
      host_f = "mail.google";
    }

    this.pollKnocked(host_f);
  },
 
  /**
   * Send a GET request to /request with the parameter hostname 
   * set to the hostname of the current site. 
   * E.g. GET localhost:12345/request?hostname=www.dropbox.com
   *
   * @public
   */

  sendHostname: function(hostname) {
    var that = this;
    $.get( this.send_headers_ + hostname )
      .done(function( ) {
        that.getResponseCode(hostname);
    });
    
  },

  /**
   * Poll /passstring. The response will initially be "nothing yet". 
   * When the device responds with a passtring, 
   * the response will change to the passstring.
   *
   * @public
   */

  getResponseCode: function(hostname) {
    var that = this;
    $.ajax({ 
      url: this.pass_string_, 
      success: function(data) {
        console.log("data" + data);
        if (data==="nothing yet") {
          setTimeout(function() {
          that.getResponseCode(hostname);
        }, 1000);
        } else {

          chrome.runtime.sendMessage({hostname: hostname, data: data.trim()});

          console.log(data);
        }
      }, 
      dataType: "text"
    });
  },

  /**
   * Poll /knocked. The response will be no if a knock has 
   * not been detected and yes if a knock has been detected.
   *
   * @public
   */
  pollKnocked: function(hostname) {
    console.log("polling knocked");

    var link = hostname;
    var that = this;

    $.ajax({ url: this.polling_, success: function(data){
      if (data==="yes") { 
        that.sendHostname(hostname);
      } else {
        setTimeout(function() {
          that.pollKnocked(hostname);
        }, 1000);
      }
    }, dataType: "text" });
  },
}
  
  //start polling once we loaded the page
chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
    if (request.url) {
      console.log(request.url);

      twoFactor.getHostname(request.url);
    }
  }
);
