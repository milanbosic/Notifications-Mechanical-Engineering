package com.vies.notifikacijevesti;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * Created by milan on 10.10.2017..
 */

public class Tab3Predmeti extends Fragment {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    //private List<String> selectedSubjects;
    private Button savebutton;
    private SearchView search;
    private int lastExpandedPosition = -1;
    int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3predmeti, container, false);
        savebutton = rootView.findViewById(R.id.saveButton);

//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(rootView.getContext().SEARCH_SERVICE);
//        search = (SearchView) rootView.findViewById(R.id.search);
//        search.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
//        search.setIconifiedByDefault(false);
//        search.setOnQueryTextListener(this);
//        search.setOnCloseListener(this);

        listView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
        initData();
        listAdapter = new ExpandableListAdapter(this.getContext(), this.listDataHeader, listHash, rootView);
        listView.setAdapter(listAdapter);
        final TinyDB tinyDB = new TinyDB(this.getContext());
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.onButtonPress();
            }
        });

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    listView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
        if (!tinyDB.contains("firstTime")){
            listView.expandGroup(0);
            View view = getActivity().findViewById(R.id.tabs);

            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500);

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity());

            sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                @Override
                public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {

                    if (counter == 2){
                        tinyDB.putBoolean("firstTime", true);
                    }
                    counter++;
                }
            });
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
                            .setContentText("U tabu \"Vesti\" će se pojavljivati nepročitane vesti, a tab \"Istorija\" sadrži istorijat svih pristiglih vesti.")
                            .setDismissOnTouch(true)
                            .withRectangleShape(true)
                            .build()
            );

            sequence.start();
//                    new MaterialShowcaseView.Builder(getActivity())
//                            .setTarget(listView)
//                            .setDismissText("O")
//                            .setContentText("This is some amazing feature you should know about")
//                            .setDelay(300)
//                            .show();
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {



            }
        });

        return rootView;
    }

    private void initData(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        //selectedSubjects = new ArrayList<>();

        listDataHeader.add("Osnovne Akademske Studije");
        listDataHeader.add("Master Akademske Studije");
        listDataHeader.add("Katedre");
//        listDataHeader.add("Ostalo");

        List<String> oas = new ArrayList<>();
        oas.add("Biomehanika lokomotornog sistema (0800)");
        oas.add("Biofizika (0823)");
        oas.add("Goriva i industrijska voda (0888)");
        oas.add("Gorivi tehnički gasovi u procesima zavarivanja (0891)");
        oas.add("Dinamika vozila (0871)");
        oas.add("Elektronika (1071)");
        oas.add("Elektrotehnika (1070)");
        oas.add("Engleski jezik 1 (0506)");
        oas.add("Engleski jezik 2 (0489)");
        oas.add("Estetski dizajn (0988)");
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
        oas.add("Fizika i merenja (0025)");

        List<String> mas = new ArrayList<>();
        mas.add("Aerodinamika velikih brzina (0950)");
        mas.add("Aeroelastičnost (0645)");
        mas.add("Analitička mehanika (0825)");
        mas.add("Automatizacija proizvodnje (0785)");
        mas.add("Biogoriva u procesima sagorevanja (0894)");
        mas.add("Biomehanika tkiva i organa (0559)");
        mas.add("Vazduhoplovni propulzori (0951)");
        mas.add("Eksperimenti i simulacije (0716)");
        mas.add("Električne mašine (0401)");
        mas.add("Elektronika (1073)");
        mas.add("Efektivnost sistema (0711)");
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
        mas.add("Hibridni tehnički sistemi (0966)");


        List<String> kat = new ArrayList<>();
        kat.add("katedra za industrijsko inženjerstvo");
        kat.add("katedra za matematiku");
        kat.add("katedra za mehaniku");
        kat.add("katedra za motorna vozila");
        kat.add("katedra za opšte mašinske konstrukcije");
        kat.add("katedra za termoenergetiku");
        kat.add("katedra za termomehaniku");
        kat.add("katedra za tehnologiju materijala");
        kat.add("katedra za fiziku i elektrotehniku");
        kat.add("katedra za hidraulične mašine i energetske sisteme");


//        List<String> ostalo = new ArrayList<>();
//        ostalo.add("Predmet 1");
//        ostalo.add("Predmet 2");

        listHash.put(listDataHeader.get(0), oas);
        listHash.put(listDataHeader.get(1), mas);
        listHash.put(listDataHeader.get(2), kat);
//        listHash.put(listDataHeader.get(3), ostalo);

    }

//    @Override
//    public boolean onClose() {
//        listAdapter.filterData("");
//        expandAll();
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String query) {
//        listAdapter.filterData(query);
//        expandAll();
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        listAdapter.filterData(query);
//        expandAll();
//        return false;
//    }


}
