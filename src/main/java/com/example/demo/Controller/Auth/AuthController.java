// java
            package com.example.demo.Controller.Auth;

            import com.example.demo.Dto.AuthenticationRequest;
            import com.example.demo.Dto.AuthenticationResponse;
            import com.example.demo.Dto.UtilisateurInscritDTO;
            import com.example.demo.Model.UtilisateurInscrit;
            import com.example.demo.Service.Auth.AuthService;
            import com.example.demo.Service.Auth.EmailService;
            import com.example.demo.Utils.JwtUtils;
            import com.fasterxml.jackson.databind.ObjectMapper;
            import org.slf4j.Logger;
            import org.slf4j.LoggerFactory;
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.beans.factory.annotation.Value;
            import org.springframework.http.HttpHeaders;
            import org.springframework.http.HttpStatus;
            import org.springframework.http.MediaType;
            import org.springframework.http.ResponseEntity;
            import org.springframework.security.access.prepost.PreAuthorize;
            import org.springframework.security.authentication.AuthenticationManager;
            import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
            import org.springframework.security.core.Authentication;
            import org.springframework.security.core.AuthenticationException;
            import org.springframework.security.core.userdetails.UserDetails;
            import org.springframework.security.core.userdetails.UserDetailsService;
            import org.springframework.web.bind.annotation.*;
            import org.springframework.web.multipart.MultipartFile;

            import java.io.IOException;
            import java.io.InputStream;
            import java.nio.file.Files;
            import java.nio.file.Path;
            import java.nio.file.Paths;
            import java.nio.file.StandardCopyOption;
            import java.util.Base64;
            import java.util.Date;
            import java.util.Map;
            import java.util.Random;

            @RestController
            @RequestMapping("/auth")
            public class AuthController {
                private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

                @Value("${app.upload.dir:uploads}")
                private String uploadBaseDir;

                @Autowired
                private AuthService authService;

                @Autowired
                private AuthenticationManager authenticationManager;
                @Autowired
                private UserDetailsService userDetailsService;
                @Autowired
                private JwtUtils jwtUtil;
                @Autowired
                private EmailService emailService;

                @PostMapping("/signup")
                public ResponseEntity<String> signup(@RequestBody UtilisateurInscritDTO utilisateurInscritDTO) {
                    if (authService.emailExists(utilisateurInscritDTO.getEmail())) {
                        return ResponseEntity.badRequest().body("An account with this email already exists");
                    }
                    String verificationCode = String.format("%04d", new Random().nextInt(10000));
                    authService.saveVerificationCode(utilisateurInscritDTO.getEmail(), verificationCode);
                    try {
                        emailService.sendVerificationEmail(utilisateurInscritDTO.getEmail(), "Email Verification", verificationCode);
                        return ResponseEntity.ok("Verification code sent to your email");
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'e-mail de vérification: " + e.getMessage());
                    }
                }

                @PostMapping(value = "/login", consumes = {"application/json", "text/plain"})
                public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
                    try {
                        Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
                        );
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        String jwt = jwtUtil.generateToken(userDetails);
                        UtilisateurInscrit utilisateurInscrit = authService.findByEmail(authenticationRequest.getEmail());
                        UtilisateurInscritDTO utilisateurInscritDTO = convertToDTO(utilisateurInscrit);
                        AuthenticationResponse response = new AuthenticationResponse(jwt);
                        return ResponseEntity.ok(response);
                    } catch (AuthenticationException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                    }
                }

                @PostMapping(value = "/verify", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
                public ResponseEntity<String> verifyUser(
                        @RequestPart("utilisateur") UtilisateurInscritDTO utilisateurInscritDTO,
                        @RequestParam("code") String code,
                        @RequestPart(value = "file", required = false) MultipartFile file) {
                    try {
                        if (file != null && !file.isEmpty()) {
                            // IMPORTANT: pass only subdir "users"
                            String savedPath = saveFile(file, "users");
                            logger.info("verifyUser -> saved imagePath (web): {}", savedPath);
                            utilisateurInscritDTO.setImagePath(savedPath);
                        }
                    } catch (IOException e) {
                        logger.error("Error saving verification file", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().build();
                    }

                    boolean isVerified = authService.verifyUserAndSave(utilisateurInscritDTO.getEmail(), code, utilisateurInscritDTO);
                    if (isVerified) {
                        return ResponseEntity.ok("User verified and saved successfully");
                    } else {
                        return ResponseEntity.badRequest().body("Invalid or expired code");
                    }
                }

                @PreAuthorize("isAuthenticated()")
                @GetMapping("/userinfo")
                public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String jwtToken = authHeader.substring(7);
                        try {
                            UtilisateurInscritDTO utilisateurInscritDTO = authService.getUserInfo(jwtToken);
                            return ResponseEntity.ok(utilisateurInscritDTO);
                        } catch (Exception e) {
                            return ResponseEntity.badRequest().body("Invalid token");
                        }
                    }
                    return ResponseEntity.badRequest().body("Authorization header missing or invalid");
                }

                @GetMapping("/me")
                public ResponseEntity<?> getCurrentUser(Authentication authentication) {
                    return ResponseEntity.ok(authentication.getPrincipal());
                }

                @GetMapping("/role")
                public ResponseEntity<?> getRoleFromToken(
                        @RequestHeader(value = "Authorization", required = false) String authHeader) {

                    logger.info("🔍 getRoleFromToken called");

                    // Vérifier le header Authorization
                    if (authHeader == null || authHeader.isBlank()) {
                        logger.error("❌ Authorization header is missing");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", "Authorization header missing"));
                    }

                    if (!authHeader.startsWith("Bearer ")) {
                        logger.error("❌ Authorization header format invalid: {}", authHeader);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", "Authorization header must start with 'Bearer '"));
                    }

                    // Extraire le token
                    String token = authHeader.substring(7).trim();

                    if (token.isEmpty()) {
                        logger.error("❌ Token is empty after 'Bearer '");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", "Token is empty"));
                    }

                    logger.info("✅ Token extracted: {}...", token.substring(0, Math.min(20, token.length())));

                    try {
                        // Vérifier si le token est valide (non expiré)
                        String username = jwtUtil.extractUsername(token);
                        logger.info("📧 Username from token: {}", username);

                        if (username == null || username.isBlank()) {
                            logger.error("❌ Username is null or empty in token");
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                    .body(Map.of("error", "Invalid token: no username found"));
                        }

                        // Extraire le rôle
                        String role = jwtUtil.extractRole(token);

                        if (role == null || role.isBlank()) {
                            logger.error("❌ Role not found in token for user: {}", username);
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                    .body(Map.of(
                                            "error", "Role not found in token",
                                            "username", username
                                    ));
                        }

                        logger.info("✅ Role successfully extracted: {} for user: {}", role, username);

                        // Retourner le rôle avec des infos supplémentaires
                        return ResponseEntity.ok(Map.of(
                                "role", role,
                                "username", username,
                                "timestamp", System.currentTimeMillis()
                        ));

                    } catch (io.jsonwebtoken.ExpiredJwtException e) {
                        logger.error("❌ Token expired: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "Token expired"));

                    } catch (io.jsonwebtoken.MalformedJwtException e) {
                        logger.error("❌ Malformed token: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", "Malformed token"));

                    } catch (io.jsonwebtoken.SignatureException e) {
                        logger.error("❌ Invalid signature: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "Invalid token signature"));

                    } catch (Exception e) {
                        logger.error("❌ Unexpected error extracting role: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of(
                                        "error", "Failed to extract role",
                                        "detail", e.getMessage()
                                ));
                    }
                }

                /**
                 * ✅ MÉTHODE HELPER - Décoder le payload JWT (DEBUG uniquement)
                 */
                private Map<String, Object> decodeJwtPayload(String token) {
                    try {
                        String[] parts = token.split("\\.");
                        if (parts.length < 2) {
                            logger.warn("Token does not have enough parts");
                            return Map.of("error", "Invalid token format");
                        }

                        String payloadB64 = parts[1];
                        byte[] decoded = Base64.getUrlDecoder().decode(payloadB64);
                        String json = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);

                        ObjectMapper mapper = new ObjectMapper();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = mapper.readValue(json, Map.class);

                        logger.info("📝 Decoded JWT payload: {}", map);
                        return map == null ? Map.of() : map;

                    } catch (Exception ex) {
                        logger.error("Failed to decode JWT payload: {}", ex.getMessage());
                        return Map.of("error", "Failed to decode payload", "detail", ex.getMessage());
                    }
                }

                /**
                 * ✅ ENDPOINT DE DEBUG - Pour vérifier le contenu du token
                 */
                @GetMapping("/debug-token")
                public ResponseEntity<?> debugToken(
                        @RequestHeader(value = "Authorization", required = false) String authHeader) {

                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Authorization header missing or invalid"));
                    }

                    String token = authHeader.substring(7).trim();

                    try {
                        // Décoder sans vérifier la signature (pour debug)
                        Map<String, Object> payload = decodeJwtPayload(token);

                        // Extraire avec la méthode officielle
                        String username = jwtUtil.extractUsername(token);
                        String role = jwtUtil.extractRole(token);
                        Date expiration = jwtUtil.extractExpiration(token);

                        return ResponseEntity.ok(Map.of(
                                "payload", payload,
                                "extractedUsername", username != null ? username : "null",
                                "extractedRole", role != null ? role : "null",
                                "expiration", expiration != null ? expiration.toString() : "null",
                                "isExpired", expiration != null && expiration.before(new Date())
                        ));

                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", e.getMessage()));
                    }
                }

                @PreAuthorize("isAuthenticated()")
                @PutMapping(value = "/profile", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
                public ResponseEntity<?> updateProfile(
                        Authentication authentication,
                        @RequestPart("utilisateur") UtilisateurInscritDTO utilisateurInscritDTO,
                        @RequestPart(value = "file", required = false) MultipartFile file) {
                    if (authentication == null || authentication.getName() == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
                    }

                    if (file != null && !file.isEmpty()) {
                        logger.info("File details - name: {}, size: {}, contentType: {}",
                                file.getOriginalFilename(), file.getSize(), file.getContentType());
                    }

                    try {
                        if (file != null && !file.isEmpty()) {
                            // IMPORTANT: pass only subdir "users" (no leading slash)
                            String savedPath = saveFile(file, "users");
                            logger.info("updateProfile -> saved imagePath (web): {}", savedPath);
                            utilisateurInscritDTO.setImagePath(savedPath);
                        } else {
                            logger.warn("No file provided in update profile request");
                        }
                        UtilisateurInscritDTO updated = authService.updateProfile(authentication.getName(), utilisateurInscritDTO);
                        return ResponseEntity.ok(updated);
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    } catch (Exception e) {
                        logger.error("Error updating profile", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating profile");
                    }
                }

                // Save under <workingDir>/<app.upload.dir>/<subdir>/filename and return web path "/uploads/<subdir>/<filename>"
                private String saveFile(MultipartFile file, String subdir) throws IOException {
                    Path base = Paths.get(uploadBaseDir).toAbsolutePath().normalize();
                    Path uploadDir = (subdir == null || subdir.isBlank()) ? base : base.resolve(subdir).normalize();

                    Files.createDirectories(uploadDir);
                    logger.info("Saving file to directory: {}", uploadDir);

                    String original = file.getOriginalFilename() == null ? "file" : Path.of(file.getOriginalFilename()).getFileName().toString();
                    String safe = original.replaceAll("[^A-Za-z0-9._-]", "_");
                    String filename = System.currentTimeMillis() + "_" + safe;

                    Path target = uploadDir.resolve(filename).normalize();
                    try (InputStream in = file.getInputStream()) {
                        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    }

                    String webPath = "/uploads/" + (subdir == null || subdir.isBlank() ? "" : subdir + "/") + filename;
                    logger.info("File saved: absolutePath={} webPath={}", target, webPath);
                    return webPath;
                }

                @PostMapping("/password-reset/request")
                public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> body) {
                    String email = body.get("email");
                    if (email == null || email.isBlank()) {
                        return ResponseEntity.badRequest().body("Email is required");
                    }
                    try {
                        // Do not reveal whether email exists
                        authService.initiatePasswordReset(email);
                        return ResponseEntity.ok("If the email exists, a reset link has been sent");
                    } catch (Exception e) {
                        logger.error("Error initiating password reset for {}", email, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to initiate password reset");
                    }
                }

                @PostMapping("/password-reset/confirm")
                public ResponseEntity<String> confirmPasswordReset(@RequestBody Map<String, String> body) {
                    String email = body.get("email");
                    String token = body.get("token");
                    String newPassword = body.get("newPassword");

                    if (email == null || token == null || newPassword == null ||
                            email.isBlank() || token.isBlank() || newPassword.isBlank()) {
                        return ResponseEntity.badRequest().body("email, token and newPassword are required");
                    }

                    try {
                        boolean ok = authService.resetPassword(email, token, newPassword);
                        if (ok) {
                            return ResponseEntity.ok("Password reset successful");
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
                        }
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                    } catch (Exception e) {
                        logger.error("Error confirming password reset for {}", email, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password");
                    }
                }
                @PatchMapping("/mailing")
                public ResponseEntity<?> setMailingAuthorization(
                        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
                        @RequestBody Map<String, Boolean> body
                ) {
                    if (authHeader == null || authHeader.isBlank() || !authHeader.toLowerCase().startsWith("bearer ")) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                    Boolean autorise = body.get("autoriseMailing");
                    if (autorise == null) {
                        return ResponseEntity.badRequest().body(Map.of("error", "autoriseMailing is required"));
                    }

                    String token = authHeader.replaceFirst("(?i)^Bearer\\s+", "").trim();
                    UtilisateurInscritDTO userDto;
                    try {
                        userDto = authService.getUserInfo(token);
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                    if (userDto == null || userDto.getEmail() == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }

                    boolean updated = authService.setMailingAuthorization(userDto.getEmail(), autorise);
                    if (!updated) return ResponseEntity.notFound().build();
                    return ResponseEntity.ok(Map.of("autoriseMailing", autorise));
                }



                private UtilisateurInscritDTO convertToDTO(UtilisateurInscrit utilisateurInscrit) {
                    UtilisateurInscritDTO dto = new UtilisateurInscritDTO();
                    dto.setId(utilisateurInscrit.getId());
                    dto.setEmail(utilisateurInscrit.getEmail());
                    dto.setNom(utilisateurInscrit.getNom());
                    dto.setPrenom(utilisateurInscrit.getPrenom());
                    dto.setTelephone(utilisateurInscrit.getTelephone());
                    dto.setRole(utilisateurInscrit.getRole());
                    dto.setAdresse(utilisateurInscrit.getAdresse());
                    dto.setCodePostal(utilisateurInscrit.getCodePostal());
                    dto.setPays(utilisateurInscrit.getPays());
                    dto.setVille(utilisateurInscrit.getVille());
                    dto.setDateNaissance(utilisateurInscrit.getDateNaissance());
                    dto.setImagePath(utilisateurInscrit.getImagePath());
                    return dto;
                }
            }