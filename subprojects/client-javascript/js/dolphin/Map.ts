export module dolphin {
    export class Map<K,V> {

        private keys:K[];
        private values:any;

        constructor() {
            this.keys = new Array;
            this.values = new Object();
        }

        put(key:K, value:V) {
            if (this.values[key] == null) {
                this.keys.push(key);
            }
            this.values[key] = value;
        }

        get(key:K) {
            return this.values[key];
        }

        isEmpty():boolean {
            return this.keys.length == 0;
        }

        length():number {
            return this.keys.length;
        }

        forEach(handler:(key:K, value:V) => void) {
            for (var i = 0; i < this.keys.length; i++) {
                handler(this.keys[i], this.values[this.keys[i]]);
            }
        }

        containsKey(key:K):boolean {
            return this.keys.indexOf(key) > -1;
        }


    }
}