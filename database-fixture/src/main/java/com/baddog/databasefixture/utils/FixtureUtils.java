package com.baddog.databasefixture.utils;

import com.baddog.databasefixture.models.VideoDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class FixtureUtils {
  public List<VideoDO> getFixture(String fixture) throws IOException {
    String filename = "fixtures/" + fixture + ".json";
    File file = new ClassPathResource(filename).getFile();
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(file, new TypeReference<>() {});
  }
}
