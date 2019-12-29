package me.marvin.achilles.messenger;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private MessageType type;
    private String data;
}
