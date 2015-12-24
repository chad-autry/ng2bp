var ngCore = require('angular2/core'),
    ngBrowser = require('angular2/platform/browser');

module.exports = ngCore
    .Component({
        selector: 'app-home',
        template: `
        <div class="container">
<div class="jumbotron">
  <h1 class="text-center">Special Notices</h1>
  <ul class="lead">
    <li>
      <i class="fa fa-exclamation-triangle"></i>&nbsp;Angular 2 is still in beta. As such the plain JavaScript documentation is currentlly lacking and the general recommendation at the moment is to use TypeScript for better support.
    </li>
    <li>
      ng2bp has its origins in <a href="https://github.com/joshdmiller"><i class="fa fa-at"></i>joshdmiller's</a>&nbsp;<a href="https://github.com/ngbp/ngbp"><i class="fa fa-code-fork"></i>ngbp project</a>
      . It has since then been heavily modified until it is only superficially similar.
    </li>
    <li>
      Special thanks to everyone over in the <a href="https://gitter.im/angular/angular">
      Angular 2 gitter chat&nbsp;<i class="fa fa-comments"></i></a>. Particularlly <a href="https://github.com/brandonroberts">
      <i class="fa fa-at"></i>brandonroberts</a> for his helpful plunkers.
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
      </p>
    </div>
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-retweet"></i> Modularization</h4>
      <p>
        Supports a structure that maintains separation of concerns while
        ensuring maximum code reuse.
      </p>
    </div>
  </div>
  <div class="row">
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-star"></i> AngularJS 2</h4>
      <p>
        JavaScript framework that augments browser-based, single-page
        applications with MVC functionality.
        <a href="https://angular.io/">More &raquo;</a>
      </p>
    </div>
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-resize-small"></i> LESS CSS</h4>
      <p>
        The dynamic stylesheet language that extends CSS with efficiency.
        <a href="http://lesscss.org">More &raquo;</a>
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
  </div>
  <div class="row">
    <div class="col-xs-12 col-sm-6 col-md-4">
      <h4><i class="fa fa-circle"></i> Angular UI Bootstrap</h4>
      <p>
        Pure AngularJS components for Bootstrap written by the
        <a href="https://github.com/angular-ui?tab=members">AngularUI Team</a>.
        <a href="http://angular-ui.github.com/bootstrap">More &raquo;</a>
      </p>
    </div>
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
      <h4><i class="fa fa-asterisk"></i> Placeholders</h4>
      <p>
        Client-side image and text placeholder directives written in pure
        AngularJS to make designing mock-ups wicked-fast.
        <a href="http://joshdmiller.github.com/angular-placeholders">
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
