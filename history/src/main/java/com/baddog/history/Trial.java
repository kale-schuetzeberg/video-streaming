package com.baddog.history;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Trial {
  @GetMapping("/test")
  public String getString() {
    return "I'm commin in hot!!!";
  }
}
