var ngCore = require('angular2/core'),
    ngCommonDirectives = require('angular2/src/common/directives'),
    ngRouter = require('angular2/router'),
    linkActiveClass = require('../../common/LinkActiveClassDirective'),
    Auth = require('ng2-ui-auth');

module.exports = ngCore
    .Component({
        selector: 'app-navbar',
        template: `
    <div class="container">
      <div class="navbar navbar-default">
        <!--Note: I don't care about giving a different mouse icon for collapsing, since it is mainly for mobile -->
        <div class="navbar-header" (click)="menuCollapsed = !menuCollapsed">
          <div class="navbar-toggle">
            <span class="sr-only">Toggle navigation</span>
            <i [ngClass]="{'fa-chevron-right': menuCollapsed, 'fa-chevron-down': !menuCollapsed}" class='fa'></i>
          </div>
          <div class="navbar-brand">
            ng2bp
            <small>
              <a class="navbar-link" href="https://github.com/chad-autry/ng2bp/tree/master">
                 master
              </a>
            </small>
          </div>
        </div>
        <div class="navbar-collapse" [ngClass]="{'hidden-xs': menuCollapsed}">
          <ul class="nav navbar-nav">
            <li linkActiveClass="active">
              <a [routerLink]="['Home']">
                <i class="fa fa-home"></i>
                Home
              </a>
            </li>
            <li linkActiveClass="active">
              <a [routerLink]="['LazyLoadDemo']">
                <i class="fa fa-download"></i>
                Lazy Load Demo
              </a>
            </li>
            <li>
              <a href="https://github.com/chad-autry/ng2bp">
                <i class="fa fa-github-alt"></i>
                Github
              </a>
            </li>
            <li>
              <a href="https://github.com/chad-autry/ng2bp/issues">
                <i class="fa fa-comments"></i>
                Support
              </a>
            </li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li linkActiveClass="active" *ngIf="auth.isAuthenticated()">
              <a [routerLink]="['UserManagement']">
                <i class="fa fa-user"></i>
                {{auth.getToken()}}
              </a>
            </li>
            <li linkActiveClass="active" *ngIf="!auth.isAuthenticated()">
              <a [routerLink]="['Login']">
                <i class="fa fa-sign-in"></i>
                Logon
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>`,
     directives: [ngRouter.ROUTER_DIRECTIVES, linkActiveClass, ngCommonDirectives.NgIf]
     })
    .Class({
        constructor: [Auth.Auth, function(auth) {
            this.auth = auth;
        }]
    });