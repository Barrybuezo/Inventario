package com.example.inventarioapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Convierte una contraseña en texto plano a un hash SHA-256 (irreversible).
// Nunca se guarda ni se compara la contraseña real, solo su hash.
public class PasswordHasher {

    public static String hashear(String textoPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytesHash = digest.digest(textoPlano.getBytes());

            StringBuilder hexBuilder = new StringBuilder();
            for (byte b : bytesHash) {
                // Cada byte se convierte a 2 caracteres hexadecimales (ej: 0x1F -> "1f")
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexBuilder.append('0');
                hexBuilder.append(hex);
            }
            return hexBuilder.toString();

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre existe en Android, este catch es solo para que Java compile.
            throw new RuntimeException("Algoritmo de hash no disponible", e);
        }
    }
}