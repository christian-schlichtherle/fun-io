const loaderUtils = require('loader-utils');
const childProcess = require('child_process');
const { PassThrough } = require('stream');
const { Buffer } = require('buffer');

const LOADER_CONFIG_KEY = 'plantumlLoader';
const DEFAULT_CONFIG = { format: 'svg' };

function bufferAsReadableStream(buffer) {
    const bufferStream = new PassThrough();
    bufferStream.end(buffer);
    return bufferStream
}

module.exports = function (source) {
    const callback = this.async();
    let query = { };
    let configKey = LOADER_CONFIG_KEY;
    if (this.query) {
        query = loaderUtils.parseQuery(this.query);
        configKey = query.config || LOADER_CONFIG_KEY
    }
    const loaderOptions = this.options[configKey] || { };
    const config = Object.assign(DEFAULT_CONFIG, loaderOptions, query);
    const plantuml = childProcess.spawn('plantuml', ['-p', '-charset', 'utf8', `-t${config.format}`]);
    bufferAsReadableStream(source).pipe(plantuml.stdin);
    const convertedChunks = [];
    const errors = [];
    plantuml.stdout.on('data', data => convertedChunks.push(data));
    plantuml.stderr.on('data', error => errors.push(error));
    plantuml.on('close', () => {
        if (errors.length > 0) {
            callback(new Error(errors.join(', ')))
        } else {
            callback(null, Buffer.concat(convertedChunks))
        }
    })
};

module.exports.raw = true;
