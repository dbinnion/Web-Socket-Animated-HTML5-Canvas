package websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/endpoint")
public class ServerEndpointTest
{
    static Set<Session> clientset = Collections.synchronizedSet(new HashSet<Session>());
    
    @OnOpen
    public void handleOpen(Session client)
    {
        client.addMessageHandler(new MessageHandlerTest(client));
        clientset.add(client);
    }

    @OnClose
    public void handleClose(Session client)
    {
        clientset.remove(client);
    }

    @OnError
    public void handleError(Session client, Throwable t)
    {
        t.printStackTrace();
    }
}
