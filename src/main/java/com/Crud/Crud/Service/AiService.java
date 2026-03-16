package com.Crud.Crud.Service;

import com.Crud.Crud.Tools.ProductTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service

public class AiService {
    private final ChatClient chatClient;
    private final ProductTools productTools;

    public AiService(ChatClient chatClient, ProductTools productTools) {
        this.chatClient = chatClient;
        this.productTools = productTools;
    }

    public String askAi(String question) {
        return chatClient.prompt()
                .system("You are a product assistant. Use tools when needed.")
                .user(question)
                .tools(productTools)
                .call()
                .content();
    }
}
