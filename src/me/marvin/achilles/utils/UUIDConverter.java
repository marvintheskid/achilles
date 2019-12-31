package me.marvin.achilles.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDConverter {
    public static byte[] to(UUID uuid) {
        if (uuid == null) return null;
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    public static UUID from(byte[] array) {
        if (array == null) return null;
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
