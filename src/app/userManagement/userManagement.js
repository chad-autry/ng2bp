var ngCore = require('angular2/core'),
    ngBrowser = require('angular2/platform/browser'),
    ngRouter = require('angular2/router'),
    satellizer = require('ng2-ui-auth');

module.exports = ngCore
    .Component({
        selector: 'app-userManagement',
        template: `
        <div class="container">
<div class="jumbotron" (click)="auth.logout(); router.navigate(['Login']);">
  <h1 class="text-center">Click me to log out</h1>
</div>
</div>`
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
