package ModelPackage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
@XmlRootElement
public class RegistrationBean implements RegistrationBeanInterface {

    private static final long serialVersionUID = 6669991911L;
    @XmlElement
    private String username;
    @XmlElement
    private String uniqueId;
    @XmlElement
    private String realFirstname;
    @XmlElement
    private String realMiddlename;
    @XmlElement
    private String realLastname;
    private transient String secretQuestion;
    private transient byte[] saltedQuestion;
    private transient String answer;
    private transient byte[] saltedAnswer;
    private transient String password;
    private transient byte[] saltedPassword;

    /**
     * @param username
     * @param secretQuestion
     * @param saltedQuestion
     * @param answer
     * @param saltedAnswer
     * @param password
     * @param saltedPassword
     */
    @SuppressWarnings( value = "javadoc")
    public RegistrationBean(String username, String realFirstname,
                            String realMiddlename, String realLastname, String uniqueId,
                            String secretQuestion, byte[] saltedQuestion,
                            String answer, byte[] saltedAnswer, String password,
                            byte[] saltedPassword) {
        this.username = username;
        this.uniqueId = uniqueId;
        this.realFirstname = realFirstname;
        this.realMiddlename = realMiddlename;
        this.realLastname = realLastname;
        this.secretQuestion = secretQuestion;
        this.saltedQuestion = saltedQuestion;
        this.answer = answer;
        this.saltedAnswer = saltedAnswer;
        this.password = password;
        this.saltedPassword = saltedPassword;
    }

    /**
     * Default constructor which initializes everything with empty values
     */
    public RegistrationBean() {
        this.username = "";
        realFirstname = "";
        realMiddlename = "";
        realLastname = "";
        this.uniqueId = "";
        this.answer = "";
        this.secretQuestion = "";
        this.password = "";
        this.saltedAnswer = null;
        this.saltedQuestion = null;
        this.saltedPassword = null;
    }

    @Override
    public RegistrationBeanInterface setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public RegistrationBeanInterface setUniqueid(String uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    @Override
    public RegistrationBeanInterface setRealfirstname(String realFirstname) {
        this.realFirstname = realFirstname;
        return this;
    }

    @Override
    public RegistrationBeanInterface setRealmiddlename(String realMiddlename) {
        this.realMiddlename = realMiddlename;
        return this;
    }

    @Override
    public RegistrationBeanInterface setReallastname(String realLastname) {
        this.realLastname = realLastname;
        return this;
    }

    @Override
    public RegistrationBeanInterface setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
        return this;
    }

    @Override
    public RegistrationBeanInterface setSaltedQuestion(byte[] saltedQuestion) {
        this.saltedQuestion = saltedQuestion;
        return this;
    }

    @Override
    public RegistrationBeanInterface setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    @Override
    public RegistrationBeanInterface setSaltedAnswer(byte[] saltedAnswer) {
        this.saltedAnswer = saltedAnswer;
        return this;
    }

    @Override
    public RegistrationBeanInterface setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public RegistrationBeanInterface setSaltedPassword(byte[] saltedPassword) {
        this.saltedPassword = saltedPassword;
        return this;
    }

    @Override
    public RegistrationBean createRegistrationBean() {
        return new RegistrationBean( this.username,
                this.realFirstname, this.realMiddlename, this.realLastname,
                this.uniqueId, this.secretQuestion, this.saltedQuestion,
                this.answer, this.saltedAnswer,
                this.password, this.saltedPassword );
    }

    public LinkedHashMap<String, Object> mapClassData() {
        LinkedHashMap<String, Object> holder = new LinkedHashMap<>();
        holder.put( "_id", this.uniqueId );
        holder.put( "username", this.username );
        holder.put( "password", this.password );
        holder.put( "firstName", this.realFirstname );
        holder.put( "middleName", this.realMiddlename );
        holder.put( "lastName", this.realLastname );
        holder.put( "pass_salt", this.saltedPassword );
        holder.put( "question", this.secretQuestion );
        holder.put( "question_salt", this.saltedQuestion );
        holder.put( "answer", this.answer );
        holder.put( "answer_salt", this.saltedAnswer );
        return holder;
    }

    public String getUsername() {
        return username;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getRealFirstname() {
        return realFirstname;
    }

    public String getRealMiddlename() {
        return realMiddlename;
    }

    public String getRealLastname() {
        return realLastname;
    }

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public byte[] getSaltedQuestion() {
        return saltedQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public byte[] getSaltedAnswer() {
        return saltedAnswer;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getSaltedPassword() {
        return saltedPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistrationBean)) return false;
        RegistrationBean that = (RegistrationBean) o;
        return username.equals( that.username ) &&
                secretQuestion.equals( that.secretQuestion ) &&
                Arrays.equals( saltedQuestion, that.saltedQuestion ) &&
                answer.equals( that.answer ) &&
                Arrays.equals( saltedAnswer, that.saltedAnswer ) &&
                password.equals( that.password ) &&
                Arrays.equals( saltedPassword, that.saltedPassword );
    }

    @Override
    public int hashCode() {

        int result = Objects.hash( username, secretQuestion, answer, password );
        result = 31 * result + Arrays.hashCode( saltedQuestion );
        result = 31 * result + Arrays.hashCode( saltedAnswer );
        result = 31 * result + Arrays.hashCode( saltedPassword );
        return result;
    }
}
