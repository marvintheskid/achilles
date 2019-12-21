package me.marvin.achilleus.utils.config.resolver;

public interface ConfigResolver<T> {
    T resolve(Object value);
}
