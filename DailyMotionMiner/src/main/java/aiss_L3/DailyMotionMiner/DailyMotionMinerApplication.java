package aiss_L3.DailyMotionMiner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
	org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
	org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
public class DailyMotionMinerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailyMotionMinerApplication.class, args);
	}

}
