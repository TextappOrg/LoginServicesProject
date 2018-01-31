package ModelPackage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegistrationBeanTest {

    RegistrationBean bean;
    SecureRandom rand;
    byte b[];
    Object objectArray[];
    String uname, pass, ans, que;

    @BeforeEach
    void setUp() {
        rand = new SecureRandom();
        b = new byte[10];
        rand.nextBytes( b );
        uname = "uname";
        pass = "pass";
        ans = "ans";
        que = "que";

        objectArray = new Object[]{uname, pass, b, que, b, ans, que};

        bean = new RegistrationBean().setPassword( pass ).setSaltedPassword( b ).setUsername( uname ).setAnswer( ans )
                .setSaltedAnswer( b ).setSecretQuestion( que ).setSaltedQuestion( b ).createRegistrationBean();
    }


    @Test
    void mapClassData() {
        assertNotNull( bean.mapClassData() );
    }

    @Test
    void checkIsEqual() {
        //assertEquals(bean.mapClassData().values().toArray(),objectArray);
        assertEquals( uname, bean.mapClassData().get( "username" ) );
        assertEquals( pass, bean.mapClassData().get( "password" ) );
        assertEquals( ans, bean.mapClassData().get( "answer" ) );
        assertEquals( que, bean.mapClassData().get( "question" ) );
        assertEquals( b, bean.mapClassData().get( "pass_salt" ) );
        assertEquals( b, bean.mapClassData().get( "question_salt" ) );
        assertEquals( b, bean.mapClassData().get( "answer_salt" ) );
    }
}