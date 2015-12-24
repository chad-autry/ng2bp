var ngCore = require('angular2/core'),
    ngBrowser = require('angular2/platform/browser');

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
        constructor: [ngBrowser.Title, function( title) {
            //Save the titleService
            this.title = title;
        }],
        routerOnActivate: function(nextInstruction, prevInstruction) {
            this.title.setTitle("ng2bp | Demo");
        }
    });
