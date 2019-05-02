package fr.lefuturiste.redis;

import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@FixMethodOrder(MethodSorters.JVM)
public class RedisClientTest {
    private final int redisPort = 6379;
    private final String redisHost = "127.0.0.1";
    private final String password = "root";
    private static String key = UUID.randomUUID().toString();
    private static String value = UUID.randomUUID().toString();
    private static String jsonKey = UUID.randomUUID().toString();
    private static JSONObject jsonObject = new JSONObject()
            .put("string", "string")
            .put("boolean", true)
            .put("array", new JSONArray().put(1).put(2).put(3).put(4))
            .put("int", 123)
            .put("float", 123.123)
            .put("object", new JSONObject().put("yes", true).put("no", false));
    private RedisClient redisClient;

    public RedisClientTest() throws IOException {
        redisClient = new RedisClient();
    }

    private void authRedisClient() throws IOException {
        this.redisClient.auth(password);
    }

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void shouldAuthSuccessfullyAndPing() throws IOException {
        Assert.assertTrue(this.redisClient.auth(password));
        Assert.assertTrue(this.redisClient.ping());
    }

    @Test
    public void shouldGetTheSocketSuccessfully() {
        Assert.assertNotNull(this.redisClient.getSocket());
        Assert.assertEquals(this.redisHost,
                this.redisClient.getSocket().getInetAddress().toString().replace("/", ""));
        Assert.assertEquals(this.redisPort, this.redisClient.getSocket().getPort());
    }

    @Test
    public void shouldSetKeySuccessfully() throws IOException {
        this.authRedisClient();
        Assert.assertTrue(this.redisClient.set(key, value));
    }

    @Test
    public void shouldNotFindAKeySuccessfully() throws IOException {
        this.authRedisClient();
        Assert.assertNull(this.redisClient.get(UUID.randomUUID().toString()));
    }

    @Test
    public void shouldKnownIfAKeyExistSuccessfully() throws IOException {
        this.authRedisClient();
        Assert.assertTrue(this.redisClient.exists(key));
        Assert.assertFalse(this.redisClient.exists(UUID.randomUUID().toString()));
    }

    @Test
    public void shouldGetKeySuccessfully() throws IOException {
        this.authRedisClient();
        Assert.assertEquals(value, redisClient.get(key));
    }

    @Test
    public void shouldDeleteAKeySuccessfully() throws IOException {
        this.authRedisClient();
        Assert.assertTrue(this.redisClient.exists(key));
        Assert.assertTrue(this.redisClient.del(key));
        Assert.assertFalse(this.redisClient.exists(key));
    }

    @Test
    public void shouldSetJsonKeySuccessfully() throws IOException {
        this.authRedisClient();
        Assert.assertTrue(this.redisClient.setJson(jsonKey, jsonObject));
        Assert.assertEquals(jsonObject.toString(0), this.redisClient.get(jsonKey));
    }

    @Test
    public void shouldNotFindAJsonKeySuccessfully() throws IOException {
        this.authRedisClient();
        Assert.assertNull(this.redisClient.getJson(UUID.randomUUID().toString()));
    }

    @Test
    public void shouldGetJsonKeySuccessfully() throws IOException {
        this.authRedisClient();
        Assert.assertEquals(jsonObject.toString(), this.redisClient.getJson(jsonKey).toString());
    }
}