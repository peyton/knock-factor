 /**
  * @type {string}
  */

var twoFactor = {
  /**
   * Flickr URL that will give us lots and lots of whatever we're looking for.
   *
   * See http://www.flickr.com/services/api/flickr.photos.search.html for
   * details about the construction of this URL.
   *
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
  getHostname: function() {
    var that = this;
    chrome.tabs.getSelected(null, function(tab) {
      var link = document.createElement('a');
      var url_full = tab.url;
      link.href = tab.url;
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
      that.pollKnocked(host_f);
    });
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
          switch (hostname) {
            case "dropbox":
              console.log("correct");
              chrome.tabs.executeScript({
                code:
                  '$("input#code").val("' + data.trim() + '");$("#twofactor-confirm").submit();'
              });
              break;
            case "github":
              console.log("correct");
              chrome.tabs.executeScript({
                code:
                  '$("input[name="otp"]).val("' + data.trim() + '");$("button[type="submit"]").submit();'
              });
            default:
              console.log("break");
              break;
          }
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
document.addEventListener('DOMContentLoaded', function () {
  twoFactor.getHostname();
});
