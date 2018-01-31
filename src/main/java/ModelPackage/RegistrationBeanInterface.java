package ModelPackage;

import javax.validation.constraints.NotNull;

public interface RegistrationBeanInterface extends java.io.Serializable {
    RegistrationBeanInterface setUsername(@NotNull String username);

    RegistrationBeanInterface setUniqueid(@NotNull String uniqueId);

    RegistrationBeanInterface setRealfirstname(@NotNull String realFirstname);

    RegistrationBeanInterface setRealmiddlename(@NotNull String realMiddlename);

    RegistrationBeanInterface setReallastname(@NotNull String realLastname);

    RegistrationBeanInterface setSecretQuestion(@NotNull String secretQuestion);

    RegistrationBeanInterface setSaltedQuestion(@NotNull byte[] saltedQuestion);

    RegistrationBeanInterface setAnswer(@NotNull String answer);

    RegistrationBeanInterface setSaltedAnswer(@NotNull byte[] saltedAnswer);

    RegistrationBeanInterface setPassword(@NotNull String password);

    RegistrationBeanInterface setSaltedPassword(@NotNull byte[] saltedPassword);

    @NotNull
    RegistrationBean createRegistrationBean();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
