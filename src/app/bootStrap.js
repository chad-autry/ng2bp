//This JS file simply bootstraps the app from the root component when the window loads
var ng = require('angular2/platform/browser'),
    ngCore = require('angular2/core'),
    ngRouter = require('angular2/router'),
    rootComponent = require('./app.js');

(function() {
    document.addEventListener('DOMContentLoaded', function() {
        //If we wanted to inject mock services (such as Http), we could do so here
        ng.bootstrap(rootComponent, [ngRouter.ROUTER_PROVIDERS, ngCore.provide(ngRouter.APP_BASE_HREF, {useValue:'/ng2bp'})]);
    });
})();