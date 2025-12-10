package com.example.demo.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    public static final String SECRET = "33a87164d219d2e7580fc2a90ae435b56a6dbac1190b7460dcfb5119754ad759";

    // Méthode pour extraire le nom d'utilisateur (subject) du token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Méthode pour extraire la date d'expiration du token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Méthode générique pour extraire une réclamation spécifique du token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * ✅ MÉTHODE CORRIGÉE - Extraction du rôle depuis le JWT
     * Gère les cas: String simple, Collection, ou null
     */
    public String extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);

            // Essayer différents noms de claims possibles
            Object roleObj = claims.get("role");
            if (roleObj == null) roleObj = claims.get("roles");
            if (roleObj == null) roleObj = claims.get("authorities");
            if (roleObj == null) roleObj = claims.get("authority");

            if (roleObj == null) {
                System.err.println("❌ No role claim found in token");
                return null;
            }

            // Cas 1: C'est une Collection (liste de rôles)
            if (roleObj instanceof Collection) {
                Collection<?> coll = (Collection<?>) roleObj;
                if (coll.isEmpty()) {
                    System.err.println("❌ Role collection is empty");
                    return null;
                }
                Object first = coll.iterator().next();
                String role = first == null ? null : first.toString();
                System.out.println("✅ Role extracted from Collection: " + role);
                return role;
            }

            // Cas 2: C'est une String simple (votre cas)
            if (roleObj instanceof String) {
                String role = (String) roleObj;
                System.out.println("✅ Role extracted as String: " + role);
                return role;
            }

            // Cas 3: Autre type, convertir en String
            String role = roleObj.toString();
            System.out.println("✅ Role extracted (toString): " + role);
            return role;

        } catch (Exception ex) {
            System.err.println("❌ Error extracting role from token: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    // Méthode privée pour extraire toutes les réclamations (claims) du token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Vérification si le token a expiré
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Méthode pour valider un token
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (username.equals(extractedUsername) && !isTokenExpired(token));
    }

    // Générer un token JWT
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Ajoute le rôle dans les claims
        try {
            if (userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty()) {
                String role = userDetails.getAuthorities().iterator().next().getAuthority();
                claims.put("role", role);
                System.out.println("✅ Token generated with role: " + role);
            } else {
                claims.put("role", "ROLE_USER");
                System.out.println("⚠️ No authorities found, using default role: ROLE_USER");
            }
        } catch (Exception e) {
            claims.put("role", "ROLE_USER");
            System.err.println("❌ Error extracting authorities: " + e.getMessage());
        }

        return createToken(claims, userDetails.getUsername());
    }

    // Créer un token avec les réclamations et un sujet
    public String createToken(Map<String, Object> claims, String subject) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 30); // 30 minutes

        System.out.println("📝 JWT Creation - Subject: " + subject + ", IssuedAt: " + issuedAt + ", Expiration: " + expiration);
        System.out.println("📝 JWT Claims: " + claims);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Obtenir une clé sécurisée pour signer le token
    public Key getSignKey() {
        byte[] keyBytes = null;
        try {
            // Essayer de décoder en base64
            keyBytes = Decoders.BASE64.decode(SECRET);
        } catch (Exception e) {
            // Si échec, essayer en hexadécimal
            try {
                String s = SECRET.trim();
                if (s.length() % 2 == 0 && s.matches("^[0-9a-fA-F]+$")) {
                    int len = s.length();
                    keyBytes = new byte[len / 2];
                    for (int i = 0; i < len; i += 2) {
                        keyBytes[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                + Character.digit(s.charAt(i + 1), 16));
                    }
                } else {
                    // Fallback: utiliser les bytes UTF-8
                    keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
                }
            } catch (Exception ex) {
                keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}