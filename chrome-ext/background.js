//start polling once we loaded the page
chrome.tabs.onUpdated.addListener(function(tabId, changeInfo) {
  if (changeInfo.status === 'complete') {
      chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
        chrome.tabs.sendMessage(tabs[0].id, {url: tabs[0].url});
      });
  }
});

chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
    if (request.hostname && request.data) {
      console.log(request);

      switch (request.hostname) {
        case "dropbox":
          chrome.tabs.executeScript({
            code:
              '$("input#code").val("' + request.data + '");$("#twofactor-confirm").submit();'
          });
          break;
        case "github":
          chrome.tabs.executeScript({
            code:
              '$(\'input[name="otp"]\').val("' + request.data + '");$(\'button[type="submit"]\').submit();'
          });
          break;
        case "tumblr":
          chrome.tabs.executeScript({
            code:
              'document.getElementById("tfa_response_field").value = "' + request.data + '";$("#signup_form").submit();'
          });
        default:
          console.log("break");
          break;
      }
    }
  }
);