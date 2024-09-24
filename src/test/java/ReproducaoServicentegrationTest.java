import br.com.interfaces.model.IMusica;
import br.com.interfaces.model.IUsuario;
import br.com.interfaces.services.IArtistaService;
import br.com.interfaces.services.IRecomendacaoService;
import br.com.model.Musica;
import br.com.model.Usuario;
import br.com.musicas.reproducao.ReproducaoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;


public class ReproducaoServicentegrationTest {
    private ReproducaoService reproducaoService;
    
    @Mock
    private IArtistaService mockArtistaService;
    
    @Mock
    private IRecomendacaoService mockRecomendacaoService;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        reproducaoService = new ReproducaoService();
    }
    
    @Test
    public void deveBuscarBiografiaArtista(){
        // Given: uma instancia de musica válida e método de obter biografia de artista
        IMusica musica = new Musica("titulo", "artista", "genero", 10);
        String retornoEsperado = "Informações do Artista";
        
        when(mockArtistaService.getBiografia(musica.getArtista())).thenReturn(Optional.of(retornoEsperado));
        
        // When: o metodo exibirInformacoesArtistaDuranteReproducao é chamado
        var resultado = reproducaoService.exibirInformacoesArtistaDuranteReproducao(musica, mockArtistaService);
        
        // Then: verifica se artistaService retornou string esperada e se o metodo foi chamado
        assertEquals(retornoEsperado, resultado);
        verify(mockArtistaService, Mockito.times(1)).getBiografia(musica.getArtista());
    }
    
    @Test
    public void deveBuscarBiografiaArtistaVazia(){
        // Given: uma instancia de musica válida e método de obter biografia de artista
        IMusica musica = new Musica("titulo", "artista", "genero", 10);
        String retornoEsperado = "";
        
        when(mockArtistaService.getBiografia(musica.getArtista())).thenReturn(Optional.of(retornoEsperado));
        
        // When: o metodo exibirInformacoesArtistaDuranteReproducao é chamado
        var resultado = reproducaoService.exibirInformacoesArtistaDuranteReproducao(musica, mockArtistaService);
        
        // Then: verifica se artistaService retornou string esperada e se o metodo foi chamado
        assertEquals(retornoEsperado, resultado);
        verify(mockArtistaService, Mockito.times(1)).getBiografia(musica.getArtista());
    }
    
    @Test
    public void deveBuscarListaDeMusicasRecomendacaoService(){
        // Given: cria instancia de lista de musicas válidas, usuário e método de obter recomendacoes de musicas
        IMusica musica1 = new Musica("titulo1", "artista1", "genero1", 10);
        IMusica musica2 = new Musica("titulo2", "artista2", "genero2", 8);
        List<IMusica> listaEsperada = new ArrayList<IMusica>();
        listaEsperada.add(musica1);
        listaEsperada.add(musica2);
        
        IUsuario usuario = new Usuario("nome", "email");
        
        when(mockRecomendacaoService.recomendarMusicasBaseadoNoHistorico(usuario)).thenReturn(listaEsperada);
        
        // When: o metodo obterRecomendacoesDuranteReproducao é chamado
        var resultado = reproducaoService.obterRecomendacoesDuranteReproducao(mockRecomendacaoService, usuario);
        
        // Then: verifica se recomendacaoService retornou a lista de musicas esperada e se o metodo foi chamado
        assertEquals(listaEsperada, resultado);
        verify(mockRecomendacaoService, Mockito.times(1)).recomendarMusicasBaseadoNoHistorico(usuario);
    }
    
    @Test
    public void deveBuscarListaDeMusicasVaziaRecomendacaoService(){
        // Given: cria instancia de lista de musicas válidas, usuário e método de obter recomendacoes de musicas
        List<IMusica> listaEsperada = new ArrayList<IMusica>();
        
        IUsuario usuario = new Usuario("nome", "email");
        
        when(mockRecomendacaoService.recomendarMusicasBaseadoNoHistorico(usuario)).thenReturn(listaEsperada);
        
        // When: o metodo obterRecomendacoesDuranteReproducao é chamado
        var resultado = reproducaoService.obterRecomendacoesDuranteReproducao(mockRecomendacaoService, usuario);
        
        // Then: verifica se recomendacaoService retornou a lista de musicas esperada e se o metodo foi chamado
        assertEquals(listaEsperada, resultado);
        assertTrue(resultado.isEmpty());
        verify(mockRecomendacaoService, Mockito.times(1)).recomendarMusicasBaseadoNoHistorico(usuario);
    }
}