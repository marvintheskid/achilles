package me.marvin.achilleus.utils.config.resolver.impl;

import me.marvin.achilleus.utils.config.resolver.ConfigResolver;

public class LongResolver implements ConfigResolver<Long> {
    @Override
    public Long resolve(Object value) {
        return Long.valueOf(value.toString());
    }
}
