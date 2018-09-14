module.exports = {
    base: '/fun-io/',
    description: 'Functional I/O for Java and Scala',
    markdown: {
        config: md => md.use(require('./theme/markup'))
    },
    themeConfig: {
        codeLanguages: {
            java: 'Java',
            pom: 'Maven',
            sbt: 'SBT',
            scala: 'Scala'
        },
        sidebar: [
            '/',
            'module-structure-and-features',
            'basic-usage',
            'basic-archive-processing',
            'advanced-archive-processing'
        ],
        sidebarDepth: 2
    },
    title: 'Fun I/O'
};
