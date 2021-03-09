package ch.isbsib.sparql.identifiers;

import org.eclipse.rdf4j.http.server.readonly.QueryResponder;
import org.eclipse.rdf4j.http.server.readonly.ReadOnlySparqlApplication;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.identifiers.api.ApiDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"org.eclipse.rdf4j" , "ch.isbsib.sparql"})
@Import(QueryResponder.class)
public class Server {
	
	@Bean
	public Repository getRepository() {
		ApiDao dao = new ApiDao();
		SailRepository sailRepository = new SailRepository(new IdentifiersOrgStore(dao));
		sailRepository.init();
		return sailRepository;
	}
	
	public static void main(String[] args) {
		System.err.println(args);
		SpringApplication.run(Server.class, args);
	}
}
