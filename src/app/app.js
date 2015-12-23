var ngCore = require('angular2/core'),
    ngRouter = require('angular2/router'),
    navbarComponent = require('./navbar/navbar.js'),
    homeComponent = require('./home/home.js'),
    aboutComponent = require('./about/about.js'),
    footerComponent = require('./footer/footer.js');

module.exports = ngCore
    .Component({
        selector: 'my-app',
        directives: [navbarComponent, footerComponent, ngRouter.ROUTER_DIRECTIVES],
        template: `
    <app-navbar></app-navbar>
    <router-outlet></router-outlet>
    <app-footer></app-footer>`
     })
    .Class({
        constructor: [ngRouter.Router, function( router) {
            //Configure our routes
            router.config([
                { path: '/home', component: homeComponent, name: 'Home', useAsDefault: true },
                { path: '/about', component: aboutComponent, name: 'About'}
            ]);
        }]
    });