package UtilityPackage;

import javax.naming.NamingException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MongoDbConnClassTest {
    private String dbName, coll, host;
    private int port;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        dbName = "video";
        coll = "users";
        host = " cluster0-shard-00-02-tadu3.mongodb.net";
        port = 27017;
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void getMongoDoc() throws NamingException {
        assertNotNull( MongoDbConnClass.getMongoDoc() );
    }


}