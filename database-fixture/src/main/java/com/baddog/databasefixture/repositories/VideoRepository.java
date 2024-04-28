package com.baddog.databasefixture.repositories;

import com.baddog.databasefixture.models.VideoDO;
import com.baddog.databasefixture.utils.FixtureUtils;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoRepository {
  private final MongoTemplate mongoTemplate;
  private final FixtureUtils fixtureUtils;

  public void dropCollection(String collection) {
    mongoTemplate.dropCollection(collection);
  }

  public void loadFixture(String fixture, String collection) throws IOException {
    List<VideoDO> videos = fixtureUtils.getFixture(fixture);
    videos.forEach(videoDO -> mongoTemplate.save(videoDO, collection));
  }

  public void unloadFixture(String fixture, String collection) throws IOException {
    List<VideoDO> videos = fixtureUtils.getFixture(fixture);
    videos.forEach(videoDO -> mongoTemplate.remove(videoDO, collection));
  }
}
