package br.com.musicas.reproducao;

import br.com.interfaces.model.IMusica;
import br.com.interfaces.model.IPlaylist;
import br.com.interfaces.model.IUsuario;
import br.com.interfaces.services.IArtistaService;
import br.com.interfaces.services.IRecomendacaoService;
import br.com.interfaces.services.IReproducaoService;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReproducaoService implements IReproducaoService {
    private Queue<IMusica> filaDeReproducao = new LinkedList<>();
    private IMusica musicaAtual;
    private boolean isPaused = false;
    private boolean isStopped = false;
    private IUsuario usuarioAtual;
    private Thread threadReproducao;
    private IArtistaService artistaService;

    @Override
    public void reproduzirMusica(IMusica musica, IUsuario usuario) {
        usuarioAtual = usuario;
        filaDeReproducao.clear();
        filaDeReproducao.add(musica);
        tocarProximaMusica();
    }

    @Override
    public void reproduzirPlayList(IPlaylist playlist, IUsuario usuario, IArtistaService artistaService) {
        usuarioAtual = usuario;
        this.artistaService = artistaService;
        filaDeReproducao.clear();
        filaDeReproducao.addAll(playlist.getMusicas());
        tocarProximaMusica();
    }

    private void tocarProximaMusica() {
        if (musicaAtual != null) {
            System.out.println("Música anterior finalizada.");
        }

        if (!filaDeReproducao.isEmpty()) {
            musicaAtual = filaDeReproducao.poll();
            isPaused = false;
            isStopped = false;
            System.out.println("Usuário " + usuarioAtual.getNome() + " está ouvindo: " + musicaAtual.getTitulo() + " por " + musicaAtual.getArtista());
            iniciarReproducao();
        } else {
            System.out.println("Fila de reprodução vazia.");
        }
    }

    private void iniciarReproducao() {
        if (threadReproducao != null && threadReproducao.isAlive()) {
            threadReproducao.interrupt(); 
        }

        threadReproducao = new Thread(() -> {
            int duracao = (int) musicaAtual.getDuracao(); 
            for (int segundo = 1; segundo <= duracao; segundo++) {
                if (isStopped) {
                    System.out.println("Reprodução interrompida.");
                    return;
                }
                
                synchronized (this) {
                    while (isPaused) {
                        try {
                            wait(); 
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }

                try {
                    System.out.println("Tocando " + musicaAtual.getTitulo() + " - Tempo: " + (segundo / 60) + " min " + (segundo % 60) + " seg");
                    Thread.sleep(1000); 
                } catch (InterruptedException e) {
                    return;
                }
            }
            System.out.println("Música " + musicaAtual.getTitulo() + " finalizada.");
            try {
                this.artistaService.atualizarEstatisticasReproducao(musicaAtual);
            } catch (Exception ex) {
                System.out.print(ReproducaoService.class.getName() + ": Falha ao atualizar estatisticas de reprodução. ");
            }
            tocarProximaMusica();
        });
        threadReproducao.start();
    }

    @Override
    public void pausarReproducao(IUsuario usuario) {
        if (musicaAtual != null && !isPaused) {
            isPaused = true;
            System.out.println("Reprodução pausada para o usuário " + usuario.getNome() + ": " + musicaAtual.getTitulo());
        }
    }

    @Override
    public void retomarReproducao(IUsuario usuario) {
        if (musicaAtual != null && isPaused) {
            isPaused = false;
            synchronized (this) {
                notify(); 
            }
            System.out.println("Reprodução retomada para o usuário " + usuario.getNome() + ": " + musicaAtual.getTitulo());
        }
    }

    @Override
    public void pararReproducao(IUsuario usuario) {
        if (musicaAtual != null) {
            isStopped = true;
            System.out.println("Reprodução parada para o usuário " + usuario.getNome() + ": " + musicaAtual.getTitulo());
            if (threadReproducao != null && threadReproducao.isAlive()) {
                threadReproducao.interrupt(); 
            }
            tocarProximaMusica(); 
        }
    }

    @Override
    public List<IMusica> obterRecomendacoesDuranteReproducao(IRecomendacaoService recomendacaoService, IUsuario usuario) {
        return recomendacaoService.recomendarMusicasBaseadoNoHistorico(usuario);
    }

    @Override
    public String exibirInformacoesArtistaDuranteReproducao(IMusica musica, IArtistaService artistaService) {
        return artistaService.getBiografia(musica.getArtista()).orElse("");
    }
}