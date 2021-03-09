package ch.isbsib.sparql.identifiers;

import java.util.Iterator;

import org.eclipse.rdf4j.http.server.readonly.QueryResponder;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.identifiers.api.ApiDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = { "org.eclipse.rdf4j", "ch.isbsib.sparql" })
@Import(QueryResponder.class)
public class Server {
	private static Logger logger = LoggerFactory.getLogger(Server.class);

	@Bean
	public Repository getRepository() {
		logger.info("Starting dao");
		ApiDao dao = new ApiDao();
		logger.info("Creating repository");
		SailRepository sailRepository = new SailRepository(new IdentifiersOrgStore(dao));
		logger.info("Initializing repository");
		sailRepository.init();
		Iterator<String> iris = dao.iris();
		while (iris.hasNext())
			logger.debug(iris.next());
		return sailRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}
}
