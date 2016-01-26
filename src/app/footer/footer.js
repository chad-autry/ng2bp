var ngCore = require('angular2/core'),
    footerHtml = require('./footer.html');

module.exports = ngCore
    .Component({
        selector: 'app-footer',
        template: footerHtml
     })
    .Class({
        constructor: function() {}
    });