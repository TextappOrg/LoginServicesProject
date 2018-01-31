package DAOPackage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.NamingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationDaoMongoDbInterfaceTest {

    RegistrationDaoInterface RD;
    String uname, pass, ans, que, fn, mn, ln;

    @BeforeEach
    void setUp() throws NamingException {
        uname = pass = ans = que = fn = ln = mn = "dummy";

        RD = new RegistrationDaoMongoDb()
                .setUsername( uname )
                .setRealfirstname( fn )
                .setRealmiddlename( mn )
                .setRealfirstname( ln )
                .setUniqueid( uname )
                .setPassword( pass )
                .setAnswer( ans )
                .setSecretQuestion( que )
                .createRegistrationDAO();
    }

    @Test
    void writeToDb() {
        assertTrue( RD.insertIntoDb() );
    }

    @Test
    void checkFromDb() {
        try {
            assertTrue( RD.authenticateUser( uname, pass ) );
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateParams() {
        try {
            assertTrue( RD.updateCredentials( uname, "Dummy2", null, null, null, "Bablu", "", "" ) );
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @Test
    void reCheckFromDb() {
        try {
            assertTrue( RD.authenticateUser( "Dummy2", "Bablu" ) );
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}