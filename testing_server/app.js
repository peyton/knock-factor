var express = require('express');
var app = express();

app.get('/knocked', function(req, res){
  res.set({"Access-Control-Allow-Origin": "*"});
  res.send('yes');
});

app.get('/request', function(req, res) {
  res.set({"Access-Control-Allow-Origin": "*"});
  res.send(req.query.hostname);
});

app.get('/passstring', function(req, res) {
  res.set({"Access-Control-Allow-Origin": "*"});
  res.send("6845468");
});

app.listen(3000);
console.log("listening on port 3000");
