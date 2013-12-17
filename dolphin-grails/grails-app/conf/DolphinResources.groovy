modules = {

    requirejs {
        resource disposition: 'head', url: 'libs/require.js'
    }

    dolphin {

        dependsOn 'requirejs'

//        un-comment the below to create the list of all dolphin.js files
//        new File("web-app/js/dolphin/").eachFile {
//            if ( ! it.name.endsWith(".js")) return
//            println "\t\tresource disposition:'head', url: 'js/dolphin/$it.name'"
//        }

        resource disposition: 'head', url: 'js/dolphin/All.js'
        resource disposition: 'head', url: 'js/dolphin/Attribute.js'
        resource disposition: 'head', url: 'js/dolphin/AttributeCreatedNotification.js'
        resource disposition: 'head', url: 'js/dolphin/AttributeMetadataChangedCommand.js'
        resource disposition: 'head', url: 'js/dolphin/BaseValueChangedCommand.js'
        resource disposition: 'head', url: 'js/dolphin/CallNamedActionCommand.js'
        resource disposition: 'head', url: 'js/dolphin/ChangeAttributeMetadataCommand.js'
        resource disposition: 'head', url: 'js/dolphin/ClientAttribute.js'
        resource disposition: 'head', url: 'js/dolphin/ClientConnector.js'
        resource disposition: 'head', url: 'js/dolphin/ClientDolphin.js'
        resource disposition: 'head', url: 'js/dolphin/ClientModelStore.js'
        resource disposition: 'head', url: 'js/dolphin/ClientPresentationModel.js'
        resource disposition: 'head', url: 'js/dolphin/Codec.js'
        resource disposition: 'head', url: 'js/dolphin/Command.js'
        resource disposition: 'head', url: 'js/dolphin/CreatePresentationModelCommand.js'
        resource disposition: 'head', url: 'js/dolphin/DeleteAllPresentationModelsOfTypeCommand.js'
        resource disposition: 'head', url: 'js/dolphin/DeletedAllPresentationModelsOfTypeNotification.js'
        resource disposition: 'head', url: 'js/dolphin/DeletedPresentationModelNotification.js'
        resource disposition: 'head', url: 'js/dolphin/DeletePresentationModelCommand.js'
        resource disposition: 'head', url: 'js/dolphin/Dolphin.js'
        resource disposition: 'head', url: 'js/dolphin/EmptyNotification.js'
        resource disposition: 'head', url: 'js/dolphin/EventBus.js'
        resource disposition: 'head', url: 'js/dolphin/GetPresentationModelCommand.js'
        resource disposition: 'head', url: 'js/dolphin/HttpTransmitter.js'
        resource disposition: 'head', url: 'js/dolphin/InitializeAttributeCommand.js'
        resource disposition: 'head', url: 'js/dolphin/Map.js'
        resource disposition: 'head', url: 'js/dolphin/NamedCommand.js'
        resource disposition: 'head', url: 'js/dolphin/NoTransmitter.js'
        resource disposition: 'head', url: 'js/dolphin/PresentationModelResetedCommand.js'
        resource disposition: 'head', url: 'js/dolphin/ResetPresentationModelCommand.js'
        resource disposition: 'head', url: 'js/dolphin/SavedPresentationModelNotification.js'
        resource disposition: 'head', url: 'js/dolphin/SwitchPresentationModelCommand.js'
        resource disposition: 'head', url: 'js/dolphin/ValueChangedCommand.js'



    }

}