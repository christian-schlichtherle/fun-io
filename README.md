# Software Documentation Template

This is a template for software documentation projects.

## Architecture / Features

+ [Jekyll](https://jekyllrb.com): 
  For page authoring using the Markdown format et al.
  + [Jekyll Livereload](https://github.com/RobertDeRose/jekyll-livereload):
  Reloads the browser page when updating a source file.
  + [Jekyll PlantUML](https://github.com/yegor256/jekyll-plantuml):
  Renders UML diagrams which are inlined in Markdown.
+ [webpack](https://webpack.js.org): 
  Packs all stylesheets, JavaScript et al in single files.
+ [PlantUML](http://plantuml.com):
  Renders UML diagrams.
+ [Bootstrap](https://getbootstrap.com):
  Twitter's front-end component library.
+ [JQuery](https://jquery.com):
  JavaScript library for simple DOM traversion and manipulation.
+ [Popper.js](https://popper.js.org):
  JavaScript library for pop-ups.

## Installation

The following assumes macOS:

```bash
$ brew install node
$ brew install plantuml
$ gem install jekyll
```

## Building

The following commands will build the site and output the result in the `_site` directory:

```sh
$ npm run clean # optional
$ npm install   # required only once
$ npm run build
```

## Development

The following commands will build the site and start a webserver at http://0.0.0.0:4000/ :

```sh
$ npm run clean # optional
$ npm install   # required only once
$ npm start
```

Press Ctrl-C to stop the processes.
