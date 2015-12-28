# [ng2bp](https://github.com/chad-autry/ng2bp) [![Build Status](https://travis-ci.org/chad-autry/ng2bp.svg)](https://travis-ci.org/ng2bp/ng2bp)

An opinionated kickstarter for [AngularJS 2](https://angular.io/) projects.

***

## Quick Start

Install Node.js and then:

```sh
$ git clone git://github.com//chad-autry/ng2bp
$ cd ng2bp
$ npm install
$ npm run build
```

Finally,
```sh
$ npm run server
```
hosts a local server and auto-launches a browser window. The server refreshes the hosted files whenever a build is run, just refresh the browser.

Happy hacking!

## Purpose

ng2bp is a template of my personal practices for building a front end client

## Philosophy

I try to follow the KISS principle where possible

## Learn

### Overall Directory Structure

At a high level, the structure looks roughly like this:

```
ng2bp/
  |- src/
  |  |- app/
  |  |  |- <app component tree structure>
  |  |- assets/
  |  |  |- <static files>
  |  |- common/
  |  |  |- <reusable modules and components>
  |  |- less/
  |  |  |- main.less
  |- node_modules/
  |- package.json
  |- target/
  |- travis.yml
```

What follows is a brief description of each entry, but most directories contain
their own `README.md` file with additional documentation, so browse around to
learn more.


- `src/` - our application sources. [Read more &raquo;](src/README.md)
- `package.json` - metadata about the app, dependencies used for building the app and includion into it, scripts used to build and test the app
- `node_modules` - the location npm stores all dependencies
- `target/` - where all temporary built files go, webapp is the subdirectory where the final application lives
- `travis.yml` - the instructions to TravisCI which allow it to build and push back the finished app to our github page

### npm scripts

//TODO document all the npm scripts here

## Roadmap

This project is mostly meant as a personal example, but documentation doesn't kill anyone and it could likely use even more in order to make it approachable by others

### Contributing

This is an opinionated kickstarter, but the opinions are fluid and
evidence-based. Don't like the way I did something? Think you know of a better
way? Have an idea to make this more useful? Let me know! You can contact me
through all the usual channels or you can open an issue on the GitHub page. If
you're feeling ambitious, you can even submit a pull request - how thoughtful
of you!

Make sure to check out the [Contributing Guide](./CONTRIBUTING.md).

So join the team! We're good people.
