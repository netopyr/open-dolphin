define(["require", "exports", "../../js/dolphin/EventBus"], function(require, exports, __bus__) {
    
    var bus = __bus__;

    (function (dolphin) {
        var clientAttributeInstanceCount = 0;
        var ClientAttribute = (function () {
            function ClientAttribute(propertyName, qualifier, value, tag) {
                if (typeof tag === "undefined") { tag = "VALUE"; }
                this.propertyName = propertyName;
                this.qualifier = qualifier;
                this.tag = tag;
                this.dirty = false;
                this.id = clientAttributeInstanceCount++;
                this.valueChangeBus = new bus.dolphin.EventBus();
                this.qualifierChangeBus = new bus.dolphin.EventBus();
                this.dirtyValueChangeBus = new bus.dolphin.EventBus();
                this.baseValueChangeBus = new bus.dolphin.EventBus();
                this.setValue(value);
                this.setBaseValue(value);
            }
            ClientAttribute.prototype.isDirty = function () {
                return this.dirty;
            };

            ClientAttribute.prototype.getBaseValue = function () {
                return this.baseValue;
            };

            ClientAttribute.prototype.setPresentationModel = function (presentationModel) {
                if (this.presentationModel) {
                    alert("You can not set a presentation model for an attribute that is already bound.");
                }
                this.presentationModel = presentationModel;
            };

            ClientAttribute.prototype.getPresentationModel = function () {
                return this.presentationModel;
            };

            ClientAttribute.prototype.getValue = function () {
                return this.value;
            };

            ClientAttribute.prototype.setValue = function (newValue) {
                var verifiedValue = ClientAttribute.checkValue(newValue);
                if (this.value == verifiedValue)
                    return;
                var oldValue = this.value;
                this.value = verifiedValue;
                this.setDirty(this.calculateDirty(this.baseValue, verifiedValue));
                this.valueChangeBus.trigger({ 'oldValue': oldValue, 'newValue': verifiedValue });
            };

            ClientAttribute.prototype.calculateDirty = function (baseValue, value) {
                if (baseValue == null) {
                    return value != null;
                } else {
                    return baseValue != value;
                }
            };

            ClientAttribute.prototype.setDirty = function (dirty) {
                var oldVal = this.dirty;
                this.dirty = dirty;
                this.dirtyValueChangeBus.trigger({ 'oldValue': oldVal, 'newValue': this.dirty });
                if (this.presentationModel)
                    this.presentationModel.updateDirty();
            };

            ClientAttribute.prototype.setQualifier = function (newQualifier) {
                if (this.qualifier == newQualifier)
                    return;
                var oldQualifier = this.qualifier;
                this.qualifier = newQualifier;
                this.qualifierChangeBus.trigger({ 'oldValue': oldQualifier, 'newValue': newQualifier });
            };

            ClientAttribute.prototype.setBaseValue = function (baseValue) {
                if (this.baseValue == baseValue)
                    return;
                var oldBaseValue = this.baseValue;
                this.baseValue = baseValue;
                this.setDirty(this.calculateDirty(baseValue, this.value));
                this.baseValueChangeBus.trigger({ 'oldValue': oldBaseValue, 'newValue': baseValue });
            };

            ClientAttribute.prototype.rebase = function () {
                this.setBaseValue(this.value);
            };

            ClientAttribute.prototype.reset = function () {
                this.setValue(this.baseValue);
                this.setDirty(false);
            };

            ClientAttribute.checkValue = function (value) {
                if (value == null || value == undefined) {
                    return null;
                }
                var result = value;
                if (result instanceof String || result instanceof Boolean || result instanceof Number) {
                    result = value.valueOf();
                }
                if (result instanceof ClientAttribute) {
                    console.log("An Attribute may not itself contain an attribute as a value. Assuming you forgot to call value.");
                    result = this.checkValue((value).value);
                }
                var ok = false;
                if (this.SUPPORTED_VALUE_TYPES.indexOf(typeof result) > -1 || result instanceof Date) {
                    ok = true;
                }
                if (!ok) {
                    throw new Error("Attribute values of this type are not allowed: " + typeof value);
                }
                return result;
            };

            // todo:  immediate value update on registration?
            ClientAttribute.prototype.onValueChange = function (eventHandler) {
                this.valueChangeBus.onEvent(eventHandler);
                eventHandler({ "oldValue": this.value, "newValue": this.value });
            };

            ClientAttribute.prototype.onQualifierChange = function (eventHandler) {
                this.qualifierChangeBus.onEvent(eventHandler);
            };

            ClientAttribute.prototype.onDirty = function (eventHandler) {
                this.dirtyValueChangeBus.onEvent(eventHandler);
            };

            ClientAttribute.prototype.onBaseValueChange = function (eventHandler) {
                this.baseValueChangeBus.onEvent(eventHandler);
            };

            ClientAttribute.prototype.syncWith = function (sourceAttribute) {
                if (sourceAttribute) {
                    this.setBaseValue(sourceAttribute.getBaseValue());
                    this.setQualifier(sourceAttribute.qualifier);
                    this.setValue(sourceAttribute.value);
                }
            };
            ClientAttribute.SUPPORTED_VALUE_TYPES = ["string", "number", "boolean"];
            return ClientAttribute;
        })();
        dolphin.ClientAttribute = ClientAttribute;
    })(exports.dolphin || (exports.dolphin = {}));
    var dolphin = exports.dolphin;
});
//# sourceMappingURL=ClientAttribute.js.map
