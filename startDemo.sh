#!/bin/bash
app_prop[1]=BindList
app_prop[2]=CategoryChange
app_prop[3]=ComposedDirty
app_prop[4]=Swing
app_prop[5]=Crud
app_prop[6]=DependentChoiceBox
app_prop[7]=DirtyAttributeFlag
app_prop[8]=GrailsClient
app_prop[9]=MultipleAttributes
app_prop[10]=MultipleAttributesSwitch
app_prop[11]=MultipleSelection
app_prop[12]=NewAndSave
app_prop[13]=PresentationModelLinks
app_prop[14]=Push
app_prop[15]=ReferenceTable
app_prop[16]=Reset
app_prop[17]=Save
app_prop[18]=Search
app_prop[19]=SharedAttributes
app_prop[20]=SingleAttributeMultipleBindings
if [ "$#" -eq 0 ];
then
echo No start parameter provided. Provide start parameter 1-20 to start one of the following demos:
i=0
for d in "${app_prop[@]}"
do
     i=`expr $i + 1`
     echo [$i] $d
done
exit 0
fi
if [ $1 -lt 1 ] || [ $1 -gt 20 ];
then
    exit 1
fi
echo Starting demo: ${app_prop[$1]}
./gradlew demo-javafx-combined:run --stacktrace -PappProp=${app_prop[$1]}
