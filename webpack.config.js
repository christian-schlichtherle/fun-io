const path = require('path');
const webpack = require('webpack');

module.exports = {
    entry: './webpack/app.js',
    output: {
        path: path.resolve(__dirname, 'assets/javascripts'),
        filename: 'app.js'
    },
    plugins: [
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            'window.jQuery': 'jquery',
            Popper: ['popper.js', 'default']
        })
    ]
};
