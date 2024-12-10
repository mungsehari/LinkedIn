package com.hari.configuration;


import com.hari.features.authentication.model.AuthUser;
import com.hari.features.authentication.repository.UserRepository;
import com.hari.features.authentication.utils.Encoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabaseConfiguration {
    private final Encoder encoder;

    public LoadDatabaseConfiguration(Encoder encoder) {
        this.encoder = encoder;
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository){
        return args -> {
            AuthUser authUser1=new AuthUser( "hari@gmail.com", encoder.encode("123"));
            userRepository.save(authUser1);

        };

    }
}
