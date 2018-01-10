package io.github.baijifeilong.antea;

/**
 * Created by bj
 * on 18-1-10.
 */

class Triple<T, U, V> {

    final T first;
    final U second;
    final V third;

    Triple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
