package cu.su.test.Controller;

import cu.su.controller.EnemyAI;
import cu.su.controller.GameClient;
import cu.su.controller.GameServer;
import org.junit.Test;

import java.io.IOException;

public class TestNetFrameWork {
    GameServer server;

    @Test(expected = IOException.class)
    public void testServerFail1() throws IOException {
        server = new GameServer(1024);
        new GameServer(1024);
    }

    @Test(expected = NullPointerException.class)
    public void testServerFail2() throws NullPointerException {
        server.sendToAll(null);
    }

    @Test(expected = IOException.class)
    public void testClientFail1() throws IOException {
        new GameClient("localhost1", 1024);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClientFail2() throws IllegalArgumentException, IOException {
        GameClient client = new GameClient("localhost", 1024);
        client.send(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClientFail3() throws IllegalArgumentException, IOException {
        GameClient client = new GameClient("localhost", 1024);
        client.send(new EnemyAI());
    }
}
