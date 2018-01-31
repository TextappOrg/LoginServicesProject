package ControllerPackage;

import DAOPackage.RegistrationDaoInterface;
import DAOPackage.RegistrationDaoMongoDb;
import com.sun.istack.internal.Nullable;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Someone please take care of the exception handling for NamingException
 */

@WebServlet("/User")
@Path("/User")
public class RegistrationController {

    private RegistrationDaoInterface registrationDaoInstanceMongoDb;

    public RegistrationController() throws NamingException {
        this.registrationDaoInstanceMongoDb = new RegistrationDaoMongoDb();

    }

    /**
     * @param username         username
     * @param password         password
     * @param securityQuestion security question asked by the user
     * @param answer           answer to the security question
     * @return HTTP status code 200 if successful or 500 if error occurs
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/Register")
    public Response registerUser(@NotNull @FormParam("username") String username,
                                 @NotNull @FormParam("firstName") String firstName,
                                 @Nullable @FormParam("middleName") String middleName,
                                 @NotNull @FormParam("lastName") String lastName,
                                 @NotNull @FormParam("password") String password,
                                 @NotNull @FormParam( "confirmPassword" ) String confirmPassword,
                                 @NotNull @FormParam("securityQuestion") String securityQuestion,
                                 @NotNull @FormParam("answer") String answer) throws NamingException {
        if(!confirmPassword.equals( password )) return Response.status( 401 ).build();
        else {
            this.registrationDaoInstanceMongoDb = new RegistrationDaoMongoDb()
                    .setUsername( username )
                    .setRealfirstname( firstName )
                    .setRealmiddlename( middleName == null || middleName.isEmpty() ? "" : middleName )
                    .setReallastname( lastName )
                    .setUniqueid( username )
                    .setPassword( password )
                    .setSecretQuestion( securityQuestion )
                    .setAnswer( answer )
                    .createRegistrationDAO();

            return (this.registrationDaoInstanceMongoDb.insertIntoDb())
                    ? Response.status(200).build() : Response.status(406).build();
        }
    }

    /**
     * @param username
     * @param password
     * @return HTTP Status code 200 if successful, 401 if wrong credentials or 500 if some server error specified by
     * the exception. Check logs
     */
    @POST
    @Path("/Login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response loginUser(@NotNull @FormParam("username") String username,
                              @NotNull @FormParam("password") String password) throws NamingException {
        try {
            return this.registrationDaoInstanceMongoDb.createRegistrationDAO().authenticateUser( username, password ) ? Response.status( 200
            ).build() :
                    Response.status( 401 ).build();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            Logger.getAnonymousLogger().log( Level.SEVERE, e.getMessage() );
            return Response.status( 500 ).build();
        }
    }

    /**
     * @param userId           UUID of the user
     * @param username         username
     * @param password         password
     * @param securityQuestion security question
     * @param answer           answer to the security question
     * @return HTTP status code 200 if successful, 422 if credentials are wrong or 500 if some server error occurs.
     * Check log to track exceptions.
     */
    @PUT
    @Path("/{id}/Reset")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response resetCredentials(@NotNull @PathParam("id") String userId,
                                     @Nullable @FormParam("username") String username,
                                     @Nullable @FormParam("firstName") String firstName,
                                     @Nullable @FormParam("middleName") String middleName,
                                     @Nullable @FormParam("lastName") String lastName,
                                     @Nullable @FormParam("password") String password,
                                     @Nullable @FormParam( "confirmPassword" ) String confirmPassword,
                                     @Nullable @FormParam("securityQuestion") String securityQuestion,
                                     @Nullable @FormParam("answer") String answer) throws NamingException {
        if(!confirmPassword.equals( password )) return Response.status( 401 ).build();
        else {
            try {
                return this.registrationDaoInstanceMongoDb.createRegistrationDAO().updateCredentials( userId, username, firstName, middleName, lastName, password, securityQuestion, answer )
                        ? Response.status( 200 ).build() : Response.status( 406 ).build();
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                Logger.getAnonymousLogger().log( Level.SEVERE, e.getMessage() );
                return Response.status( 500 ).build();
            }
        }
    }

    /**
     * @param Uid User UUID
     * @return HTTP status code 200 if successful, 401 otherwise
     */
    @PUT
    @Path("/ResetUID")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response resetUid(@NotNull @FormParam("UID") String Uid) throws NamingException {
        return this.registrationDaoInstanceMongoDb.createRegistrationDAO().changeUniqueid( Uid )
                ? Response.status( 200 ).build() : Response.status( 406 ).build();
    }
}