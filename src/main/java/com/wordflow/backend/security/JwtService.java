package com.wordflow.backend.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    // 🔐 Generate JWT
    public String generateToken(String email) {
        try {
            JWSSigner signer = new MACSigner(SECRET.getBytes());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(email)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );

            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT", e);
        }
    }

    // 🔍 Extract email
    public String extractEmail(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    // ✅ Validate token
    public boolean isValidToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            JWSVerifier verifier = new MACVerifier(SECRET.getBytes());

            boolean isSignatureValid = signedJWT.verify(verifier);
            boolean isExpired = signedJWT.getJWTClaimsSet()
                    .getExpirationTime()
                    .before(new Date());

            return isSignatureValid && !isExpired;

        } catch (Exception e) {
            return false;
        }
    }
}