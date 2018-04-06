package com.jtbdevelopment.core.mongo.spring.security.rememberme

import com.jtbdevelopment.core.mongo.spring.AbstractMongoDefaultSpringContextIntegration
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.conversions.Bson
import org.junit.Before
import org.junit.Test
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken

import java.util.function.Consumer

/**
 * Date: 1/3/2015
 * Time: 9:51 PM
 *
 * Loosely based on spring's own JdbcTokenRepositoryImplTests
 *
 */
class MongoPersistentTokenRepositoryIntegration extends AbstractMongoDefaultSpringContextIntegration {

    static final String USER_COLUMN = 'username'
    static final String SERIES_COLUMN = 'series'
    static final String TOKEN_COLUMN = 'tokenValue'
    static final String DATE_COLUMN = 'date'
    static final String TOKEN_COLLECTION_NAME = 'rememberMeToken'

    protected static MongoPersistentTokenRepository repository
    private MongoCollection collection

    @Before
    void setup() {
        repository = context.getBean(MongoPersistentTokenRepository.class)
        collection = db.getCollection(TOKEN_COLLECTION_NAME)
    }

    @Test
    void testCollectionConfiguration() {
        boolean seriesIndexFound = false
        collection.listIndexes().forEach(new Consumer<Document>() {
            @Override
            void accept(Document it) {
                switch (it.get('name')) {
                    case SERIES_COLUMN:
                        seriesIndexFound = true
                        assert it.get('unique') == Boolean.TRUE
                        BasicDBObject key = it.get('key') as BasicDBObject
                        assert key.size() == 1
                        assert key.get(SERIES_COLUMN) == 1
                        break
                }
            }
        })
        assert seriesIndexFound
    }

    @Test
    void testNewSeries() {
        MongoCollection collection = db.getCollection(TOKEN_COLLECTION_NAME)
        PersistentRememberMeToken token = new PersistentRememberMeToken('newuser', 'newSeries', 'newToken', new Date())
        repository.createNewToken(token)

        Document result = collection.find(queryBySeries(token.series)).first()
        assert result.get('_id')
        compareTokenToMongo(result, token)
    }

    @Test
    void testRetrievesExistingSeries() {
        def date = new Date()
        def user = 'finduser'
        def series = 'findseries'
        def value = 'findValue'

        collection.insertOne(new Document().
                append(USER_COLUMN, user).
                append(SERIES_COLUMN, series).
                append(TOKEN_COLUMN, value).
                append(DATE_COLUMN, date))

        def loaded = repository.getTokenForSeries(series)

        assert loaded.id
        assert date == loaded.date
        assert user == loaded.username
        assert series == loaded.series
        assert value == loaded.tokenValue
    }

    @Test
    void testRemovingUserTokensDeletesData() {
        def date = new Date()
        def user = 'deleteuser'

        MongoCollection collection = db.getCollection(TOKEN_COLLECTION_NAME)
        collection.insertOne(new Document().
                append(USER_COLUMN, user).
                append(SERIES_COLUMN, 'deleteSeries1').
                append(TOKEN_COLUMN, 'deleteValue1').
                append(DATE_COLUMN, date))
        collection.insertOne(new Document().
                append(USER_COLUMN, user).
                append(SERIES_COLUMN, 'deleteSeries2').
                append(TOKEN_COLUMN, 'deleteValue2').
                append(DATE_COLUMN, date))

        def query = Filters.eq(USER_COLUMN, user)
        assert collection.count(query) == 2

        repository.removeUserTokens(user)
        assert collection.count(query) == 0
    }

    @Test
    void testUpdatingTokenModifiesTokenValueAndLastUsed() {
        def date = new Date(0)
        def user = 'updateUser'
        def series = 'updateSeries'
        def value = 'updateValue'
        def newValue = 'updateValue2'
        def newDate = new Date()

        PersistentRememberMeToken initialToken = new PersistentRememberMeToken(user, series, value, date)
        PersistentRememberMeToken updateToken = new PersistentRememberMeToken(user, series, newValue, newDate)

        repository.createNewToken(initialToken)
        def result = collection.find(queryBySeries(series)).first()
        assert result.get('_id')
        compareTokenToMongo(result, initialToken)
        repository.updateToken(series, newValue, newDate)
        result = collection.find(queryBySeries(series)).first()
        assert result.get('_id')
        compareTokenToMongo(result, updateToken)
    }

    @Test
    void testUpdatingTokenWithNoSeries() {
        def series = 'updateSeries'
        def newValue = 'updateValue2'
        def newDate = new Date()

        assert collection.count(queryBySeries(series)) == 0
        repository.updateToken(series, newValue, newDate)
        assert collection.count(queryBySeries(series)) == 0
    }

    private static Bson queryBySeries(String series) {
        Filters.eq(SERIES_COLUMN, series)
    }

    private static void compareTokenToMongo(Document result, PersistentRememberMeToken token) {
        assert result.get(USER_COLUMN).equals(token.username)
        assert result.get(SERIES_COLUMN).equals(token.series)
        assert result.get(TOKEN_COLUMN).equals(token.tokenValue)
        assert result.get(DATE_COLUMN).equals(token.date)
    }

}
