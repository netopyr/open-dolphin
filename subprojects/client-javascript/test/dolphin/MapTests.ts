import tsUnit            = require("../../testsuite/tsUnit")
import ca                = require("../../js/dolphin/ClientAttribute")
import cpm               = require("../../js/dolphin/ClientPresentationModel")
import map               = require("../../js/dolphin/Map")

export module dolphin {
    export class MapTests extends tsUnit.tsUnit.TestClass {

        testMap() {
            var pm1 = new cpm.dolphin.ClientPresentationModel(undefined, "type1");
            var pm2 = new cpm.dolphin.ClientPresentationModel(undefined, "type2");
            var firstAttribute = new ca.dolphin.ClientAttribute("prop1", "qual");
            var secondAttribute = new ca.dolphin.ClientAttribute("prop2", "qual");

            pm1.addAttribute(firstAttribute);
            pm2.addAttribute(secondAttribute);

            var testMap:map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel> = new map.dolphin.Map<string,cpm.dolphin.ClientPresentationModel>();
            testMap.put(pm1.id, pm1);
            testMap.put(pm2.id, pm2);

            this.areIdentical(testMap.length(), 2);
            this.isTrue(testMap.containsKey(pm1.id))
            this.isTrue(testMap.containsKey(pm2.id))

            this.isFalse(testMap.containsKey('dummy'))

            testMap.remove(pm1.id)
            this.isFalse(testMap.containsKey(pm1.id))

            testMap.put(pm1.id, pm1);
            this.isTrue(testMap.containsKey(pm1.id))

            var keys:string[] = [];
            var values:cpm.dolphin.ClientPresentationModel[] = [];
            testMap.forEach((key:string, value:cpm.dolphin.ClientPresentationModel) => {
                keys.push(key);
                values.push(value);
            })

            this.areIdentical(keys.length, 2);
            this.areIdentical(values.length, 2);


        }

    }
}