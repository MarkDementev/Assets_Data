package fund.data.assets.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;

/**
 * @version 0.6-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Component
public class JWTHelper {
    private final String secretKey;
    private final String issuer;
    private final Long expirationSec;
    private final Long clockSkewSec;
    private final Clock clock;

    public JWTHelper(@Value("${jwt.issuer:app}") final String issuer,
                     @Value("${jwt.expiration-sec:86400}") final Long expirationSec,
                     @Value("${jwt.clock-skew-sec:300}") final Long clockSkewSec,
                     @Value("${jwt.secret:secret}") final String secret) {
        this.secretKey = BASE64.encode(secret.getBytes());
        this.issuer = issuer;
        this.expirationSec = expirationSec;
        this.clockSkewSec = clockSkewSec;
        this.clock = new DefaultClock();
    }

    public String expiring(final Map<String, Object> attributes) {
        return Jwts.builder()
                .signWith(HS256, secretKey)
                .setClaims(getClaims(attributes, expirationSec))
                .compact();
    }

    public Map<String, Object> verify(final String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .setClock(clock)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims getClaims(final Map<String, Object> attributes, final Long expiresInSec) {
        final Claims claims = Jwts.claims();

        claims.setIssuer(issuer);
        claims.setIssuedAt(clock.now());
        claims.putAll(attributes);

        if (expiresInSec > 0) {
            claims.setExpiration(new Date(System.currentTimeMillis() + expiresInSec * 1000));
        }
        return claims;
    }
}
