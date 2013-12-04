import tsUnit            = require("../../testsuite/tsUnit")
import ca                = require("../../js/dolphin/ClientAttribute")
import cpm               = require("../../js/dolphin/ClientPresentationModel")
import map               = require("../../js/dolphin/Map")

export module dolphin {
    export class MapTests extends tsUnit.tsUnit.TestClass {

        testAdd() {
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined, "type1");
            var pm2 = new cpm.dolphin.ClientPresentationModel(undefined, "type2");

            var testMap:map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel> = new map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel>();
            testMap.put(pm1.id, pm1);
            testMap.put(pm2.id, pm2);

            this.areIdentical(testMap.length(), 2);
            this.isTrue(testMap.containsKey(pm1.id))
            this.isTrue(testMap.containsKey(pm2.id))

            this.isFalse(testMap.containsKey('dummy'))

        }

        testRemove() {
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined, "type1");

            var testMap:map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel> = new map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel>();

            testMap.put(pm1.id, pm1);

            testMap.remove(pm1.id)
            this.isFalse(testMap.containsKey(pm1.id))

            testMap.put(pm1.id, pm1);
            this.isTrue(testMap.containsKey(pm1.id))
        }

        testForEach() {
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined, "type1");
            var pm2 = new cpm.dolphin.ClientPresentationModel(undefined, "type2");

            var testMap:map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel> = new map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel>();
            testMap.put(pm1.id, pm1);
            testMap.put(pm2.id, pm2);

            var keys:string[] = [];
            var values:cpm.dolphin.ClientPresentationModel[] = [];
            testMap.forEach((key:string, value:cpm.dolphin.ClientPresentationModel) => {
                keys.push(key);
                values.push(value);
            })

            this.areIdentical(keys.length, 2);
            this.areIdentical(values.length, 2);

            this.areIdentical(keys[0], values[0].id);
            this.areIdentical(keys[1], values[1].id);
        }

        testGet() {
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined, "type1");
            var testMap:map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel> = new map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel>();
            testMap.put(pm1.id, pm1);

            var pm = testMap.get(pm1.id);
            this.areIdentical(pm.id, pm1.id);

            var pm = testMap.get("dummyKey");
            this.areIdentical(pm, undefined);
        }

        testKeySetAndValues() {
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined, "type1");
            var pm2 = new cpm.dolphin.ClientPresentationModel(undefined, "type2");

            var testMap:map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel> = new map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel>();

            this.areIdentical(testMap.keySet().length, 0);
            this.areIdentical(testMap.values().length, 0);

            testMap.put(pm1.id, pm1);
            testMap.put(pm2.id, pm2);

            this.areIdentical(testMap.keySet().length, 2);
            this.areIdentical(testMap.values().length, 2);

        }
    }
}