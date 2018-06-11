package com.vies.notifikacijevesti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * Tab that shows a list of all eligible subjects to which you can subscribe/unsubscribe
 * and receive relevant notifications
 */

public class Tab3Predmeti extends Fragment {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    private int lastExpandedPosition = -1;
    private TinyDB tinyDB;

    int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3predmeti, container, false);
        // Save button for sending an HTTP request and saving the list of subscriptions
        Button savebutton = rootView.findViewById(R.id.saveButton);

        listView = rootView.findViewById(R.id.lvExp);

        tinyDB = new TinyDB(this.getContext());

        initData();

        listAdapter = new ExpandableListAdapter(this.getContext(), this.listDataHeader, listHash, rootView);
        listView.setAdapter(listAdapter);


        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.onButtonPress();
            }
        });

        // Set a GroupExpandListener in order to collapse other groups on click
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    listView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        // Check if an app has been started once, if not show the tutorial sequence
        if (!tinyDB.contains("firstTime")){
            listView.expandGroup(0);
            View view = getActivity().findViewById(R.id.tabs);

            // Tutorial sequence to introduce the user
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity());

            sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                @Override
                public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {

                    // If sequence counter has reached the third slide, update local storage
                    if (counter == 2){
                        tinyDB.putBoolean("firstTime", true);
                    }
                    counter++;
                }
            });

            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500);

            sequence.setConfig(config);

            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(listView)
                            .setContentText("Prvo obeležite predmete za koje želite da vam stižu notifikacije.")
                            .setDismissOnTouch(true)
                            .build()
            );
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(savebutton)
                            .setContentText("Zatim dodirnete dugme da biste sačuvali izbor.")
                            .setDismissOnTouch(true)
                            .build()
            );
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(view)
                            .setContentText("U tabu \"Nepročitano\" će se pojavljivati nepročitane vesti, a tab \"Istorija\" sadrži istorijat svih pristiglih vesti.")
                            .setDismissOnTouch(true)
                            .withRectangleShape(true)
                            .build()
            );

            sequence.start();
        }

        return rootView;
    }

    // Initialize expandable list data
    private void initData(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Osnovne Akademske Studije");
        listDataHeader.add("Master Akademske Studije");
        listDataHeader.add("Katedre");

        // If the first list doesn't exist in the database, set default values
        List<String> oas;
        List<String> mas;
        List<String> kat;
        if (!tinyDB.contains("oas")) {
            Log.d("DB_SUBJECTS", "no subjects in database, creating new.");

            oas = new ArrayList<>();
            oas.add("Biomehanika lokomotornog sistema (0800)");
            oas.add("Biofizika (0823)");
            oas.add("Dinamika vozila (0871)");
            oas.add("Elektronika (1071)");
            oas.add("Elektrotehnika (1070)");
            oas.add("Engleski jezik 1 (0506)");
            oas.add("Engleski jezik 2 (0489)");
            oas.add("Estetski dizajn (0988)");
            oas.add("Fizika i merenja (0025)");
            oas.add("Goriva i industrijska voda (0888)");
            oas.add("Gorivi tehnički gasovi u procesima zavarivanja (0891)");
            oas.add("Inženjerska grafika (0572)");
            oas.add("Kompjuterska grafika (0663)");
            oas.add("Kompjutersko modeliranje i animacija (0591)");
            oas.add("Konstruktivna geometrija i grafika (0203)");
            oas.add("Konstrukcija i tehnologija proizvodnje letelica (0253)");
            oas.add("Matematika 1 (0669)");
            oas.add("Matematika 2 (0671)");
            oas.add("Matematika 3 (0672)");
            oas.add("Mašine alatke (0916)");
            oas.add("Mašinski elementi 1 (0045)");
            oas.add("Mašinski elementi 1 (1090)");
            oas.add("Mašinski elementi 2 (0046)");
            oas.add("Mašinski elementi 2 (1091)");
            oas.add("Mašinski materijali 1 (0883)");
            oas.add("Mašinski materijali 2 (0884)");
            oas.add("Mašinsko inženjerstvo u praksi (0879)");
            oas.add("Mehanika 1 (0001)");
            oas.add("Mehanika 2 (0002)");
            oas.add("Mehanika 3 (0799)");
            oas.add("Modeliranje komponenata mašina (0962)");
            oas.add("Modeliranje oblika (0088)");
            oas.add("Numeričke metode (0673)");
            oas.add("Objektno orijentisano programiranje i JAVA (0674)");
            oas.add("Obnovljivi izvori energije - biomasa (0969)");
            oas.add("Osnove biomedicinskog inženjerstva (0723)");
            oas.add("Osnove motornih vozila (0869)");
            oas.add("Osnove parnih kotlova (0090)");
            oas.add("Osnove solarnih sistema (1065)");
            oas.add("Osnove tehnike hlađenja (0029)");
            oas.add("Osnove tehničkih inovacija (0961)");
            oas.add("Osnove tehničkih inovacija (1092)");
            oas.add("Osnove turbomašina (0999)");
            oas.add("Osnovi konstruisanja sistema naoružanja (0408)");
            oas.add("Osnovi otpornosti konstrukcija (0021)");
            oas.add("Osnovi prenošenja toplote (0532)");
            oas.add("Osnovi sociologije i ekonomije (0405)");
            oas.add("Otpornost materijala (0020)");
            oas.add("Pogon i oprema letelica (1076)");
            oas.add("Pogonski materijali (0054)");
            oas.add("Poslovno-proizvodni informacioni sistemi (0412)");
            oas.add("Postupci zavarivanja B (0890)");
            oas.add("Primenjena termodinamika (0215)");
            oas.add("Programiranje (0670)");
            oas.add("Proračun strukture letelica (0945)");
            oas.add("Računarski alati (0016)");
            oas.add("Računarski alati (0930)");
            oas.add("Reparacija mašinskih delova i konstrukcija (0887)");
            oas.add("Sagorevanje B (0968)");
            oas.add("Stacionarni problemi prostiranja toplote (0668)");
            oas.add("Stručna praksa B - VAZ (0398)");
            oas.add("Stručna praksa B - DUM (0482)");
            oas.add("Stručna praksa B - DUM (1093)");
            oas.add("Stručna praksa B - MOT (0847)");
            oas.add("Stručna praksa B - MFB (0715)");
            oas.add("Stručna praksa B - PRO (0576)");
            oas.add("Stručna praksa B - HEN (0925)");
            oas.add("Teorija oscilacija (0012)");
            oas.add("Termodinamika B (0372)");
            oas.add("Tehnologija mašinske obrade (0065)");
            oas.add("Uvod u verovatnoću i statistiku (0543)");
            oas.add("Uvod u energetiku (0406)");
            oas.add("Uvod u industrijsko inženjerstvo  (0209)");
            oas.add("Uvod u sisteme naoružanja (0210)");

            mas = new ArrayList<>();
            mas.add("Aerodinamika velikih brzina (0950)");
            mas.add("Aeroelastičnost (0645)");
            mas.add("Analitička mehanika (0825)");
            mas.add("Automatizacija proizvodnje (0785)");
            mas.add("Biogoriva u procesima sagorevanja (0894)");
            mas.add("Biomehanika tkiva i organa (0559)");
            mas.add("Eksperimenti i simulacije (0716)");
            mas.add("Električne mašine (0401)");
            mas.add("Elektronika (1073)");
            mas.add("Efektivnost sistema (0711)");
            mas.add("Hibridni tehnički sistemi (0966)");
            mas.add("Industrijski roboti (0290)");
            mas.add("Inovativni dizajn tehničkih sistema (0964)");
            mas.add("Informacione tehnologije u medicini (0393)");
            mas.add("Informacione tehnologije u medicini (0938)");
            mas.add("Informacione tehnologije u medicini (1010)");
            mas.add("Kompjuterska simulacija u automatizaciji proizvodnje (0722)");
            mas.add("Komponente rashladnih uređaja (0291)");
            mas.add("Mašine alatke i roboti nove generacije (0919)");
            mas.add("Mašine alatke M (0920)");
            mas.add("Mašinski materijali 3 (0892)");
            mas.add("Metalurgija zavarivanja (0901)");
            mas.add("Mehanika kontinuuma (0826)");
            mas.add("Mehanika loma i integritet konstrukcija (0896)");
            mas.add("Mehanika M (0004)");
            mas.add("Mehanika robota (0007)");
            mas.add("Mehatronika (0201)");
            mas.add("Mikro obrada i karakterizacija (0601)");
            mas.add("Modeliranje i proračun struktura (1098)");
            mas.add("Noseći sistemi vozila (0441)");
            mas.add("Obezbeđenje i kontrola kvaliteta zavarenih spojeva (0720)");
            mas.add("Održavanje letelica (0952)");
            mas.add("Oštećenja tehničkih sistema (0717)");
            mas.add("Pogonski materijali 2 (0893)");
            mas.add("Ponašanje zavarenih spojeva u eksploataciji (0719)");
            mas.add("Postupci zavarivanja M (0899)");
            mas.add("Pouzdanost konstrukcija (0486)");
            mas.add("Pouzdanost prenosnika (0556)");
            mas.add("Prenošenje količine toplote (0478)");
            mas.add("Prenošenje toplote i supstancije (0350)");
            mas.add("Primenjena aerodinamika (0946)");
            mas.add("Primenjena teorija plastičnosti i pogonska čvrstoća (1032)");
            mas.add("Programabilni sistemi upravljanja (0904)");
            mas.add("Projektovanje vozila  (0437)");
            mas.add("Projektovanje letelica (0136)");
            mas.add("Proračunska aerodinamika (1078)");
            mas.add("Rashladna postrojenja (0192)");
            mas.add("Sagorevanje M (0971)");
            mas.add("Softverski alati u dizajnu (0963)");
            mas.add("Strukturalna analiza (0947)");
            mas.add("Stručna praksa M - PRO (0788)");
            mas.add("Teorija oscilacija (0037)");
            mas.add("Teorija turbomašina (1000)");
            mas.add("Termodinamika M (0202)");
            mas.add("Tehnika merenja i senzori (0647)");
            mas.add("Tehnika merenja i senzori (0926)");
            mas.add("Toplotne pumpe (1050)");
            mas.add("Vazduhoplovni propulzori (0951)");

            kat = new ArrayList<>();
            kat.add("katedra za industrijsko inženjerstvo");
            kat.add("katedra za fiziku i elektrotehniku");
            kat.add("katedra za hidraulične mašine i energetske sisteme");
            kat.add("katedra za matematiku");
            kat.add("katedra za mehaniku");
            kat.add("katedra za motorna vozila");
            kat.add("katedra za opšte mašinske konstrukcije");
            kat.add("katedra za termoenergetiku");
            kat.add("katedra za termomehaniku");
            kat.add("katedra za tehnologiju materijala");

            tinyDB.putListString("oas", (ArrayList<String>) oas);
            tinyDB.putListString("mas", (ArrayList<String>) mas);
            tinyDB.putListString("kat", (ArrayList<String>) kat);

        } else{
            Log.d("DB_SUBJECTS", "subjects exist in database");

            oas = tinyDB.getListString("oas");
            mas = tinyDB.getListString("mas");
            kat = tinyDB.getListString("kat");
        }

        listHash.put(listDataHeader.get(0), oas);
        listHash.put(listDataHeader.get(1), mas);
        listHash.put(listDataHeader.get(2), kat);

    }

    /**
     * Call adapter's onStop method to cancel any pending requests
     */
    public void onStop(){
        super.onStop();
        listAdapter.onStop();
    }

}
