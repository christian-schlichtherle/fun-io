const ExtractTextWebpackPlugin = require('extract-text-webpack-plugin');
const path = require('path');
const webpack = require('webpack');

module.exports = {
    entry: path.resolve(__dirname, 'webpack/entry.js'),
    module: {
        rules: [{
            test: /\.scss$/,
            use: ExtractTextWebpackPlugin.extract({
                fallback: "style-loader",
                use: [{
                    loader: 'css-loader'
                }, {
                    loader: 'postcss-loader',
                    options: {
                        plugins: function () {
                            return [
                                require('precss'),
                                require('autoprefixer')
                            ];
                        }
                    }
                }, {
                    loader: 'sass-loader'
                }]
            })
        }, {
            test: /\.p?uml$/,
            use: [
                'file-loader?name=[name].svg&outputPath=uml/',
                path.resolve(__dirname, 'plantuml-loader')
            ]
        }]
    },
    output: {
        path: path.resolve(__dirname, 'assets/bundle'),
        filename: 'main.js'
    },
    plugins: [
        new ExtractTextWebpackPlugin('main.css'),
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            'window.jQuery': 'jquery',
            Popper: ['popper.js', 'default']
        })
    ]
};
