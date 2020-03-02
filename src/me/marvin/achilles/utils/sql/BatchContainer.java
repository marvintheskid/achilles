package me.marvin.achilles.utils.sql;

import lombok.Getter;

@Getter
public class BatchContainer {
    private Object[] objects;

    public BatchContainer(Object... objects) {
        this.objects = objects;
    }
}
