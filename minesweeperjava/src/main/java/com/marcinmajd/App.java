package com.marcinmajd;

/**
 * Importowanie wszystkich potrzebnych bibliotek do dzialania aplikacji.
 * Importowane sa tu elementy biblioteki JavaFX ktora pozwala na tworzenie aplikacji graficznych w Javie.
 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 *Jest to glowna klasa calej aplikacji w ktorej sa wszystkie inne klasy.
 * <p>
 *     Ta klasa podpieta jest pod rozszerzenie z JavaFX dzieki czemu mozna korzystac z funkcjonalnosci biblioteki.
 *     Znajduja sie tu wszystkie zmienne i klasy i metody potrzebne do funkcjonowania programu.
 * </p>
 */
public class App extends Application {

    private static final int ROZMIAR_KAFELKA=60;
    private static final int SZER=900;
    private static final int WYS=600;
    private static final double PROCENT_BOMB=0.05;
    private static int AKTYWNE_BOMBY=0;


    private static final int ROZMIAR_PLANSZY_X=SZER/ROZMIAR_KAFELKA;
    private static final int ROZMIAR_PLANSZY_Y=WYS/ROZMIAR_KAFELKA;

    private Kafelki[][] grid = new Kafelki[ROZMIAR_PLANSZY_X][ROZMIAR_PLANSZY_Y];

    /**
     * <p>
     *     Metoda createContent() tworzy okno aplikacji.
     *     Nastepnie sa dwie petle:
     *     -Pierwsza wypelnia plansze bombami, ktorych jest taka ilosc procentowa jaka okreslono w zmiennej
     *     -Druga wypelnia pozostale pola planszy, ktore nie sa bombami liczbami informujacymi o tym ile bomb znajduje sie w sasiedztwie
     *     Na koniec metoda zwraca obiekt root
     * </p>
     * @return Zwraca obiekt w ktorym sa dane o planszy gry
     */
    private Parent createContent(){
        Pane root = new Pane();
        root.setPrefSize(SZER,WYS);



        for(int y=0;y<ROZMIAR_PLANSZY_Y;y++){
            for(int x=0;x<ROZMIAR_PLANSZY_X;x++){
                Kafelki kafelek = new Kafelki(x,y,Math.random()<PROCENT_BOMB);

                grid[x][y]=kafelek;
                root.getChildren().add(kafelek);
            }
        }

        for(int y=0;y<ROZMIAR_PLANSZY_Y;y++){
            for(int x=0;x<ROZMIAR_PLANSZY_X;x++){
               Kafelki kafelek = grid[x][y];

               if(kafelek.Bomba){
                   AKTYWNE_BOMBY++;
                   continue;}

               long bombs = znajdzSasiadow(kafelek).stream().filter(k -> k.Bomba).count();
               if(bombs>0)
                kafelek.tekst.setText(String.valueOf(bombs));
            }
        }

        return root;
    }

    /**
     * <p>
     *     Metoda znajdzSasiadow wyszukuje ile bomb znajduje sie na 8 polach otaczajacych kazde pole
     *     Na koniec zwraca obiekt z nowymi danymi
     * </p>
     *
     * @param kafelek jest to obiekt klasy Kafelki
     * @return zwraca obiekt sasiedzi
     */
    private List<Kafelki> znajdzSasiadow(Kafelki kafelek){
        List<Kafelki> sasiedzi = new ArrayList<>();

        int[] points = new int[] {
                -1,-1,
                -1,0,
                -1,1,
                0,-1,
                0,1,
                1,-1,
                1,0,
                1,1
        };

        for(int i=0;i<points.length;i++){
            int dx=points[i];
            int dy=points[++i];

            int newX = kafelek.x+dx;
            int newY = kafelek.y+dy;

            if(newX>=0 && newX<ROZMIAR_PLANSZY_X && newY>=0 && newY<ROZMIAR_PLANSZY_Y){
                sasiedzi.add(grid[newX][newY]);
            }
        }
        return sasiedzi;
    }

    /**
     * jest to klasa testowa, do przeprowadzania testow.
     */
    public static class Test{
        public static String test(){
            String testowy="To Test";

            return testowy;
        }
    }

    /**
     * Jest to klasa ktora odpowiada za to jak beda wyswietlane kafelki, oraz jak beda sie zachowywac przy interakcji z uzytkownikiem.
     */
    public class Kafelki extends StackPane{
        private int x,y;
        private boolean Bomba;
        private boolean czyOtwarte=false;

        private Rectangle obwod = new Rectangle(ROZMIAR_KAFELKA-2,ROZMIAR_KAFELKA-2);
        private Text tekst = new Text();

        /**
         *  Ten obiekt odpowiada za dane aktualnych kafelkow.
         * @param x Wspolrzedna pozioma kafelkow
         * @param y Wspolrzedna pionowa kafelkow
         * @param Bomba Zmienna logiczna przechowujaca dane o tym czy w danym kafelku jest bomba
         */
        public Kafelki(int x, int y, boolean Bomba){
            this.x=x;
            this.y=y;
            this.Bomba=Bomba;

            obwod.setFill(Color.LIGHTGRAY);
            obwod.setStroke(Color.BLUE);

            tekst.setFont(Font.font(30));
            tekst.setText(Bomba ? "O" : "");
            tekst.setVisible(false);


            getChildren().addAll(obwod,tekst);

            setTranslateX(x*ROZMIAR_KAFELKA);
            setTranslateY(y*ROZMIAR_KAFELKA);

            setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    MouseButton button = mouseEvent.getButton();
                   if(button==MouseButton.PRIMARY){
                       open();
                   }
                   if(button==MouseButton.SECONDARY){
                       zaznacz();
                   }
                }
            });
        }

        /**
         * Metoda ktora "otwiera" kafelek przy kliknieciu na niego LPM.
         * Sprawdza ona czy klikniety kafelek jest pusty, czy ma bombe i postepuje wedlug tego jaki jest stan kafelka.
         */
        public void open(){
            if(czyOtwarte){
                return;
            }
            if(Bomba){
                System.out.println("Game Over");
                Platform.exit();

            }
            czyOtwarte=true;
            tekst.setVisible(true);
            obwod.setFill(Color.BLUE);

            if(tekst.getText().isEmpty()) {
                znajdzSasiadow(this).forEach(Kafelki::open);
            }
        }

        /**
         * Metoda odpowiadajaca za zaznaczanie kafelkow bez otwierania ich.
         * Jest uruchamiana kiedy gracz wciska PPM.
         * Metoda ta sprawdza czy zaznaczony kafelek faktycznie posiada bombe i jesli tak to zmniejsza licznik bomb znajdujacych sie w grze.
         *
         * Wyswietla tez informacje o zwyciestwie gdy gracz wygra gre.
         */
        public void zaznacz(){
            if(czyOtwarte){
                return;
            }
            if(Bomba){
                AKTYWNE_BOMBY--;

            }
            obwod.setFill(Color.RED);
            System.out.println("Liczba bomb w grze: "+ AKTYWNE_BOMBY);
            if(AKTYWNE_BOMBY==0){
                for(int y=0;y<ROZMIAR_PLANSZY_Y;y++){
                    for(int x=0;x<ROZMIAR_PLANSZY_X;x++){
                        Kafelki kafelek = grid[x][y];
                        if(y==0){
                            kafelek.tekst.setText("W");
                            kafelek.tekst.setFont(Font.font(30));
                        }
                        if(y==1){
                            kafelek.tekst.setText("Y");
                            kafelek.tekst.setFont(Font.font(30));
                        }
                        if(y==2){
                            kafelek.tekst.setText("G");
                            kafelek.tekst.setFont(Font.font(30));
                        }
                        if(y==3){
                            kafelek.tekst.setText("R");
                            kafelek.tekst.setFont(Font.font(30));
                        }
                        if(y==4){
                            kafelek.tekst.setText("A");
                            kafelek.tekst.setFont(Font.font(30));
                        }
                        if(y==5){
                            kafelek.tekst.setText("L");
                            kafelek.tekst.setFont(Font.font(30));
                        }
                        if(y==6){
                            kafelek.tekst.setText("E");
                            kafelek.tekst.setFont(Font.font(30));
                        }
                        if(y==7){
                            kafelek.tekst.setText("S");
                            kafelek.tekst.setFont(Font.font(30));
                        }
                        if(kafelek.Bomba){
                            kafelek.obwod.setFill(Color.GREEN);
                        }
                    }
                }
                System.out.println("Gratulacje Wygrales :) ");
            }
        }
    }

    /**
     * Funkcja zwiazana z biblioteka JavaFX, ktora odpowiada za uruchomienie i wyswietlenie okna aplikacji dla uzytkownika.
     * @param stage jest to okno ktore ma byc wyswietlone
     */
    @Override
    public void start(Stage stage) {


        Scene scene = new Scene(createContent());

        stage.setTitle("Saper 2020");

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Funkcja main, ktora uruchamia biblioteke JavaFX i cala reszte.
     */
    public static void main(String[] args) {
        launch();
    }

}