package br.com.escreenjava.screenmatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import br.com.escreenjava.screenmatch.principal.Principal;

@SpringBootApplication
public class ScreenmatchApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ScreenmatchApplication.class, args);
        Principal principal = context.getBean(Principal.class);
        principal.exibeMenu();
    }
}
