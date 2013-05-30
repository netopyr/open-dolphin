application {
    title = 'Sample'
    startupGroups = ['sample']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "sample"
    'sample' {
        model      = 'org.opendolphin.demo.crud.SampleModel'
        view       = 'org.opendolphin.demo.crud.SampleView'
        controller = 'org.opendolphin.demo.crud.SampleController'
    }
}
