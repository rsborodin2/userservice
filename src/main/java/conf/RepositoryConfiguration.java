package conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "rborodin.skillgram.userservice.repository")
public class RepositoryConfiguration {
}