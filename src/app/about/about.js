var ngCore = require('angular2/core');

module.exports = ngCore
    .Component({
        selector: 'app-about',
        template: `
        <div class="container">
<div class="jumbotron">
  <h1>Going to put some more routing examples on this page</h1>
</div>
        </div>`
     })
    .Class({
        constructor: function() {}
    });
