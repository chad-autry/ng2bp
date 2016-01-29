var ngCore = require('angular2/core'),
    ngBrowser = require('angular2/platform/browser'),
    ngRouter = require('angular2/router'),
    ngCommon = require('angular2/common'),
    Auth = require('ng2-ui-auth'),
    loginHtml = require('./Login.html');

module.exports = ngCore
    .Component({
        selector: 'login',
        template: loginHtml,
        directives: [ ngRouter.ROUTER_DIRECTIVES]
     })
    .Class({
        constructor: [Auth.Auth, ngCommon.FormBuilder, ngRouter.Router, ngCore.ElementRef, ngCore.Renderer, ngBrowser.Title, function(auth, formBuilder, router, element, renderer, title) {
            this.auth = auth;
            this.router = router;
            this.element = element;
            this.renderer = renderer;
            
            this.user = {};
            this.user.password = '';
            this.user.email ='';
            this.userControlsConfig={
            email: ['', ngCommon.Validators.compose([ngCommon.Validators.required])],
            password: ['', ngCommon.Validators.required],
            };
            this.form = formBuilder.group(this.userControlsConfig);
            this.title = title;
        }],
        routerOnActivate: function(nextInstruction, prevInstruction) {
            this.title.setTitle("ng2bp | Log in");
        },
        form: ngCommon.ControlGroup,
        login: function () {
                   var component = this;
                   this.auth.login(this.user)
                   .subscribe(
                       component.goToMain.call(component),
                       this.handleError
                   );
        },
        authenticate: function (provider) {
            var component = this;
            this.auth.authenticate(provider)
            .subscribe(
               component.goToMain.call(component),
               this.handleError
            );
        },
        handleError: function (e) {
            console.log(e);
        },
        goToMain: function() {
            this.router.navigate(['Home']);
        },
        ngAfterContentInit: function() {
            this.renderer.setElementClass(this.element, 'app', true);
            if (this.auth.isAuthenticated()) {
                this.goToMain();
            }
        }
    });
