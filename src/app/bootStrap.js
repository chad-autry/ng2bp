"use strict";
//This JS file simply bootstraps the app from the root component when the window loads
var ng = require('angular2/platform/browser'),
    ngCore = require('angular2/core'),
    ngRouter = require('angular2/router'),
    rootComponent = require('./app.js'),
    ngHttp = require('angular2/http'),
    ngTesting = require('angular2/http/testing'),
    jwt = require('angular2-jwt'),
    satellizer = require('ng2-ui-auth');
    
    //This is the public ID google gave the ng2bp project to authorize with. It will work locally, or from the githib page.
    //It requires the private key to verify the response on the server and actually access any information. Replace with your own project's key
    const GOOGLE_CLIENT_ID = '458116224997-15nrlko9rnpqj9pjeln1303u9t514tmn.apps.googleusercontent.com';
    
    
    //Note: This bootStrap sets up an Http service with a mock backend. IF there was a real backened, you'd use ngHttp.HTTP_PROVIDERS, instead of 
    // ngHttp.BaseRequestOptions, ngTesting.MockBackend, and the Http provider

(function() {
    document.addEventListener('DOMContentLoaded', function() {
        var backEnd = new ngTesting.MockBackend();
        backEnd.connections.subscribe((c) => {
            //Our mock backend service returns a JWT token with a payload of {name: "John Doe"}  no matter what the request
            c.mockRespond({ json: function(){return {token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiSm9obiBEb2UifQ.xuEv8qrfXu424LZk8bVgr9MQJUIrp1rHcPyZw_KSsds'};}});
        });

        ng.bootstrap(rootComponent, [ng.Title, ngRouter.ROUTER_PROVIDERS, ngHttp.BaseRequestOptions,
        ngCore.provide(ngRouter.LocationStrategy, {useClass:ngRouter.HashLocationStrategy}),
        ngCore.provide(ngHttp.Http, {useFactory:
            function(defaultOptions) {
                return new ngHttp.Http(backEnd, defaultOptions);
            },
            deps: [ngHttp.BaseRequestOptions]}),
        ngCore.provide(jwt.JwtHelper, {useFactory:
            function() {
                return new jwt.JwtHelper();
            }}
        ),
        satellizer.SATELLIZER_PROVIDERS({providers: {google: {clientId: GOOGLE_CLIENT_ID}}}),
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