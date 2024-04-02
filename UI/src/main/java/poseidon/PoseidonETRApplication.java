package poseidon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Scanner;

@SpringBootApplication
@EntityScan(basePackages = "poseidon.DTO")
public class PoseidonETRApplication {

    public static void main(String[] args) {
		/*String prop = System.getProperty("spring.datasource.username");
		if (prop == null){
			Scanner myObj = new Scanner(System.in);
			System.out.println("Enter data source username: ");

			prop = myObj.nextLine();
		}
		System.setProperty("spring.datasource.username", prop);

		prop = System.getProperty("spring.datasource.password");
		if (prop == null){
			Scanner myObj = new Scanner(System.in);
			System.out.println("Enter data source password: ");

			prop = myObj.nextLine();
		}
		System.setProperty("spring.datasource.password", prop);*/
        SpringApplication.run(PoseidonETRApplication.class, args);
    }
}
