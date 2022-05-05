package ch.isbsib.sparql.identifiers;

import org.eclipse.rdf4j.http.server.readonly.QueryResponder;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.identifiers.api.ApiDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = { "org.eclipse.rdf4j", "org.example" })
@Import(QueryResponder.class)
public class Server {
	@Bean
	public Repository getRepository(){
		IdentifiersOrgStore store = new IdentifiersOrgStore(new ApiDao());
		SailRepository sailRepository = new SailRepository(store);
		sailRepository.init();
		return sailRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}
}
