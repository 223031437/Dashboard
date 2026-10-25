
package za.co.dbamonitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/connections/test",
                                "/api/monitoring/overview",
                                "/api/monitoring/hardware-memory",
                                "/api/monitoring/long-running-queries",
                                "/api/monitoring/active-sessions",
                                "/api/monitoring/sql-agent-jobs",
                                "/api/monitoring/database-metrics",
                                "/api/monitoring/missing-indexes",
                                "/api/monitoring/wait-stats",
                                "/api/monitoring/blocking-sessions",
                                "/api/monitoring/top-queries",
                                "/api/monitoring/database-io",
                                "/api/monitoring/backup-status"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> { })
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
