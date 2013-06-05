application {
    title = 'CRUD Demo'
    startupGroups = ['crud']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "crud"
    'crud' {
        model      = 'org.opendolphin.demo.crud.CrudModel'
        view       = 'org.opendolphin.demo.crud.CrudView'
        controller = 'org.opendolphin.demo.crud.CrudController'
    }
    // MVC Group for "portfolioEditor"
    'portfolioEditor' {
        model      = 'org.opendolphin.demo.crud.PortfolioEditorModel'
        view       = 'org.opendolphin.demo.crud.PortfolioEditorView'
        controller = 'org.opendolphin.demo.crud.PortfolioEditorController'
    }
}
