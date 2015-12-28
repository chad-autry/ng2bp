var ngCore = require('angular2/core'),
    ngBrowser = require('angular2/platform/browser');

module.exports = ngCore
    .Component({
        selector: 'app-home',
        template: `
        <div class="container">
<div class="jumbotron">
  <h1 class="text-center">Notice</h1>
  <ul class="lead">
    <li>
      <i class="fa fa-exclamation-triangle"></i>&nbsp;Angular 2 is still in beta. On top of any normal beta issues, another issue is plain JavaScript documentation and tutorials are extremely limited. A new user of Angular 2 is highly encouraged to go the TypeScript route rather than follow this project's pure JS route.
    </li>
    <li>
      <i class="fa fa-code-fork"></i>&nbsp;ng2bp has its origins in <a href="https://github.com/joshdmiller">joshdmiller's</a>&nbsp;<a href="https://github.com/ngbp/ngbp">ngbp project</a>. 
      It has since then been heavily modified.
    </li>
    <li>
      <i class="fa fa-comments"></i>&nbsp;Special thanks to everyone over in the <a href="https://gitter.im/angular/angular">
      Angular 2 gitter chat</a>. Particularlly <a href="https://github.com/brandonroberts">
      brandonroberts</a> for his helpful plunkers.
    </li>
  </ul>
</div>

<div class="marketing">
  <div class="row">
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-gears"></i> npm</h4>
      <p>
        Dependency management, build, and test execution rolled into one
      </p>
    </div>
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-magic"></i> browserify</h4>
      <p>
        Brings node packages to the browser. Magic!
        <a href="http://browserify.org/">
          More &raquo;
        </a>
      </p>
    </div>
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-star"></i> AngularJS 2</h4>
      <p>
        JavaScript framework that augments browser-based, single-page
        applications with MVC functionality.
        <a href="https://angular.io/">More &raquo;</a>
      </p>
    </div>
  </div>
  <div class="row">
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-flag"></i> Font Awesome</h4>
      <p>
        The iconic font designed for use with Twitter Bootstrap.
        <a href="http://fortawesome.github.com/Font-Awesome">
          More &raquo;
        </a>
      </p>
    </div>
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-twitter"></i> Twitter Bootstrap</h4>
      <p>
        Sleek, intuitive, and powerful front-end framework for faster and easier
        web development.
        <a href="http://getbootstrap.com">More &raquo;</a>
      </p>
    </div>
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-resize-small"></i> LESS CSS</h4>
      <p>
        The dynamic stylesheet language that extends CSS with efficiency.
        <a href="http://lesscss.org">More &raquo;</a>
      </p>
    </div>
  </div>
  <div class="row">
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-sign-in"></i> ng2-ui-auth/Satellizer</h4>
      <p>
        Token-based Angular2 Authentication.
        <a href="https://github.com/ronzeidman/ng2-ui-auth">
          More &raquo;
        </a>
        <br/>Based off the original AngularJS project 
        <a href="https://github.com/sahat/satellizer">
          More &raquo;
        </a>
      </p>
    </div>
  </div>
</div>
</div>`
     })
    .Class({
        constructor: [ngBrowser.Title, function( title) {
            //Save the titleService
            this.title = title;
        }],
        routerOnActivate: function(nextInstruction, prevInstruction) {
            this.title.setTitle("ng2bp | Home");
        }
    });
