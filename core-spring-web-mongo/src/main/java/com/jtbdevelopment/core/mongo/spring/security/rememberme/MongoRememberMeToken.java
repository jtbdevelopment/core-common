package com.jtbdevelopment.core.mongo.spring.security.rememberme;

import com.jtbdevelopment.core.spring.security.rememberme.AbstractRememberMeToken;
import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * Date: 12/26/14 Time: 9:03 AM
 */
@Document(collection = "rememberMeToken")
@CompoundIndexes({@CompoundIndex(name = "series", unique = true, def = "{'series':1}")})
public class MongoRememberMeToken extends AbstractRememberMeToken<ObjectId> {

  @Id
  private ObjectId id;

  @PersistenceConstructor
  public MongoRememberMeToken(final String username, final String series, final String tokenValue,
      final Date date, final ObjectId id) {
    super(username, series, tokenValue, date);
    this.id = id;
  }

  public MongoRememberMeToken(final PersistentRememberMeToken persistentRememberMeToken) {
    this(persistentRememberMeToken.getUsername(),
        persistentRememberMeToken.getSeries(),
        persistentRememberMeToken.getTokenValue(),
        persistentRememberMeToken.getDate(),
        null);
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }
}
