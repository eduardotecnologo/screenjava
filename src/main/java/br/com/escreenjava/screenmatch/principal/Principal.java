package br.com.escreenjava.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.github.cdimascio.dotenv.Dotenv;
import br.com.escreenjava.screenmatch.model.DadosEpisodio;
import br.com.escreenjava.screenmatch.model.DadosSerie;
import br.com.escreenjava.screenmatch.model.DadosTemporada;
import br.com.escreenjava.screenmatch.model.Episodio;
import br.com.escreenjava.screenmatch.service.ConsumoApi;
import br.com.escreenjava.screenmatch.service.ConverteDados;
@Component
public class Principal {
    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumo;
    private final ConverteDados conversor;
    private final Dotenv dotenv;

    public Principal(ConsumoApi consumo, ConverteDados conversor, Dotenv dotenv) {
        this.consumo = consumo;
        this.conversor = conversor;
        this.dotenv = dotenv;

}
    public void exibeMenu(){

        System.out.println("Digite o nome da série para buscar:");
        var nomeSerie = leitura.nextLine();

        var ENDERECO = dotenv.get("OMDB_API_URL");
        var API_KEY = dotenv.get("OMDB_API_KEY");
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&apikey=" + API_KEY);

        // Log da resposta JSON
        System.out.println("Resposta JSON: " + json);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        if (dados == null || dados.totalTemporadas() == null) {
            System.err.println("Erro ao obter os dados da série ou total de temporadas não encontrado.");
            return;
        }
        

		List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1; i <= dados.totalTemporadas(); i++) {
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
			DadosTemporada Dadostemporada = conversor.obterDados(json, DadosTemporada.class);

			temporadas.add(Dadostemporada);
		}
        //Lambida
		temporadas.forEach(System.out::println);

        // for(int i = 0; i < dados.totalTemporadas(); i++){
        //     List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
        //     for( int j = 0; j < episodiosTemporada.size(); j++){
        //         System.out.println(episodiosTemporada.get(j).toString());
        //     }
        // }
        //Lambida
        //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
		temporadas.forEach(t -> {
		    List<DadosEpisodio> episodios = t.episodios();
		    if (episodios != null) {
		        episodios.forEach(e -> System.out.println(e.titulo()));
		    } else {
		        System.err.println("Episódios da temporada " + t.numero() + " não encontrados.");
		    }
		});

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
            .flatMap(t -> t.episodios().stream())
            .collect(Collectors.toList());
            System.out.println("\nTop 5 episódios!");

            dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);  

                List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

                episodios.forEach(System.out::println);
            //.toList();
        // dadosEpisodios.add(new DadosEpisodio("teste", 3,"10", "2020-01-01"));
        // dadosEpisodios.forEach(System.out::println);        
        // dadosEpisodios.forEach(System.out::println);
        
    }
}