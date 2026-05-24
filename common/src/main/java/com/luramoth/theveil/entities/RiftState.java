package com.luramoth.theveil.entities;

import java.util.HashMap;
import java.util.Map;

public enum RiftState {
    OPENING(0),
    ACTIVE(1),
    CLOSING(2);

    private int value;
    private static Map map = new HashMap<>();
    RiftState(int value) {
        this.value = value;
    }

    static {
        for (RiftState riftState : RiftState.values()){
            map.put(riftState.value, riftState);
        }
    }

    public static RiftState valueOf(int riftState) {
        return (RiftState) map.get(riftState);
    }

    public int  getValue() {
        return value;
    }
}
