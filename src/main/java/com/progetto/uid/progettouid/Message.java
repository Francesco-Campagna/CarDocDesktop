package com.progetto.uid.progettouid;

public class Message {
    public final static String login_error = "";
    public final static String cart_error = "Errore nel caricamento del carrello";
    public final static String returnHome_error = "Errore nel ritorno alla home";
    public final static String recovery_password_error = "Errore nel caricamento della pagina recupero password";
    public final static String load_page_error = "Errore nel caricamento della pagina";
    public final static String wishlist_error = "Errore wishlist";
    public final static String box_page_error = "Errore box page";
    public final static String not_logged_in_error = "Effettuare il login prima di accedere a questa sezione!";
    public final static String privacy_information_error = "Errore nell'apertura di informazioni sulla privacy";
    public final static String general_condition_error = "Errore nell'apertura delle condizioni generali di uso e vendita";
    public final static String add_home_page_product_error = "Errore nell'aggiunta dei prodotti alla home";
    public final static String registration_field_empty_error = "Compilare tutti i campi per proseguire con la registrazione";
    public final static String registration_email_error = "Inserire un email valida";
    public final static String registration_password_error = "Le password inserite non coincidono";
    public final static String registration_password_length_error = "La passowrd inserita non è valida, assicurarsi che la lunghezza sia di almeno 6 caratteri";
    public final static String registration_email_exist_error = "L'indirizzo email inserito è associato a un altro account, effettuare il login";
    public final static String load_product_view_error = "Errore nel caricamento della pagina del prodotto";
    public final static String no_car_selected_error = "Nessun modello di auto selezionato";
    public final static String exists_car_selected_error = "L'auto selezionata è già presente nel garage";
    public final static String add_cart_information = "Attenzione è probabile che non tutti i prodotti siano stati aggiunti al carrello a causa del raggiungimento della capienza massima";
    public final static String add_wishlist_max_information = "Capienza massima wishlist";
    public final static String add_wishlist_find_information = "Il prodotto è già presente nella tua wishlist";
    public final static String upgrade_information = "Effettua ora l'upgrade a premium! Stiamo lavorando per inserire nuovi veicoli selezionabili nel tuo garage per un'esperienza personalizzata.";
    public final static String upgrade_information_error = "Errore nell'apertura delle informazioni sull'upgrade";
    public final static String address_error = "Inserire un indirizzo valido";
    public final static String update_password_success = "La password è stata modificata";
    public final static String add_coupon_error = "Il coupon inserito non è valido o già utilizzato";
    public final static String add_coupon_success = "Il coupon è stato inserito correttamente";
    public final static String insufficient_balance_error = "Non è possibile procedere con l'ordine poichè il saldo è insufficiente, aggiornare il saldo nella sezione account";
    public final static String order_success = "Ordine completato con successo";
    public final static String empty_cart = "Il carrello è vuoto, inserire dei prodotti per proseguire con l'ordine";
    public final static String not_logged_cart_error = "Effettuare il login prima di poter effettuare l'ordine!";
    public final static String recovery_password_email_message = "Abbiamo inviato un email all'indirizzo associato al tuo account, segui le istruzioni per proseguire con il reset della password";
    public final static String thread_error = "Errore nell'esecuzione del thread";

    public final static String add_car_information = """
        Per aggiungere un nuovo veicolo inviare i dati relativo ad esso ad uno dei seguenti indirizzi email:
        
        francesco.campa2010@libero.it
        ctldfederico@gmail.com
        pietrocimino@gmail.com
        """;

    public final static String recovery_password_information = """
            Per proseguire con il recupero password, richiedere informazioni/assistenza ecc. inviare un'email ad uno dei seguenti indirizzi:
            
            francesco.campa2010@libero.it
            ctldfederico@gmail.com
            pietrocimino@gmail.com
            """;

    public final static String app_information = """
        CarDoc è uno shop di articoli per auto. Sviluppato in Java per il corso di User Interface Design.
        Sviluppatori:
        
            Francesco Campagna
            Cataldo Federico
            Pietro Cimino
            """;
    public final static String app_information_error = "Errore nell'apertura delle informazioni sull'app";

    public final static String privacy_information = """
            1) Raccolta dei dati personali:
            Raccogliamo i tuoi dati personali, come nome, indirizzo email, 
            indirizzo di fatturazione e spedizione, numero di telefono e 
            dati di pagamento, al fine di elaborare gli ordini, effettuare
            consegne, fornire assistenza clienti e personalizzare la tua
            esperienza di shopping.
                        
            2) Utilizzo dei dati personali:
            Utilizziamo i tuoi dati personali per elaborare gli ordini,
            comunicare informazioni relative agli ordini, fornire assistenza
            clienti, personalizzare l'esperienza di shopping e migliorare
            i nostri servizi.
                        
            3) Condivisione dei dati personali:
            Non condividiamo i tuoi dati personali con terze parti senza il
            tuo consenso, tranne per fornitori di servizi di fiducia che ci
            assistono nell'elaborazione degli ordini e per adempiere
            a obblighi legali.
                        
            4) Sicurezza dei dati:
            Implementiamo misure di sicurezza per proteggere i tuoi dati
            personali da accessi non autorizzati o divulgazioni.
            """;

    public final static String general_condition = """
            1) Accettazione delle condizioni:
            Utilizzando il nostro shop online e effettuando acquisti,
            accetti di essere vincolato dalle presenti Condizioni Generali
            di Uso e Vendita. Ti invitiamo a leggerle attentamente prima
            di procedere con l'acquisto.
                        
            2) Utilizzo del sito:
            Il nostro shop online è destinato esclusivamente all'utilizzo
            personale e non commerciale. Non è consentito modificare,
            riprodurre, duplicare o distribuire il contenuto del sito
            senza il nostro consenso scritto.
                        
            3) Proprietà intellettuale:
            Tutti i diritti di proprietà intellettuale relativi al
            contenuto del nostro shop online, inclusi testi, grafiche,
            loghi, immagini e software, sono di nostra proprietà o di
            terze parti autorizzate. È vietata la riproduzione o
            l'utilizzo non autorizzato di tali materiali.
                        
            4) Prodotti e prezzi:
            Forniamo descrizioni accurate dei prodotti disponibili
            nel nostro shop online. Tuttavia, non possiamo garantire
            che le informazioni siano sempre complete, accurate o
            aggiornate. I prezzi dei prodotti sono indicati in valuta
            locale e possono essere soggetti a modifiche senza preavviso.
            """;
}
