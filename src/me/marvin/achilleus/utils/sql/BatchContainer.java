package me.marvin.achilleus.utils.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BatchContainer {
    private Object[] objects;
}
