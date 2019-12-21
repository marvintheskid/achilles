package me.marvin.achilleus.utils.config.resolver.impl;

import me.marvin.achilleus.utils.config.resolver.ConfigResolver;

public class IntegerResolver implements ConfigResolver<Integer> {
    @Override
    public Integer resolve(Object value) {
        return Integer.valueOf(value.toString());
    }
}
