package fr.lefuturiste.redis.test;

import fr.lefuturiste.redis.RedisClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@FixMethodOrder(MethodSorters.JVM)
public class RedisClientTest {

    private RedisClient redisClient;
    private String redisHost = "127.0.0.1";
    private int redisPort = 6379;
    private final String password = "root";
    private static String key = UUID.randomUUID().toString();
    private static String value = UUID.randomUUID().toString();
    private static String jsonKey = UUID.randomUUID().toString();
    private static JSONObject jsonObject = new JSONObject()
            .put("string", "str\"ing hello'o'o'o")
            .put("boolean", true)
            .put("array", new JSONArray().put(1).put(2).put(3).put(4))
            .put("int", 123)
            .put("float", 123.123)
            .put("object", new JSONObject().put("yes", true).put("no", false));

    public RedisClientTest() throws IOException {
        redisClient = new RedisClient();
    }

    private void authRedisClient() throws IOException {
        this.redisClient.auth(password);
    }

    @Test
    public void shouldAuthSuccessfullyAndPing() throws IOException {
        Assert.assertTrue(this.redisClient.auth(password));
        Assert.assertTrue(this.redisClient.ping());
    }

    @Test
    public void shouldGetTheSocketSuccessfully() {
        Assert.assertNotNull(this.redisClient.getSocket());
        Assert.assertEquals(redisHost,
                this.redisClient.getSocket().getInetAddress().toString().replace("/", ""));
        Assert.assertEquals(redisPort, this.redisClient.getSocket().getPort());
    }

    @Test
    public void shouldInstantiateCustomClients() throws IOException {
        RedisClient firstCustomRedisClient = new RedisClient(redisPort);
        Assert.assertNotNull(firstCustomRedisClient);
        RedisClient secondCustomRedisClient = new RedisClient(redisHost, redisPort);
        Assert.assertNotNull(secondCustomRedisClient);
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

    @Test
    public void shouldFlushAllKeysSuccessfully() throws IOException {
        this.authRedisClient();
        Assert.assertTrue(this.redisClient.set("key1", "value1"));
        Assert.assertTrue(this.redisClient.set("key2", "value2"));
        Assert.assertTrue(this.redisClient.exists("key1"));
        Assert.assertTrue(this.redisClient.exists("key2"));
        Assert.assertTrue(this.redisClient.flushAll());
        Assert.assertFalse(this.redisClient.exists("key1"));
        Assert.assertFalse(this.redisClient.exists("key2"));
    }

    @Test
    public void shouldSetExpireSuccessfully() throws IOException, InterruptedException {
        this.authRedisClient();
        key = UUID.randomUUID().toString();
        Assert.assertTrue(this.redisClient.set(key, "content"));
        Assert.assertTrue(this.redisClient.exists(key));
        Assert.assertEquals(-1, this.redisClient.ttl(key));
        Assert.assertTrue(this.redisClient.expire(key, Duration.ofSeconds(1)));
        Assert.assertEquals(1, this.redisClient.ttl(key));
        Thread.sleep(1500);
        Assert.assertEquals(-2, this.redisClient.ttl(key));
    }

    @Test
    public void shouldEscapeJsonStringSuccessfully() throws IOException {
        this.authRedisClient();
        this.redisClient.flushAll();
        this.redisClient.setJson(jsonKey, jsonObject);
        Assert.assertEquals(jsonObject.toString(), this.redisClient.getJson(jsonKey).toString());
    }
}
