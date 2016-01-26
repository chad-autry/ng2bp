var ngCore = require('angular2/core'),
    ngBrowser = require('angular2/platform/browser'),
    homeHtml = require('./home.html');

module.exports = ngCore
    .Component({
        selector: 'app-home',
        template: homeHtml
     })
    .Class({
        constructor: [ngBrowser.Title, function( title) {
            //Save the titleService
            this.title = title;
        }],
        routerOnActivate: function(nextInstruction, prevInstruction) {
            this.title.setTitle("ng2bp | Home");
        }
    });
