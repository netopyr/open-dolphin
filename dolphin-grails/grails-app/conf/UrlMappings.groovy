class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
        "/djs/$pageName" (
            controller : 'dolphinjs',
            view : { params.pageName }
        )
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
