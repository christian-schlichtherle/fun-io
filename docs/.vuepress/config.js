module.exports = {
    base: '/fun-io/',
    description: 'Functional I/O for Java and Scala',
    markdown: {
        anchor: {
            level: [2, 3]
        },
        config: md => {
            let markup = require('vuepress-theme-craftdocs/markup');
            md.use(markup)
        }
    },
    theme: 'craftdocs',
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
