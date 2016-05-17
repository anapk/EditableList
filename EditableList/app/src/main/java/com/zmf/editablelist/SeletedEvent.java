package com.zmf.editablelist;

/**
 * Created by zimengfang on 16/5/4.
 * 删除自选股
 */
public class SeletedEvent {
    private int size;

    public SeletedEvent(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
