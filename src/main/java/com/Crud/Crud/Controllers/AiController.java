package com.Crud.Crud.Controllers;

import com.Crud.Crud.Service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {
    private final AiService aiService;

    @PostMapping("/assistant")
    public String assistant(@RequestBody String question) {

        return aiService.askAi(question);

    }

}
