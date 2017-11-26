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
  Packs all stylesheets, JavaScript et al in single files
  + [PlantUML file loader](https://github.com/lucsorel/plantuml-file-loader):
  Renders separate PlantUML files.
+ [PlantUML](http://plantuml.com):
  Renders UML diagrams.

## Prerequisites

```bash
$ gem install jekyll
$ brew install plantuml
```

## Setup

```sh
$ bundle install
$ bundle exec jekyll build
```
