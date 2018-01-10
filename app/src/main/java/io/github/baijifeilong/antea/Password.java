package io.github.baijifeilong.antea;

import java.io.Serializable;

/**
 * Created by bj
 * on 18-1-9.
 */

class Password implements Serializable {
    Password() {
    }

    Password(int id, String name, String value, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.isDefault = isDefault;
    }

    int id;
    String name;
    String value;
    boolean isDefault;
}
