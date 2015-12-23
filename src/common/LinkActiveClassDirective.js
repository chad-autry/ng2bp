var ngCore = require('angular2/core'),
    ngRouter = require('angular2/router');

// A custom directive to apply the given class
// to elements with a child routerLink directive that's active
module.exports = ngCore.Directive({
    selector: '[linkActiveClass]',
    inputs: ['linkActiveClass']
})
.Class({
  constructor: [[new ngCore.Query(ngRouter.RouterLink), ngCore.QueryList],
      ngCore.ElementRef,
      ngCore.Renderer,
      ngRouter.Router,
      function(routerLinks,eleRef, renderer, router) {
          //Get the routerLink from our query when it is presented. This should be called once
          routerLinks.changes.subscribe((_) => {
              this.routerLink = routerLinks.first;
          });
          //Every time the route changes update elements class to add/remove the class provided
          router.subscribe((_) => {
            if (this.isRouteActive()) {
                renderer.setElementClass(eleRef, this.linkActiveClass, true);
            } else {
                renderer.setElementClass(eleRef, this.linkActiveClass, false);
            }
          });
      }],
  isRouteActive: function() {
    return !!this.routerLink ? this.routerLink.isRouteActive : false;
  }
});

//module.exports.annotations = [new ngCore.ContentChildren(ngRouter.RouterLink, 'routerLink', module.exports) ];