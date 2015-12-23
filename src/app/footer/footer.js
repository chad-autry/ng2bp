var ngCore = require('angular2/core');

module.exports = ngCore
    .Component({
        selector: 'app-footer',
        template: `
    <footer class="footer">
      <div class="container">
        <div class="footer-inner">
          <p>
            <i class="fa fa-copyright"></i> 2015 <a href="http://chad-autry.github.io/">Chad Autry</a>.
          </p>
        </div>
      </div>
    </footer>`
     })
    .Class({
        constructor: function() {}
    });