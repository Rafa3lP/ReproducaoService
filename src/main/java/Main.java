import br.com.interfaces.model.IMusica;
import br.com.interfaces.model.IPlaylist;
import br.com.interfaces.model.IUsuario;
import br.com.model.Musica;
import br.com.model.Playlist;
import br.com.model.Usuario;
import br.com.musicas.reproducao.ReproducaoService;

/**
 *
 * @author nitro5
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        IMusica musica1 = new Musica("musica 1", "Artista 1", "genero 1", 10);
        IMusica musica2 = new Musica("musica 2", "Artista 2","genero 2", 5);
        IMusica musica3 = new Musica("musica 3", "Artista 3", "genero 3", 7);

        IPlaylist playlist = new Playlist("playlist");
        playlist.adicionarMusica(musica1);
        playlist.adicionarMusica(musica2);
        playlist.adicionarMusica(musica3);

        IUsuario usuario = new Usuario("Fulano", "email do fulano");

        ReproducaoService reprodutor = new ReproducaoService();
        
        reprodutor.reproduzirPlayList(playlist, usuario, null);

        Thread.sleep(3000); 
        reprodutor.pausarReproducao(usuario);

        Thread.sleep(2000);
        reprodutor.retomarReproducao(usuario);

        Thread.sleep(12000); 
        reprodutor.pararReproducao(usuario);
    }
}