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

public class ReproducaoService implements IReproducaoService {
    private Queue<IMusica> filaDeReproducao = new LinkedList<>();
    private IMusica musicaAtual;
    private boolean isPaused = false;
    private boolean isStopped = false; 
    private IUsuario usuarioAtual;
    private Thread threadReproducao;

    @Override
    public void reproduzirMusica(IMusica im, IUsuario iu) {
        usuarioAtual = iu;
        filaDeReproducao.clear();
        filaDeReproducao.add(im);
        tocarProximaMusica();
    }

    @Override
    public void reproduzirPlayList(IPlaylist ip, IUsuario iu, IArtistaService ias) {
        usuarioAtual = iu;
        filaDeReproducao.clear();
        filaDeReproducao.addAll(ip.getMusicas());
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
            tocarProximaMusica();
        });
        threadReproducao.start();
    }

    @Override
    public void pausarReproducao(IUsuario iu) {
        if (musicaAtual != null && !isPaused) {
            isPaused = true;
            System.out.println("Reprodução pausada para o usuário " + iu.getNome() + ": " + musicaAtual.getTitulo());
        }
    }

    @Override
    public void retomarReproducao(IUsuario iu) {
        if (musicaAtual != null && isPaused) {
            isPaused = false;
            synchronized (this) {
                notify(); 
            }
            System.out.println("Reprodução retomada para o usuário " + iu.getNome() + ": " + musicaAtual.getTitulo());
        }
    }

    @Override
    public void pararReproducao(IUsuario iu) {
        if (musicaAtual != null) {
            isStopped = true;
            System.out.println("Reprodução parada para o usuário " + iu.getNome() + ": " + musicaAtual.getTitulo());
            if (threadReproducao != null && threadReproducao.isAlive()) {
                threadReproducao.interrupt(); 
            }
            tocarProximaMusica(); 
        }
    }

    @Override
    public List<IMusica> obterRecomendacoesDuranteReproducao(IRecomendacaoService irs, IUsuario iu) {
        //chamada externa
        return null;
    }

    @Override
    public String exibirInformacoesArtistaDuranteReproducao(IMusica im, IArtistaService ias) {
        //chamada externa
        return null;
    }
}