package com.app.gym.servicios;

import java.time.Duration;
import java.util.Set;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ---------- VALUE ----------
    public void setValue(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    public Long incrementValue(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public void setExpiration(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
    }

    // ---------- SET ----------
    public void addToSet(String key, String value, Duration ttl) {
        redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, ttl);
    }

    public Set<String> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // -------- VERIFICATION --------
    public boolean existsKey(String key){
        return redisTemplate.hasKey(key);
    }

    /*---------- PIPELINE ----------
    Pipeline = mandar MUCHAS operaciones a Redis en UN SOLO viaje de red

    Normalmente:

    redisTemplate.delete("k1");  
    redisTemplate.delete("k2");  
    redisTemplate.delete("k3");


    Esto hace 3 viajes de ida y vuelta:

    El cliente Java → Redis

    Redis → cliente Java

    El tiempo de red puede ser más lento que las operaciones, entonces borrar 500 claves sería lento si haces 500 viajes.

    El pipeline manda todas las instrucciones JUNTAS, Redis las ejecuta y envía todas las respuestas juntas.

    Es como:

    En vez de ir al supermercado 10 veces a comprar 1 cosa
    vas UNA vez con una lista de 10 cosas.

    🔼 Mucho más eficiente
    🔼 Mucho más rápido
    ❌ NO es atómico
    ❌ Otro cliente puede ver estados intermedios
    */
    public void deleteKeysPipeline(Set<String> keys) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keys.forEach(k -> connection.keyCommands().del(k.getBytes()));
            return null;
        });
    }
}
