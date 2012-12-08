@ECHO OFF

set bind_list=BindList
set category_change=CategoryChange
set composed_dirty=ComposedDirty
set crud=Crud
set dependent_choice_box=DependentChoiceBox
set dirty_attribute_flag=DirtyAttributeFlag
set grails_client=GrailsClient
set grails_client_performance=GrailsClientPerformance
set multiple_attributes=MultipleAttributes
set multiple_attributes_switch=MultipleAttributesSwitch
set multiple_selection=MultipleSelection
set new_and_save=NewAndSave
set presentation_model_links=PresentationModelLinks
set push=Push
set reference_table=ReferenceTable
set reset=Reset
set save=Save
set search=Search
set shared_attributes=SharedAttributes
set single_attribute_multiple_bindings=SingleAttributeMultipleBindings
set swing=Swing

set app_prop=push

IF "%1"=="" GOTO DEFAULT
IF "%1"=="1" set app_prop=%bind_list%
IF "%1"=="2" set app_prop=%category_change%
IF "%1"=="3" set app_prop=%composed_dirty%
IF "%1"=="4" set app_prop=%grails_client_performance%
IF "%1"=="5" set app_prop=%crud%
IF "%1"=="6" set app_prop=%dependent_choice_box%
IF "%1"=="7" set app_prop=%dirty_attribute_flag%
IF "%1"=="8" set app_prop=%multiple_attributes%
IF "%1"=="9" set app_prop=%multiple_attributes_switch%
IF "%1"=="10" set app_prop=%multiple_attributes%
IF "%1"=="11" set app_prop=%new_and_save%
IF "%1"=="12" set app_prop=%presentation_model_links%
IF "%1"=="13" set app_prop=%push%
IF "%1"=="14" set app_prop=%reference_table%
IF "%1"=="15" set app_prop=%reset%
IF "%1"=="16" set app_prop=%save%
IF "%1"=="17" set app_prop=%search%
IF "%1"=="18" set app_prop=%shared_attributes%
IF "%1"=="19" set app_prop=%single_attribute_multiple_bindings%
IF "%1"=="20" set app_prop=%swing%

IF %1 leq 0 GOTO END
IF %1 geq 21 GOTO END
ECHO Starting demo: %app_prop%
gradlew demo-javafx-combined:run --stacktrace -PappProp=%app_prop%
GOTO END
:DEFAULT
    ECHO No start parameter provided. Provide start parameter 1-21 to start one of the following demos:
    ECHO [1] : %bind_list%
    ECHO [2] : %category_change%
    ECHO [3] : %composed_dirty%
    ECHO [4] : %grails_client_performance%
    ECHO [5] : %crud%
    ECHO [6] : %dependent_choice_box%
    ECHO [7] : %dirty_attribute_flag%
    ECHO [8] : %multiple_attributes%
    ECHO [9] : %multiple_attributes_switch%
    ECHO [10] : %multiple_attributes%
    ECHO [11] : %new_and_save%
    ECHO [12] : %presentation_model_links%
    ECHO [13] : %push%
    ECHO [14] : %reference_table%
    ECHO [15] : %reset%
    ECHO [16] : %save%
    ECHO [17] : %search%
    ECHO [18] : %shared_attributes%
    ECHO [19] : %single_attribute_multiple_bindings%
    ECHO [20] : %swing%
    GOTO END
:END
