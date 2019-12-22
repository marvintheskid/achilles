package me.marvin.achilles.utils.config.resolver.impl;

import me.marvin.achilles.utils.config.resolver.ConfigResolver;

public class StringResolver implements ConfigResolver<String> {
    @Override
    public String resolve(Object value) {
        return value.toString();
    }
}
