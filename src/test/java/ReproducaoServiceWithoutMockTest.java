import br.com.interfaces.model.IMusica;
import br.com.interfaces.model.IUsuario;
import br.com.interfaces.repository.IArtistaRepository;
import br.com.interfaces.repository.IMusicaRepository;
import br.com.interfaces.repository.IUsuarioRepository;
import br.com.interfaces.services.IArtistaService;
import br.com.interfaces.services.IRecomendacaoService;
import br.com.model.Musica;
import br.com.model.Usuario;
import br.com.musicas.reproducao.ReproducaoService;
import br.com.repositories.ArtistaRepository;
import br.com.repositories.MusicaRepository;
import br.com.repositories.UsuarioRepository;
import br.com.services.ArtistaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ufes.gqs.recomendacaoservice.services.RecomendacaoService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ReproducaoServiceWithoutMockTest {
    private ReproducaoService reproducaoService;
    private IArtistaService artistaService;
    private IRecomendacaoService recomendacaoService;
    private IArtistaRepository artistaRepository;
    private IMusicaRepository musicaRepository;
    private IUsuarioRepository usuarioRepository;

    @BeforeEach
    public void setup() {
        artistaRepository = ArtistaRepository.getArtistaRepository();
        musicaRepository = MusicaRepository.getMusicaRepository();
        usuarioRepository = UsuarioRepository.getUsuarioRepository();

        reproducaoService = new ReproducaoService();
        artistaService = new ArtistaService(artistaRepository, musicaRepository);
        recomendacaoService = new RecomendacaoService();
    }

    @Test
    public void deveBuscarBiografiaArtista() {
        try {
            // Given: obtém a lista de músicas de "BlackPink"
            Optional<List<IMusica>> musicas = musicaRepository.getMusicas("BlackPink");

            // Verifica se a lista de músicas está presente e não vazia
            assertTrue(musicas.isPresent() && !musicas.get().isEmpty(), "A lista de músicas não deveria estar vazia.");

            // Pega a primeira música
            IMusica musica = musicas.get().get(0);

            // Define o valor esperado da biografia como vazio
            String retornoEsperado = "Biografia de BlackPink";

            // When: exibe as informações do artista durante a reprodução
            String resultadoObtido = reproducaoService.exibirInformacoesArtistaDuranteReproducao(musica, artistaService);

            // Then: verifica se o retorno está vazio conforme esperado
            assertEquals(retornoEsperado, resultadoObtido, "A biografia deve ser igual à esperada.");

        } catch (Exception e) {
            fail("Não deveria lançar exceção: " + e.getMessage());
        }
    }

    @Test
    public void deveBuscarBiografiaArtistaVazia() {
        // Given: uma instancia de musica inválida e método de obter biografia de artista
        IMusica musica = new Musica("musica", "inexistente", "genero", 10);

        // Supondo que o serviço real retorne vazio para este caso
        String retornoEsperado = "";

        // When: o metodo exibirInformacoesArtistaDuranteReproducao é chamado
        var resultadoObtido = reproducaoService.exibirInformacoesArtistaDuranteReproducao(musica, artistaService);

        // Then: verifica se artistaService retornou string esperada
        assertEquals(retornoEsperado, resultadoObtido);
    }

    @Test
    public void deveBuscarListaDeMusicasRecomendacaoService() {
        // Given: cria instancia de lista de musicas válidas, usuário e método de obter recomendacoes de musicas
        try {
            Optional<List<IMusica>> musicas = musicaRepository.getMusicas("BlackPink");

            // Verifica se a lista de músicas está presente e não vazia
            assertTrue(musicas.isPresent() && !musicas.get().isEmpty(), "A lista de músicas não deveria estar vazia.");

            // Pega a primeira música
            IMusica musica = musicas.get().get(0);

            IUsuario usuario = new Usuario("nome", "email", true, true);
            usuarioRepository.inserir(usuario);

            recomendacaoService.registrarReproducao(musica, usuario);

            // Supondo que o serviço real tenha uma implementação que retorna uma lista de recomendações
            List<IMusica> resultadoObtido = recomendacaoService.recomendarMusicasBaseadoNoHistorico(usuario);

            // Then: verifica se recomendacaoService retornou a lista de musicas esperada
            System.out.println(resultadoObtido);
            assertNotEquals(List.of(), resultadoObtido);
        } catch (Exception e) {
            fail("Não deveria lançar exceção: " + e.getMessage());
        }
    }

    @Test
    public void deveBuscarListaDeMusicasVaziaRecomendacaoService() {
        // Given: cria instancia de lista de musicas vazia, usuário e método de obter recomendacoes de musicas

        IUsuario usuario = new Usuario("", "", true, true);

        // When: o método real retorna uma lista vazia
        List<IMusica> resultadoObtido = recomendacaoService.recomendarMusicasBaseadoNoHistorico(usuario);

        // Then: verifica se recomendacaoService retornou a lista de músicas esperada
        assertTrue(resultadoObtido.isEmpty());
    }

    @Test
    public void naoDeveAtualizarEstatisticaArtistaService() {
        // Given: cria instancia de musica válida
        IMusica musica = new Musica("titulo", "artista", "genero", 10);

        try {
            // Supondo que o serviço real lança uma exceção durante a atualização
            artistaService.atualizarEstatisticasReproducao(musica);
            fail("Deveria ter lançado uma exceção");
        } catch (Exception e) {
            // Then: verifica se a exceção foi lançada corretamente
            assertEquals("Artista artista não existe na plataforma", e.getMessage());
        }
    }
}