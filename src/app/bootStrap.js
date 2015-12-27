//This JS file simply bootstraps the app from the root component when the window loads
var ng = require('angular2/platform/browser'),
    ngCore = require('angular2/core'),
    ngRouter = require('angular2/router'),
    rootComponent = require('./app.js'),
    ngHttp = require('angular2/http'),
    jwt = require('angular2-jwt'),
    satellizer = require('ng2-ui-auth');
    

(function() {
    document.addEventListener('DOMContentLoaded', function() {
        //If we wanted to inject mock services (such as Http), we could do so here
        ng.bootstrap(rootComponent, [ng.Title, ngRouter.ROUTER_PROVIDERS, ngCore.provide(ngRouter.APP_BASE_HREF, {useValue:'/'}),
        ngHttp.HTTP_PROVIDERS,
        satellizer.SATELLIZER_PROVIDERS({providers: {google: {clientId: 'tessst'}}}),
        ngCore.provide(jwt.AuthHttp, {
        useFactory: (auth, config) => {
            return new jwt.AuthHttp({
                tokenName: config.tokenName,
                tokenGetter: () => auth.getToken(),
            });
        },
        deps: [satellizer.Auth, satellizer.Config]
        })
      ]);
    });
})();