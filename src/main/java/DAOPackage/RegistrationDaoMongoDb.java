package DAOPackage;

import CryptoPackage.EncryptClass;
import ModelPackage.RegistrationBean;
import UtilityPackage.HandymanClass;
import UtilityPackage.MongoDbConnClass;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sun.istack.internal.Nullable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;

import javax.naming.NamingException;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * This class is the DAO for registration. It is advisable to use the builder
 * to construct the the RegistrationDaoInterface object.
 */
@SuppressWarnings("WeakerAccess")
public class RegistrationDaoMongoDb implements RegistrationDaoInterface {
    private RegistrationBean registrationBean;
    private MongoCollection mongoCollection;

    private String username;
    private String uniqueId;
    private String realFirstname;
    private String realMiddlename;
    private String realLastname;

    private transient String secretQuestion;
    private transient String answer;
    private transient String password;

    private transient byte[] saltedPeanutsPass;
    private transient byte[] saltedPeanutsQuestion;
    private transient byte[] saltedPeanutsAns;


    private EncryptClass aClass;

    /**
     * <h1>Builder has been provided, please use it</h1>
     *
     * @param username       The username that the user inputs
     * @param password       the password typed by the user
     * @param secretQuestion security question typed by the user
     * @param answer         answer to the security question
     */
    public RegistrationDaoMongoDb(String username, String realFirstname, String realMiddlename, String realLastname,
                                  String uniqueId, String password,
                                  String secretQuestion, String answer) throws NamingException {
        this.username = username;
        this.realFirstname = realFirstname;
        this.realMiddlename = realMiddlename;
        this.realLastname = realLastname;
        this.uniqueId = uniqueId;
        this.password = password;
        this.secretQuestion = secretQuestion;
        this.answer = answer;
        this.mongoCollection = MongoDbConnClass.getMongoDoc();
        setData();
    }

    /**
     * <h1>Builder has been provided, please use it</h1>
     */
    public RegistrationDaoMongoDb() {
        this.username = "";
        this.uniqueId = "";
        this.password = "";
        this.answer = "";
        this.secretQuestion = "";
        this.saltedPeanutsAns = null;
        this.saltedPeanutsPass = null;
        this.saltedPeanutsQuestion = null;
        this.registrationBean = null;
        this.mongoCollection = null;
    }

    /**
     * initializes the <code>RegistrationBean</code> instance
     */
    private void setData() {
        this.aClass = new EncryptClass( 2048, 256, 100000 );
        try {
            String key = aClass.hashPassword( this.password );
            this.saltedPeanutsPass = aClass.getMC();

            String question = aClass.hashPassword( this.secretQuestion );
            this.saltedPeanutsQuestion = aClass.getMC();

            String ans = aClass.hashPassword( this.answer );
            this.saltedPeanutsAns = aClass.getMC();

            this.uniqueId = HandymanClass.makeUID( this.username );

            this.registrationBean = new RegistrationBean()
                    .setUsername( this.username )
                    .setRealfirstname( this.realFirstname )
                    .setRealmiddlename( this.realMiddlename )
                    .setReallastname( this.realLastname )
                    .setUniqueid( this.uniqueId )
                    .setPassword( key )
                    .setSecretQuestion( question )
                    .setAnswer( ans )
                    .setSaltedAnswer( saltedPeanutsAns )
                    .setSaltedQuestion( saltedPeanutsQuestion )
                    .setSaltedPassword( saltedPeanutsPass )
                    .createRegistrationBean();

        } catch (NoSuchAlgorithmException e) {
            Logger.getAnonymousLogger().log( Level.SEVERE, "Crypto Algo doesn't exist" + " " + e.getMessage() );
        } catch (InvalidKeySpecException e) {
            Logger.getAnonymousLogger().log( Level.SEVERE, "invalid Key Specification provided" + " " + e.getMessage() );
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean insertIntoDb() {
        Document dataFromMongo = new Document( this.registrationBean.mapClassData() );
        Document tempDoc = new Document( "username", 1 );
        this.mongoCollection.createIndex( tempDoc, new IndexOptions().unique( true ) );
        try {
            this.mongoCollection.insertOne( dataFromMongo );
            return true;
        } catch (com.mongodb.MongoWriteException e) {
            return false;
        }
    }

    /**
     * @param username username provided by user
     * @param password password provided by the user
     * @return true if authenticated else false
     * @throws InvalidKeySpecException Thrown by the Crypto class
     * @throws NoSuchAlgorithmException Thrown by the Crypto class
     */
    @Override
    @SuppressWarnings(value = "unchecked")
    public LinkedHashMap<String, Object> authenticateUser(@NotNull String username, @NotNull String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        LinkedHashMap<String,Object> holderMap = new LinkedHashMap<>();
        this.aClass = new EncryptClass( 2048, 256, 100000 );
        Bson filter = new Document( "username", username );
        List<Document> result = (List<Document>) this.mongoCollection.find( filter ).into( new ArrayList<Document>() );
        if (!result.isEmpty()) {
            Document doc = result.get( 0 );
            Binary namakTemp = doc.get( "pass_salt", org.bson.types.Binary.class );
            byte[] namak = namakTemp.getData();
            String pass = doc.getString( "password" );
            if (aClass.chkPass( password, pass, namak)){

                Bson tokenFilter =  new Document(((Document) filter)).append("clientToken",new Document("$exists",true ))
                        .append("serverToken", new Document("$exists",true)); // client token checking filter
                List<Document> tokenResults = (List<Document>) this.mongoCollection.find(tokenFilter).into(new
                        ArrayList()); // collection to store the filtered results where client token field is present

                if (tokenResults.isEmpty()) {
                    //Set the tokens
                    String loginTokenServer = HandymanClass.makeUID(System.nanoTime()+""+System.currentTimeMillis());
                    String loginTokenClient = HandymanClass.makeUID(System.nanoTime()+""+System.currentTimeMillis());

                    holderMap.put("serverToken", loginTokenServer);
                    holderMap.put("clientToken", loginTokenClient);
                    // Write tokens to persistence
                    Bson temp = new Document("$set", new Document( holderMap ));
                    this.mongoCollection.updateOne(filter, temp);

                    holderMap.put( "username", result.get( 0 ).get( "username" ));
                    holderMap.put( "UUID" , result.get( 0 ).get( "_id" ));
                    holderMap.put( "flag", "new" );
                    holderMap.remove( "serverToken" );
                    return holderMap;
                } else {
                    holderMap.put("flag","logged");
                    return holderMap;
                }
            }
        }
        holderMap.put("flag","NaN");
        return holderMap;
    }


    /**
     * @param username the user's username
     * @param clientToken the unique token of the user which is stored in the user's machine
     * @return true if the username and token match, false otherwise
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public boolean logoutService(@NotNull String username, @NotNull String clientToken) {
        Bson filter = new Document( "username",username ).append( "clientToken" ,clientToken);
        List<Document> result = (List<Document>) this.mongoCollection.find(filter).into(new ArrayList());
        if(!result.isEmpty()){
            Bson actionFilter = new Document( "$unset",new Document( "clientToken",1 ).append( "serverToken",1 ) );
            this.mongoCollection.updateOne( filter,actionFilter );
            return true;
        }
        else return false;
    }


    /**
     * @param UserId         username to check against. It is base64 encoded
     * @param Username       new username
     * @param realFirstName  first name of the user
     * @param realMiddleName middle name of the user(optional)
     * @param realLastName   last name of the user
     * @param Password       new password
     * @param SecretQuestion new secret question
     * @param Answer         new answer to secret question
     * @return True if changes made else false
     * @throws InvalidKeySpecException Thrown by Crypto class
     * @throws NoSuchAlgorithmException Thrown by Crypto class
     */
    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean updateCredentials(@NotNull String UserId, @Nullable String Username,
                                     @Nullable String realFirstName, @Nullable String realMiddleName, @Nullable String realLastName,
                                     @Nullable String Password, @Nullable String SecretQuestion, @Nullable String Answer)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        Bson filter = new Document( "_id", UserId );

        String newUsername = Username == null || Username.isEmpty() ? "" : Username;
        String newFirstname = realFirstName == null || realFirstName.isEmpty() ? "" : realFirstName;
        String newMiddlename = realMiddleName == null || realMiddleName.isEmpty() ? "" : realMiddleName;
        String newLastname = realLastName == null || realLastName.isEmpty() ? "" : realLastName;
        String newPassword = Password == null || Password.isEmpty() ? "" : Password;
        String newSecretQuestion = SecretQuestion == null || SecretQuestion.isEmpty() ? "" : SecretQuestion;
        String newAnswer = Answer == null || Answer.isEmpty() ? "" : Answer;
        Collection<Document> result = this.mongoCollection.find( filter ).into( new ArrayList<Document>() );

        if (!result.isEmpty()) {
            if (!newPassword.isEmpty() || !newPassword.equalsIgnoreCase( "" )) {
                String key = aClass.hashPassword( newPassword );
                byte[] keySalt = aClass.getMC();
                this.mongoCollection.updateOne( filter, combine( set( "password", key ), set( "pass_salt",
                        new Binary( keySalt ) ) ) );
            }

            if (!newFirstname.isEmpty() || !newFirstname.equalsIgnoreCase( "" ))
                this.mongoCollection.updateOne( filter, combine( set( "firstName", newFirstname ) ) );
            if (!newMiddlename.isEmpty() || !newMiddlename.equalsIgnoreCase( "" ))
                this.mongoCollection.updateOne( filter, combine( set( "middleName", newMiddlename ) ) );
            if (!newLastname.isEmpty() || !newLastname.equalsIgnoreCase( "" ))
                this.mongoCollection.updateOne( filter, combine( set( "firstName", newLastname ) ) );

            if (!newSecretQuestion.isEmpty() || !newSecretQuestion.equalsIgnoreCase( "" )) {
                String question = aClass.hashPassword( newSecretQuestion );
                byte[] questionSalt = aClass.getMC();
                this.mongoCollection.updateOne( filter, combine( set( "question", question ), set( "question_salt",
                        new Binary( questionSalt ) ) ) );
            }
            if (!newAnswer.isEmpty() || !newAnswer.equalsIgnoreCase( "" )) {
                String ans = aClass.hashPassword( newAnswer );
                byte[] answerSalt = aClass.getMC();
                this.mongoCollection.updateOne( filter, combine( set( "answer", ans ), set( "answer_salt",
                        new Binary( answerSalt ) ) ) );
            }

            if (!newUsername.isEmpty() || !newUsername.equalsIgnoreCase( "" )) {
                this.mongoCollection.updateOne( filter, set( "username", newUsername ) );
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * @param userID unique ID of the user
     * @param password user's password
     * @return true if UUID is changed, false otherwise
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public boolean changeUniqueid(@NotNull String userID, @NotNull String password) {
        Bson filter = new Document( "_id", userID );
        Document tempDoc = new Document( "username", 1 );
        List<Document> result = new ArrayList<>( this.mongoCollection.find( filter ).into( new ArrayList<Document>() ) );
        if (!result.isEmpty()) {
            String newUUID = HandymanClass.makeUID(userID);
            Document tempDoc2 = result.get(0).append( "_id", newUUID );
            this.mongoCollection.dropIndex(new Document("username", 1)); // or this.mongoCollection.dropIndex("username_1");
            this.mongoCollection.deleteOne( filter );
            this.mongoCollection.createIndex( tempDoc, new IndexOptions().unique( true ) );
            this.mongoCollection.insertOne( tempDoc2 );
            return true;
        }
        return false;
    }


    @Override
    public RegistrationDaoInterface setRealfirstname(String realFirstname) {
        this.realFirstname = realFirstname;
        return this;
    }

    @Override
    public RegistrationDaoInterface setRealmiddlename(String realMiddlename) {
        this.realMiddlename = realMiddlename;
        return this;
    }

    @Override
    public RegistrationDaoInterface setReallastname(String realLastname) {
        this.realLastname = realLastname;
        return this;
    }

    @Override
    public RegistrationDaoInterface setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public RegistrationDaoInterface setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public RegistrationDaoInterface setUniqueid(String uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    @Override
    public RegistrationDaoInterface createRegistrationDAO() throws NamingException {
        return new RegistrationDaoMongoDb( this.username, this.realFirstname,
                this.realMiddlename, this.realLastname,
                this.uniqueId, this.password,
                this.secretQuestion, this.answer );
    }

    @Override
    public RegistrationDaoInterface setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
        return this;
    }

    @Override
    public RegistrationDaoInterface setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

}