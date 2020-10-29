package com.example.myapplication.entity;

import java.io.Serializable;

public class NewsEntity implements Serializable {
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
