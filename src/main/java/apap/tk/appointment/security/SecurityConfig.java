package apap.tk.appointment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import apap.tk.appointment.security.jwt.JwtTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
            .cors(cors -> cors.configure(http))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/appointment/all").hasAnyAuthority("Admin", "Nurse")
                .requestMatchers("/api/appointment/{id}").hasAnyAuthority("Admin", "Doctor", "Nurse", "Patient")
                .requestMatchers("/api/appointment/doctor/{doctorId}").hasAuthority("Doctor")
                .requestMatchers("/api/appointment/date-range/**").hasAnyAuthority("Admin", "Doctor", "Nurse")
                .requestMatchers("/api/appointment/create").hasAnyAuthority("Admin", "Patient")
                .requestMatchers("/api/appointment/{id}/update").hasAnyAuthority("Admin", "Patient")
                .requestMatchers("/api/appointment/{id}/update-diagnosis-treatment").hasAuthority("Doctor")
                .requestMatchers("/api/appointment/{id}/delete").hasAuthority("Admin")
                .requestMatchers("/api/appointment/patient/{patientId}").hasAnyAuthority("Admin", "Patient")
                

                // Authenticated users for any other endpoints
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            );

        return http.build();
    }
}