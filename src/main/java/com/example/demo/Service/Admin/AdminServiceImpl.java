// java
        package com.example.demo.Service.Admin;

        import com.example.demo.Dto.UtilisateurInscritDTO;
        import com.example.demo.Model.Role;
        import com.example.demo.Model.UtilisateurInscrit;
        import com.example.demo.Repository.UtilisateurInscritRepository;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.Date;
        import java.util.List;

        @Service
        @Transactional
        public class AdminServiceImpl implements AdminService {

            private final UtilisateurInscritRepository repo;
            private final PasswordEncoder passwordEncoder;

            public AdminServiceImpl(UtilisateurInscritRepository repo, PasswordEncoder passwordEncoder) {
                this.repo = repo;
                this.passwordEncoder = passwordEncoder;
            }

            @Override
            @Transactional(readOnly = true)
            public List<UtilisateurInscritDTO> findAllAdmins() {
                return repo.findAllByRole(Role.ADMINISTRATEUR)
                        .stream().map(UtilisateurInscrit::getDTO).toList();
            }

            @Override
            @Transactional(readOnly = true)
            public UtilisateurInscritDTO findAdminById(long id) {
                UtilisateurInscrit admin = repo.findByIdAndRole(id, Role.ADMINISTRATEUR)
                        .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
                return admin.getDTO();
            }

            @Override
            public UtilisateurInscritDTO createAdmin(UtilisateurInscritDTO dto) {
                if (repo.existsByEmail(dto.getEmail())) {
                    throw new IllegalArgumentException("Email already in use");
                }
                UtilisateurInscrit a = new UtilisateurInscrit();
                apply(dto, a);

                if (dto.getMotDePasse() != null && !dto.getMotDePasse().isBlank()) {
                    a.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
                }

                a.setRole(Role.ADMINISTRATEUR);
                if (a.getDateInscription() == null) {
                    a.setDateInscription(new Date());
                }
                return repo.save(a).getDTO();
            }

            @Override
            public UtilisateurInscritDTO updateAdmin(long id, UtilisateurInscritDTO dto) {
                UtilisateurInscrit a = repo.findByIdAndRole(id, Role.ADMINISTRATEUR)
                        .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
                if (dto.getEmail() != null && repo.existsByEmailAndIdNot(dto.getEmail(), id)) {
                    throw new IllegalArgumentException("Email already in use");
                }
                apply(dto, a);

                if (dto.getMotDePasse() != null && !dto.getMotDePasse().isBlank()) {
                    a.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
                }

                a.setRole(Role.ADMINISTRATEUR);
                return repo.save(a).getDTO();
            }

            @Override
            public void deleteAdmin(long id) {
                UtilisateurInscrit a = repo.findByIdAndRole(id, Role.ADMINISTRATEUR)
                        .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
                repo.delete(a);
            }

            private void apply(UtilisateurInscritDTO dto, UtilisateurInscrit u) {
                if (dto.getEmail() != null) u.setEmail(dto.getEmail());
                if (dto.getMotDePasse() != null) u.setMotDePasse(dto.getMotDePasse()); // overridden if provided (encoded) above
                if (dto.getNom() != null) u.setNom(dto.getNom());
                if (dto.getPrenom() != null) u.setPrenom(dto.getPrenom());
                if (dto.getTelephone() != null) u.setTelephone(dto.getTelephone());
                if (dto.getAdresse() != null) u.setAdresse(dto.getAdresse());
                if (dto.getVille() != null) u.setVille(dto.getVille());
                if (dto.getCodePostal() != null) u.setCodePostal(dto.getCodePostal());
                if (dto.getPays() != null) u.setPays(dto.getPays());
                if (dto.getDateNaissance() != null) u.setDateNaissance(dto.getDateNaissance());
                if (dto.getImagePath() != null) u.setImagePath(dto.getImagePath());
                if (dto.getDateInscription() != null) u.setDateInscription(dto.getDateInscription());
            }
        }