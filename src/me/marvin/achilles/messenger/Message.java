package me.marvin.achilles.messenger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Message {
    private MessageType type;
    private String data;
}
