/*
 * Created on Sat Aug 07 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce;

public class RemapInfo {

    private byte[] data;

    private String fromName;
    private String toName;

    public RemapInfo(byte[] data, String fromName, String toName) {
        this.data = data;
        this.fromName = fromName;
        this.toName = toName;
    }

    public byte[] getData() {
        return data;
    }

    public String getFromName() {
        return fromName;
    }

    public String getToName() {
        return toName;
    }
    
}
