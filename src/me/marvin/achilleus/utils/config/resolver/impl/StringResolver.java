package me.marvin.achilleus.utils.config.resolver.impl;

import me.marvin.achilleus.utils.config.resolver.ConfigResolver;

public class StringResolver implements ConfigResolver<String> {
    @Override
    public String resolve(Object value) {
        return value.toString();
    }
}
