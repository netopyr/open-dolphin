// Place your Spring DSL code here
beans = {
    dolphinBean(DolphinSpringBean) { bean ->
        bean.scope = 'session'
    }
}
