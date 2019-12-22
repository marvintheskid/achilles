package me.marvin.achilles.utils.config.resolver.impl;

import me.marvin.achilles.utils.config.resolver.ConfigResolver;

public class IntegerResolver implements ConfigResolver<Integer> {
    @Override
    public Integer resolve(Object value) {
        return Integer.valueOf(value.toString());
    }
}
