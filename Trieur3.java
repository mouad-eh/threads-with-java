import java.util.concurrent.Semaphore;

public class Trieur3 extends Thread {
    private int[] t;
    private int debut, fin;
    private Trieur3 parent;
    private int nbNotify = 0;
    private Semaphore s;

    public Trieur3(int[] t) {
        this(null, t, 0, t.length - 1,null);
    }

    private Trieur3(Trieur3 parent, int[] t, int debut, int fin, Semaphore s) {
        this.parent = parent;
        this.t = t;
        this.debut = debut;
        this.fin = fin;
        this.s = s;
        start();
    }

    public void notifier() {
        this.nbNotify++;
    }

    public void run() {
        if (fin - debut < 2) {
            if (t[debut] > t[fin]) {
                echanger(debut, fin);
            }
        } else {
            Semaphore ss = new Semaphore(1);
            int milieu = debut + (fin - debut) / 2;
            Trieur3 trieur1 = new Trieur3(this, t, debut, milieu,ss);
            Trieur3 trieur2 = new Trieur3(this, t, milieu + 1, fin,ss);
            try{
                while(true){
                    ss.acquire();
                    int nb = nbNotify;
                    ss.release();
                    if( nb >=2 ){
                        break;
                    }
                }
            }catch(InterruptedException e){
            }
            triFusion(debut, fin);
        }
        if (parent != null) {
            try{
                s.acquire();
                parent.notifier();
                s.release();
            }catch(InterruptedException e) {
            }
        }
    }

    private void echanger(int i, int j) {
        int valeur = t[i];
        t[i] = t[j];
        t[j] = valeur;
    }

    private void triFusion(int debut, int fin) {

        int[] tFusion = new int[fin - debut + 1];
        int milieu = (debut + fin) / 2;
        int i1 = debut, i2 = milieu + 1;
        // indice de la prochaine case du tableau tFusion à remplir
        int iFusion = 0;
        while (i1 <= milieu && i2 <= fin) {
            if (t[i1] < t[i2]) {
                tFusion[iFusion++] = t[i1++];
            } else {
                tFusion[iFusion++] = t[i2++];
            }
        }
        if (i1 > milieu) {
            for (int i = i2; i <= fin;) {
                tFusion[iFusion++] = t[i++];
            }
        } else { // la 2ème tranche est épuis
            for (int i = i1; i <= milieu;) {
                tFusion[iFusion++] = t[i++];
            }
        }
        for (int i = 0, j = debut; i <= fin - debut;) {
            t[j++] = tFusion[i++];
        }
    }

    public static void main(String[] args) {
        int[] t = { 5, 8, 3, 2, 7, 10, 1 };
        Trieur3 trieur = new Trieur3(t);
        try {
            trieur.join();
        } catch (InterruptedException e) {
        }
        for (int i = 0; i < t.length; i++) {
            System.out.print(t[i] + ";");
        }
        System.out.println();
    }
}