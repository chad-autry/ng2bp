var ngCore = require('angular2/core'),
    ngBrowser = require('angular2/platform/browser'),
    ngRouter = require('angular2/router'),
    ngCommon = require('angular2/common'),
    Auth = require('ng2-ui-auth');

module.exports = ngCore
    .Component({
        selector: 'login',
        template: `
    <div class="container">
    <div class="center-form panel">
        <div class="panel-body">
            <h2 class="text-center">Log in</h2>
            <h6 class="text-center">ng2bp has a mock backend which is hard coded to log you in as 'John Doe'</h6>
            <form (ngSubmit)="login()" [ngFormModel]="form" #f="ngForm">
                <div class="form-group has-feedback" [class.has-error]="f.form.controls.email.dirty && !f.form.controls.email.valid">
                    <input type="text" ngControl="email" class="form-control input-lg" [(ngModel)]="user.email" placeholder="Email" autofocus>
                    <i class="fa form-control-feedback fa-at"></i>
                    <ng-messages [errors]="f.form.controls.email.errors"></ng-messages>
                </div>
                <div class="form-group has-feedback" [class.has-error]="f.form.controls.password.dirty && !f.form.controls.password.valid">
                    <input type="password" ngControl="password" class="form-control input-lg" [(ngModel)]="user.password" placeholder="Password">
                    <i class="fa form-control-feedback fa-key"></i>
                    <ng-messages [errors]="f.form.controls.password.errors"></ng-messages>
                </div>
                <button type="submit" [disabled]="!f.form.valid" class="btn btn-lg btn-block btn-success">Log in</button>
                <br>
                <!--
                <p class="text-center text-muted" >
                    <small>Don&apos;t have an account yet?</small>
                    <a [routerLink]="['Signup']">Sign up</a>
                </p>-->
                <div class="signup-or-separator">
                    <h6 class="text">or</h6>
                    <hr>
                </div>
            </form>

            <button class="btn btn-block btn-google-plus" (click)="authenticate('google')">
                <i class="fa fa-google-plus"></i>
                sign in with Google
            </button>
        </div>
    </div>
</div>`,
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
                   this.auth.login(this.user)
                   .subscribe(
                       () => this.goToMain(),
                       this.handleError
                   );
        },
        authenticate: function (provider) {
            this.auth.authenticate(provider)
            .subscribe(
               () => this.goToMain(),
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
