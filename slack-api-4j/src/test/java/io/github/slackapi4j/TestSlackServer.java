package io.github.slackapi4j;

import io.github.slackapi4j.internal.SlackConstants;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 30/06/2019.
 */
public class TestSlackServer extends MockServerClient{
    public TestSlackServer(){
        super("127.0.0.1",1080);
    }

    public MockServerClient addAuthResponses() {
        this
                .when(HttpRequest
                        .request()
                        .withPath("/api/" + SlackConstants.AUTH_TEST)
                        .withHeader("Authorization", "Bearer " + SlackAPITest.token)
                )
                .respond(
                        HttpResponse
                                .response()
                                .withStatusCode(200)
                                .withBody("{\"ok\":true,\"url\":\"https://addstarmc.slack.com/\",\"team\":\"AddstarMC\",\"user\":\"mcbot\",\"team_id\":\"T03QHU9NF\",\"user_id\":\"UGBBH4R0S\"}")
                );
        return this;
    }

}
