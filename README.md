# Software Documentation Template

This is a template for software documentation projects.

## Architecture / Features

+ [Jekyll](https://jekyllrb.com): 
  For page authoring using the Markdown format et al.
  + [Jekyll Livereload](https://github.com/RobertDeRose/jekyll-livereload):
  Reloads the browser page when updating a source file.
  + [Jekyll PlantUML](https://github.com/yegor256/jekyll-plantuml):
  Renders UML diagrams contained in MarkDown.
+ [webpack](https://webpack.js.org): 
  Packs all stylesheets, JavaScript et al in single files.
  + [PlantUML file loader](https://github.com/lucsorel/plantuml-file-loader):
  Renders separate PlantUML files.
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
$ npm install
$ npm run build
$ bundle install
$ bundle exec jekyll build
```
