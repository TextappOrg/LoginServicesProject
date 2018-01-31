package UtilityPackage;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Statically creates an instance of MongoCollection object
 */

public class MongoDbConnClass {

    /**
     * @return <code>MongoCollection</code>
     */
    public static MongoCollection getMongoDoc() throws NamingException {
        InitialContext ctx = new InitialContext();
        Properties prop = (Properties) ctx.lookup( "RestServiceAuthMongoDbConnDetails" );
        String connectionStr = prop.getProperty( "Connection_String" );
        String dbName = prop.getProperty( "Database_Name" );
        String collectionName = prop.getProperty( "Collection_Name" );
        MongoDatabase database;
        MongoClient mongoClient = new MongoClient( new MongoClientURI( connectionStr ) );
        database = mongoClient.getDatabase( dbName );
        return database.getCollection( collectionName );
    }
}
