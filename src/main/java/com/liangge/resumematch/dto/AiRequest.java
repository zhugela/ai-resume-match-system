package com.liangge.resumematch.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AiRequest {
    private String model;
    private List<Message> messages;
    
    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
