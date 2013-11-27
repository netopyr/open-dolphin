export module dolphin {
    export class Map<K,V> {

        private keys:K[];
        private data:any;

        constructor() {
            this.keys = new Array;
            this.data = new Object();
        }

        put(key:K, value:V) {
            if (this.data[key] == null) {
                this.keys.push(key);
            }
            this.data[key] = value;
        }

        get(key:K) {
            return this.data[key];
        }

        remove(key:K) {
            if (this.containsKey(key)) {
                this.data[key] = undefined;
                this.keys.splice(this.keys.indexOf(key), 1);
            }
        }

        isEmpty():boolean {
            return this.keys.length == 0;
        }

        length():number {
            return this.keys.length;
        }

        forEach(handler:(key:K, value:V) => void) {
            for (var i = 0; i < this.keys.length; i++) {
                handler(this.keys[i], this.data[this.keys[i]]);
            }
        }

        containsKey(key:K):boolean {
            return this.keys.indexOf(key) > -1;
        }

        values():V[] {
            var valueArr:V[] = [];
            for (var i = 0; i < this.keys.length; i++) {
                valueArr.push(this.data[this.keys[i]]);
            }
            return valueArr;
        }

        keySet():K[] {
            return this.keys;
        }

    }
}