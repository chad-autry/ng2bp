var ngCore = require('angular2/core'),
    ngBrowser = require('angular2/platform/browser'),
    ngRouter = require('angular2/router'),
    satellizer = require('ng2-ui-auth'),
    userManagementHtml = require('./userManagement.html');

module.exports = ngCore
    .Component({
        selector: 'app-userManagement',
        template: userManagementHtml
     })
    .Class({
        constructor: [ngBrowser.Title, satellizer.Auth, ngRouter.Router, function( title, auth, router) {
            //Save the titleService
            this.title = title;
            
            //save the auth servoce
            this.auth = auth;
            
            this.router = router;
        }],
        routerOnActivate: function(nextInstruction, prevInstruction) {
            this.title.setTitle("ng2bp | User");
        }
    });
