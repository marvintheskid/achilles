package me.marvin.achilles.utils.config.resolver;

public interface ConfigResolver<T> {
    T resolve(Object value);
}
