package com.database.federation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

  private static final Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
  private final ObjectMapper objectMapper = new ObjectMapper();

  public String generateToken(Object object, long expirationTime) throws JsonProcessingException {
    String objectJson = objectMapper.writeValueAsString(object);
    return Jwts.builder()
        .setSubject(objectJson)
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(key)
        .compact();
  }

  public <T> T parseToken(String token, Class<T> objectType) throws JsonProcessingException {
    Jws<Claims> claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token);
    String objectJson = claims.getBody().getSubject();
    return objectMapper.readValue(objectJson, objectType);
  }
}
