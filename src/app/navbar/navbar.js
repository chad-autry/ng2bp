var ngCore = require('angular2/core'),
    ngCommonDirectives = require('angular2/src/common/directives'),
    ngRouter = require('angular2/router'),
    linkActiveClass = require('../../common/LinkActiveClassDirective'),
    jwt = require('angular2-jwt'),
    Auth = require('ng2-ui-auth'),
    navbarHtml = require('./navbar.html');

module.exports = ngCore
    .Component({
        selector: 'app-navbar',
        template: navbarHtml,
     directives: [ngRouter.ROUTER_DIRECTIVES, linkActiveClass, ngCommonDirectives.NgIf]
     })
    .Class({
        constructor: [Auth.Auth, jwt.JwtHelper, function(auth, jwtHelper) {
            this.auth = auth;
            this.jwtHelper = jwtHelper;
        }]
    });