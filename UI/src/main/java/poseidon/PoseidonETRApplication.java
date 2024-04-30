package poseidon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Scanner;

@SpringBootApplication
@EntityScan(basePackages = "poseidon.DTO")
public class PoseidonETRApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoseidonETRApplication.class, args);
    }
}
