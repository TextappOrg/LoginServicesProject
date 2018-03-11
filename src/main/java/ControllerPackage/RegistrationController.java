package ControllerPackage;

import DAOPackage.RegistrationDaoInterface;
import DAOPackage.RegistrationDaoMongoDb;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.internal.Nullable;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.lang.annotation.Retention;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Someone please take care of the exception handling for NamingException
 */

@WebServlet("/User")
@Path("/User")
public class RegistrationController {

    private RegistrationDaoInterface registrationDaoInstanceMongoDb;

    public RegistrationController() {
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
                                 @NotNull @FormParam("answer") String answer)
             {
        if(!confirmPassword.equals( password )) return Response.status( 401 ).build();
        else {
            try {
                this.registrationDaoInstanceMongoDb = new RegistrationDaoMongoDb()
                        .setUsername( username )
                        .setRealfirstname( firstName )
                        .setRealmiddlename( middleName == null || middleName.isEmpty() ? "" : middleName )
                        .setReallastname( lastName )
                        .setUniqueid( username )
                        .setPassword( password )
                        .setSecretQuestion( securityQuestion )
                        .setAnswer(answer)
                        .createRegistrationDAO();
            } catch (NamingException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
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
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings( "javadoc" )
    public Response loginUser(@NotNull @FormParam("username") String username,
                              @NotNull @FormParam("password") String password)  {
        try {
            LinkedHashMap<String,Object> judgement = this.registrationDaoInstanceMongoDb
                                                    .createRegistrationDAO()
                                                    .authenticateUser( username,password );
            if(judgement.get("flag").equals("new")){
                judgement.remove( "flag" );
                String jsonDat = new ObjectMapper().writeValueAsString(judgement);
                return Response.ok(jsonDat,MediaType.APPLICATION_JSON).build();
            }else if(((String) judgement.get( "flag" )).equalsIgnoreCase("logged" )){
                return Response.status(Response.Status.CONFLICT).entity("User already logged in").build();
            }else if(((String) judgement.get( "flag" )).equalsIgnoreCase("NaN" )){
                return Response.status( Response.Status.NO_CONTENT ).entity( "User not found" ).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (InvalidKeySpecException | NamingException | JsonProcessingException | NoSuchAlgorithmException e) {
            Logger.getAnonymousLogger().log( Level.SEVERE, e.getMessage() );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
        }
    }


    /**
     * @param username
     * @param clientToken
     * @return
     */
    @POST
    @Path( "/Logout" )
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SuppressWarnings( "javadoc" )
    public Response logoutUser(@NotNull @FormParam( "username" ) String username,
                               @NotNull @FormParam( "clientToken" ) String clientToken){
        try {
            return this.registrationDaoInstanceMongoDb.createRegistrationDAO().logoutService( username,clientToken )
                    ? Response.status( Response.Status.OK ).build() : Response.status( Response.Status.UNAUTHORIZED )
                    .build();
        } catch (NamingException e) {
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
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
                                     @Nullable @FormParam("confirmPassword" ) String confirmPassword,
                                     @Nullable @FormParam("securityQuestion") String securityQuestion,
                                     @Nullable @FormParam("answer") String answer) throws NamingException {
        if((confirmPassword != null) && confirmPassword.equalsIgnoreCase(password))
            return Response.status( 401 ).build();
        else {
            try {
                return this.registrationDaoInstanceMongoDb.createRegistrationDAO().updateCredentials( userId, username, firstName, middleName, lastName, password, securityQuestion, answer )
                        ? Response.status( 200 ).build() : Response.status( 406 ).build();
            } catch (InvalidKeySpecException | NoSuchAlgorithmException | NullPointerException e) {
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
    public Response resetUid(@NotNull @FormParam("UID") String Uid, @NotNull @FormParam("password") String password)
            throws NamingException {
        return this.registrationDaoInstanceMongoDb.createRegistrationDAO().changeUniqueid( Uid, password )
                ? Response.status( 200 ).build() : Response.status( 406 ).build();
    }
}