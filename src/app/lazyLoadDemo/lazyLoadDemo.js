var ngCore = require('angular2/core'),
    ngBrowser = require('angular2/platform/browser'),
    lazyLoadDemoHtml = require('./lazyLoadDemo.html');

module.exports = ngCore
    .Component({
        selector: 'app-about',
        template: lazyLoadDemoHtml
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
