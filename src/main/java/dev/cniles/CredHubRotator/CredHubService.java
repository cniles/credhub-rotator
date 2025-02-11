package dev.cniles.CredHubRotator;

import jakarta.annotation.PostConstruct;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.stereotype.Component;

@Component
public class CredHubService {

    private final CredHubProperties credHubProperties;

    private final CredHubOperations credHubOperations;

    public CredHubService(CredHubProperties credhubProperties, CredHubOperations credHubOperations) {
         this.credHubOperations = credHubOperations;
         this.credHubProperties = credhubProperties;
    }

    @PostConstruct
    public void init() {
        var credentialName = new SimpleCredentialName("/credhub-service-broker/credhub/dc715b74-9858-463f-a1a1-f9f4cbb0fb41/credentials");

        if (credHubOperations != null) {
            var credentialOperations = credHubOperations.credentials();
            if (credentialOperations != null) {
                try {
                    var result = credentialOperations.getByName(credentialName, JsonCredential.class);
                    if (result != null) {
                        System.out.println(result.getValue());
                    } else {
                        System.out.println("No credential found");
                    }

                    // try updating the credential
                    var requestBuilder = new JsonCredentialRequest.JsonCredentialRequestBuilder();
                    JsonCredential updatedValue = new JsonCredential();
                    updatedValue.put("password", "newvalue");
                    requestBuilder.name(credentialName).value(updatedValue);

                    credentialOperations.write(requestBuilder.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("No credential operations found");
            }
        }
    }
}
