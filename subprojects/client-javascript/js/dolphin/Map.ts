export module dolphin {
    export class Map<K,V> {

        private keys:K[];
        private data:V[];

        constructor() {
            this.keys = [];
            this.data = [];
        }

        put(key:K, value:V) {
            if (!(this.keys.indexOf(key) > -1)) {
                this.keys.push(key);
            }
            this.data[this.keys.indexOf(key)] = value;
        }

        get(key:K):V {
            return this.data[this.keys.indexOf(key)];
        }

        remove(key:K):void {
            if (this.containsKey(key)) {
                this.data.splice(this.keys.indexOf(key), 1);
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
                handler(this.keys[i], this.data[i]);
            }
        }

        containsKey(key:K):boolean {
            return this.keys.indexOf(key) > -1;
        }

        containsValue(value:V):boolean {
            return this.data.indexOf(value) > -1;
        }

        values():V[] {
            return this.data.slice(0);
        }

        keySet():K[] {
            return this.keys.slice(0);
        }

    }
}