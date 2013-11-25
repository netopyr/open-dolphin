export module dolphin {

    export class Codec {

        encode(commands:any) {
            return JSON.stringify(commands);
        }

        decode(transmitted:any) {
            if (typeof transmitted == 'string') {
                return JSON.parse(transmitted);
            } else {
                return transmitted;
            }
        }
    }

}