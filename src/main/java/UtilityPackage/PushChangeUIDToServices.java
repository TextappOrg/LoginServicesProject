package UtilityPackage;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

public class PushChangeUIDToServices implements Runnable {
    private static final String localhost = "http://127.0.0.1:8080/";
    public static final String frndMgmtSrvcEndpoint = localhost + "Friends/Finder/Drug/ChangeUUID";
    public static final String NotifSrvcEndpoint = localhost +
            "NotifierService/NotifyRegister/RegisterToken/ChangeUUID";

    private final String oldUUID;
    private final String newUUID;
    private final String url;

    public PushChangeUIDToServices(String oldUUID, String newUUID, String url) {
        this.oldUUID = oldUUID;
        this.newUUID = newUUID;
        this.url = url;
    }

    private void doPost(String oldUUID, String newUUID, String url) {
        Form form = new Form();
        form.param("oldUUID",oldUUID);
        form.param("newUUID",newUUID);

        ClientBuilder.newClient() // Client
                .target(url) // WebTarget
                .request(MediaType.APPLICATION_FORM_URLENCODED) //Invocation.Builder
                .post(Entity.form(form));
    }



    @Override
    public void run() {
        doPost(this.oldUUID,this.newUUID,this.url);
    }
}
