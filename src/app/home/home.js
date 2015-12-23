var ngCore = require('angular2/core');

module.exports = ngCore
    .Component({
        selector: 'app-home',
        template: `
        <div class="container">
<div class="jumbotron">
  <h1>Important</h1>

  <p class="lead">
    Angular 2 is still in beta. Also TypeScript is MUCH better documented than plain JavaScript, you'll probablly have an easier time using TypeScript.
  </p>
  <p class="lead">
    ng2bp has its origins in Josh Miller's ngbp project. It has since then been heavily modified by me.
  </p>
  <p class="lead">
    Special thanks to everyone over of the Angular 2 gitter chat. Particularlly brandonroberts
  </p>
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
        constructor: function() {}
    });
