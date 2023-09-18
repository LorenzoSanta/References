package com.security.SecurityProj.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY="Wxv/i81eAVthWlYUUKCum7fYzmL3HWq+St6qRBupiIT98BYDmUzheq9PSham/NK/";

    //estraggo il subject "quindi lo username" dal token
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    //estrae un claim specifico da tutti i claims
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //per generare un token senza extraclaims
    public String generateToken(UserDetails userDetails){
       return generateToken(new HashMap<>(),userDetails);
    }

    //creo un token basato sullo userDetails e i suoi contenuti
    public String generateToken(Map<String,Object> extraClaims, UserDetails userDetails){
        return Jwts.builder() //chiamata al builder
                .setClaims(extraClaims) //claims extra oltre l'entity
                .setSubject(userDetails.getUsername()) //aggiungi il subject
                .setIssuedAt(new Date(System.currentTimeMillis())) //momento di creazione
                .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*24)) //scadenza del token
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) //creazione di chiave valida
                .compact(); //generazione del token
    }

    //estre i claims del token
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //la signinkey serve alla firma per definire il client che invia i dati
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //check della validità del token se UserDetails possiede il token
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    //controllo se il token è scaduto
    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());

    }

    //estrae il claim riguardo l'expiration del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
