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

The following assumes macOS with [Homebrew](https://brew.sh):

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

The following commands will build the site and start a webserver at [http://0.0.0.0:4000/]():

```sh
$ npm run clean # optional
$ npm install   # required only once
$ npm start
```

Press Ctrl-C to stop the processes.

### Testing on Mobile Devices

The web server binds to all host interfaces, which allows to conveniently test the site on mobile devices:

1. Allow access to port 4000 on the host or switch off its firewall entirely (not recommended).
1. Run `npm start`.
1. On the mobile device, browse to [http://A.B.C.D:4000/](), where `A.B.C.D` is the IP address of one of the network
   interfaces of the host.
