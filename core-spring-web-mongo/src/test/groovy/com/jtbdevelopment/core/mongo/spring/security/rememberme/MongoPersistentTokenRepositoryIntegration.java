package com.jtbdevelopment.core.mongo.spring.security.rememberme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.core.mongo.spring.AbstractMongoDefaultSpringContextIntegration;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import groovy.lang.Reference;
import java.util.Date;
import java.util.function.Consumer;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 1/3/2015 Time: 9:51 PM
 *
 * Loosely based on spring's own JdbcTokenRepositoryImplTests
 */
public class MongoPersistentTokenRepositoryIntegration extends
    AbstractMongoDefaultSpringContextIntegration {

  private static final String USER_COLUMN = "username";
  private static final String SERIES_COLUMN = "series";
  private static final String TOKEN_COLUMN = "tokenValue";
  private static final String DATE_COLUMN = "date";
  private static final String TOKEN_COLLECTION_NAME = "rememberMeToken";
  private static MongoPersistentTokenRepository repository;
  private MongoCollection collection;

  private static Bson queryBySeries(String series) {
    return Filters.eq(SERIES_COLUMN, series);
  }

  private static void compareTokenToMongo(Document result, PersistentRememberMeToken token) {
    assertEquals(token.getUsername(), result.get(USER_COLUMN));
    assertEquals(token.getSeries(), result.get(SERIES_COLUMN));
    assertEquals(token.getTokenValue(), result.get(TOKEN_COLUMN));
    assertEquals(token.getDate(), result.get(DATE_COLUMN));
  }

  @Before
  public void setup() {
    repository = context.getBean(MongoPersistentTokenRepository.class);
    collection = db.getCollection(TOKEN_COLLECTION_NAME);
  }

  @Test
  public void testCollectionConfiguration() {
    final Reference<Boolean> seriesIndexFound = new Reference<>(false);
    collection.listIndexes().forEach((Consumer<Document>) document -> {
      final String name = (String) document.get("name");
      if (SERIES_COLUMN.equals(name)) {
        seriesIndexFound.set(true);
        assertTrue((boolean) document.get("unique"));
        Document key = (Document) document.get("key");
        assertEquals(1, key.size());
        assertEquals(1, key.get(SERIES_COLUMN));
      }
    });
    assertTrue(seriesIndexFound.get());
  }

  @Test
  public void testNewSeries() {
    MongoCollection collection = db.getCollection(TOKEN_COLLECTION_NAME);
    PersistentRememberMeToken token = new PersistentRememberMeToken("newuser", "newSeries",
        "newToken", new Date());
    repository.createNewToken(token);

    Document result = (Document) collection.find(queryBySeries(token.getSeries())).first();
    assertNotNull(result.get("_id"));
    compareTokenToMongo(result, token);
  }

  @Test
  public void testRetrievesExistingSeries() {
    Date date = new Date();
    String user = "finduser";
    String series = "findseries";
    String value = "findValue";

    collection.insertOne(new Document().append(USER_COLUMN, user).append(SERIES_COLUMN, series)
        .append(TOKEN_COLUMN, value).append(DATE_COLUMN, date));

    MongoRememberMeToken loaded = repository.getTokenForSeries(series);

    assertNotNull(loaded.getId());
    assertEquals(date, loaded.getDate());
    assertEquals(user, loaded.getUsername());
    assertEquals(series, loaded.getSeries());
    assertEquals(value, loaded.getTokenValue());
  }

  @Test
  public void testRemovingUserTokensDeletesData() {
    Date date = new Date();
    String user = "deleteuser";

    MongoCollection collection = db.getCollection(TOKEN_COLLECTION_NAME);
    collection.insertOne(
        new Document().append(USER_COLUMN, user).append(SERIES_COLUMN, "deleteSeries1")
            .append(TOKEN_COLUMN, "deleteValue1").append(DATE_COLUMN, date));
    collection.insertOne(
        new Document().append(USER_COLUMN, user).append(SERIES_COLUMN, "deleteSeries2")
            .append(TOKEN_COLUMN, "deleteValue2").append(DATE_COLUMN, date));

    Bson query = Filters.eq(USER_COLUMN, user);
    assertEquals(2, collection.count(query));

    repository.removeUserTokens(user);
    assertEquals(0, collection.count(query));
  }

  @Test
  public void testUpdatingTokenModifiesTokenValueAndLastUsed() {
    Date date = new Date(0);
    String user = "updateUser";
    String series = "updateSeries";
    String value = "updateValue";
    String newValue = "updateValue2";
    Date newDate = new Date();

    PersistentRememberMeToken initialToken = new PersistentRememberMeToken(user, series, value,
        date);
    PersistentRememberMeToken updateToken = new PersistentRememberMeToken(user, series, newValue,
        newDate);

    repository.createNewToken(initialToken);
    Document result = (Document) collection.find(queryBySeries(series)).first();
    assertNotNull(ReflectionTestUtils.invokeMethod(result, "get", "_id"));
    compareTokenToMongo(result, initialToken);
    repository.updateToken(series, newValue, newDate);
    result = (Document) collection.find(queryBySeries(series)).first();
    assertNotNull(ReflectionTestUtils.invokeMethod(result, "get", "_id"));
    compareTokenToMongo(result, updateToken);
  }

  @Test
  public void testUpdatingTokenWithNoSeries() {
    String series = "updateSeries";
    String newValue = "updateValue2";
    Date newDate = new Date();

    assertEquals(0, collection.count(queryBySeries(series)));
    repository.updateToken(series, newValue, newDate);
    assertEquals(0, collection.count(queryBySeries(series)));
  }
}
