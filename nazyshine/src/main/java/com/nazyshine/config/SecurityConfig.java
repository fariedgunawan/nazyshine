package com.nazyshine.config;

import com.nazyshine.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer; // Import ini untuk Customizer.withDefaults()
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Pastikan JwtAuthEntryPoint dan UserDetailsServiceImpl sudah diimpor dengan benar di sini

@Configuration
@EnableMethodSecurity(
        // securedEnabled = true, // Tidak diperlukan dengan prePostEnabled = true
        // jsr250Enabled = true, // Tidak diperlukan dengan prePostEnabled = true
        prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthFilter authenticationJwtTokenFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Izinkan akses ke endpoint API autentikasi
                        .requestMatchers("/api/auth/**").permitAll()
                        // Izinkan akses ke endpoint API publik (jika ada)
                        .requestMatchers("/api/test/**").permitAll()

                        // --- ATURAN BARU: IZINKAN AKSES KE FILE STATIS ---
                        // Mengizinkan akses ke root path dan file HTML spesifik
                        .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/admin.html", "/user.html").permitAll()
                        // Mengizinkan akses ke folder statis umum (CSS, JS, Images, dll.)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        // --------------------------------------------------

                        // Wajib ADMIN role untuk API admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Wajib USER atau ADMIN role untuk API user
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN") // Perbarui ini jika sebelumnya hanya hasRole("USER")

                        // Setiap request lainnya yang belum diizinkan secara eksplisit, wajib otentikasi
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // --- PENTING: Aktifkan CORS (jika Anda memiliki @CrossOrigin di controller) ---
        http.cors(Customizer.withDefaults()); // Memastikan CORS ditangani dengan benar

        return http.build();
    }
}