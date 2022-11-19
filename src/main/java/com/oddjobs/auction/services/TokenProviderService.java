package com.oddjobs.auction.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Slf4j
public class TokenProviderService {
    private static final String AUTHENTICATED = "authenticated";

    public static final long TEMP_TOKEN_VALIDITY_IN_MILLIS = 300000;

    private final Algorithm algorithm = JwtUtils.getAlgorithm();

    @Value("${jwt.issuer}")
    private String issuer;


   public String createToken(Object principal, boolean authenticated){

       User user = (User) principal;

       return JWT.create()
               .withSubject(user.getUsername())
               .withExpiresAt(new Date(System.currentTimeMillis()+ 600000))
               .withIssuer(issuer)
               .withClaim("AUTHENTICATED", authenticated)
               .sign(algorithm);
   }

   public String extractJwtFromRequest(HttpServletRequest request) throws Exception{
       String authorizationHeader = request.getHeader(AUTHORIZATION);
       if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
           return  authorizationHeader.substring("Bearer ".length());
       }
       throw new Exception("Wrong authorization header value");
   }
   public boolean isAuthenticated(DecodedJWT jwtToken){
       return jwtToken.getClaim("AUTHENTICATED").asBoolean();
   }
   public DecodedJWT validateToken(String token) throws Exception{
       JWTVerifier verifier = JWT.require(algorithm).build();
       return verifier.verify(token);
   }

}
