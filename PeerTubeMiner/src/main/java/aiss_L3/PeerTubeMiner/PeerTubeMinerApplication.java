package aiss_L3.PeerTubeMiner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { 
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class 
})
public class PeerTubeMinerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeerTubeMinerApplication.class, args);
	}

}
