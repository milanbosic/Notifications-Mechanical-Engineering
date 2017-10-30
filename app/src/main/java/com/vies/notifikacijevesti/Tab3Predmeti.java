package com.vies.notifikacijevesti;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
        listAdapter = new ExpandableListAdapter(this.getContext(), this.listDataHeader, listHash);
        listView.setAdapter(listAdapter);
        final TinyDB tinyDB = new TinyDB(this.getContext());
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SLANJE SAJKU
                listAdapter.onButtonPress();
//
//                for (int i = 0; i < selectedSubjects.size(); i++){
//                    Log.d("selected", "" + selectedSubjects.get(i));
//                }
            }
        });

        return rootView;
    }

    private void initData(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        //selectedSubjects = new ArrayList<>();

        listDataHeader.add("Основне Академске Студије");
        listDataHeader.add("Мастер Академске Студије");
        listDataHeader.add("Катедре");
        listDataHeader.add("Остало");

        List<String> oas = new ArrayList<>();
        oas.add("Биомеханика локомоторног система (0800)");
        oas.add("Биофизика (0823)");
        oas.add("Горива и индустријска вода (0888)");
        oas.add("Гориви технички гасови у процесима заваривања (0891)");
        oas.add("Динамика возила (0871)");
        oas.add("Електроника (1071)");
        oas.add("Електротехника (1070)");
        oas.add("Енглески језик 1 (0506)");
        oas.add("Енглески језик 2 (0489)");
        oas.add("Естетски дизајн (0988)");
        oas.add("Инжењерска графика (0572)");
        oas.add("Компјутерска графика (0663)");
        oas.add("Компјутерско моделирање и анимација (0591)");
        oas.add("Конструктивна геометрија и графика (0203)");
        oas.add("Конструкција и технологија производње летелица (0253)");
        oas.add("Математика 1 (0669)");
        oas.add("Математика 1 (0929)");
        oas.add("Математика 2 (0671)");
        oas.add("Математика 3 (0672)");
        oas.add("Машине алатке (0916)");
        oas.add("Машински елементи 1 (0045)");
        oas.add("Машински елементи 1 (1090)");
        oas.add("Машински елементи 2 (0046)");
        oas.add("Машински елементи 2 (1091)");
        oas.add("Машински материјали 1 (0883)");
        oas.add("Машински материјали 2 (0884)");
        oas.add("Машинско инжењерство у пракси (0879)");
        oas.add("Механика 1 (0001)");
        oas.add("Механика 2 (0002)");
        oas.add("Механика 3 (0799)");
        oas.add("Моделирање компонената машина (0962)");
        oas.add("Моделирање облика (0088)");
        oas.add("Нумеричке методе (0673)");
        oas.add("Објектно оријентисано програмирање и JAVA (0674)");
        oas.add("Обновљиви извори енергије - биомаса (0969)");
        oas.add("Основе биомедицинског инжењерства (0723)");
        oas.add("Основе парних котлова (0090)");
        oas.add("Основе соларних система (1065)");
        oas.add("Основе технике хлађења (0029)");
        oas.add("Основе техничких иновација (0961)");
        oas.add("Основе техничких иновација (1092)");
        oas.add("Основе турбомашина (0999)");
        oas.add("Основи конструисања система наоружања (0408)");
        oas.add("Основи отпорности конструкција (0021)");
        oas.add("Основи преношења топлоте (0532)");
        oas.add("Основи социологије и економије (0405)");
        oas.add("Отпорност материјала (0020)");
        oas.add("Погон и опрема летелица (1076)");
        oas.add("Погонски материјали (0054)");
        oas.add("Пословно-производни информациони системи (0412)");
        oas.add("Поступци заваривања Б (0890)");
        oas.add("Примењена термодинамика (0215)");
        oas.add("Програмирање (0670)");
        oas.add("Прорачун структуре летелица (0945)");
        oas.add("Рачунарски алати (0016)");
        oas.add("Рачунарски алати (0930)");
        oas.add("Репарација машинских делова и конструкција (0887)");
        oas.add("Сагоревање Б (0968)");
        oas.add("Стационарни проблеми простирања топлоте (0668)");
        oas.add("Стручна пракса Б - ВАЗ (0398)");
        oas.add("Стручна пракса Б - ДУМ (0482)");
        oas.add("Стручна пракса Б - ДУМ (1093)");
        oas.add("Стручна пракса Б - МОТ (0847)");
        oas.add("Стручна пракса Б - МФБ (0715)");
        oas.add("Стручна пракса Б - ПРО (0576)");
        oas.add("Стручна пракса Б - ХЕН (0925)");
        oas.add("Теорија осцилација (0012)");
        oas.add("Термодинамика Б (0372)");
        oas.add("Технологија машинске обраде (0065)");
        oas.add("Увод у вероватноћу и статистику (0543)");
        oas.add("Увод у енергетику (0406)");
        oas.add("Увод у индустријско инжењерство  (0209)");
        oas.add("Увод у системе наоружања (0210)");
        oas.add("Физика и мерења (0025)");

        List<String> mas = new ArrayList<>();
        mas.add("Аеродинамика великих брзина (0950)");
        mas.add("Аероеластичност (0645)");
        mas.add("Аналитичка механика (0825)");
        mas.add("Аутоматизација производње (0785)");
        mas.add("Биогорива у процесима сагоревања (0894)");
        mas.add("Биомеханика ткива и органа (0559)");
        mas.add("Ваздухопловни пропулзори (0951)");
        mas.add("Експерименти и симулације (0716)");
        mas.add("Електричне машине (0401)");
        mas.add("Електроника (1073)");
        mas.add("Индустријски роботи (0290)");
        mas.add("Иновативни дизајн техничких система (0964)");
        mas.add("Информационе технологије у медицини (0393)");
        mas.add("Информационе технологије у медицини (0938)");
        mas.add("Информационе технологије у медицини (1010)");
        mas.add("Компјутерска симулација у аутоматизацији производње (0722)");
        mas.add("Компоненте расхладних уређаја (0291)");
        mas.add("Машине алатке и роботи нове генерације (0919)");
        mas.add("Машине алатке М (0920)");
        mas.add("Машински материјали 3 (0892)");
        mas.add("Металургија заваривања (0901)");
        mas.add("Механика континуума (0826)");
        mas.add("Механика лома и интегритет конструкција (0896)");
        mas.add("Механика М (0004)");
        mas.add("Механика робота (0007)");
        mas.add("Мехатроника (0201)");
        mas.add("Микро обрада и карактеризација (0601)");
        mas.add("Моделирање и прорачун структура (1098)");
        mas.add("Обезбеђење и контрола квалитета заварених спојева (0720)");
        mas.add("Одржавање летелица (0952)");
        mas.add("Оштећења техничких система (0717)");
        mas.add("Погонски материјали 2 (0893)");
        mas.add("Понашање заварених спојева у експлоатацији (0719)");
        mas.add("Поступци заваривања М (0899)");
        mas.add("Поузданост преносника (0556)");
        mas.add("Преношење количине топлоте (0478)");
        mas.add("Преношење топлоте и супстанције (0350)");
        mas.add("Примењена аеродинамика (0946)");
        mas.add("Примењена теорија пластичности и погонска чврстоћа (1032)");
        mas.add("Програмабилни системи управљања (0904)");
        mas.add("Пројектовање возила  (0437)");
        mas.add("Пројектовање летелица (0136)");
        mas.add("Прорачунска аеродинамика (1078)");
        mas.add("Расхладна постројења (0192)");
        mas.add("Сагоревање М (0971)");
        mas.add("Софтверски алати у дизајну (0963)");
        mas.add("Структурална анализа (0947)");
        mas.add("Стручна пракса М - ПРО (0788)");
        mas.add("Теорија осцилација (0037)");
        mas.add("Теорија турбомашина (1000)");
        mas.add("Термодинамика М (0202)");
        mas.add("Техника мерења и сензори (0647)");
        mas.add("Техника мерења и сензори (0926)");
        mas.add("Топлотне пумпе (1050)");
        mas.add("Хибридни технички системи (0966)");

        List<String> kat = new ArrayList<>();
        kat.add("катедра за индустријско инжењерство");
        kat.add("катедра за математику");
        kat.add("катедра за механику");
        kat.add("катедра за моторна возила");
        kat.add("катедра за опште машинске конструкције");
        kat.add("катедра за термоенергетику");
        kat.add("катедра за термомеханику");
        kat.add("катедра за технологију материјала");
        kat.add("катедра за физику и електротехнику");
        kat.add("катедра за хидрауличне машине и енергетске системе");

        List<String> ostalo = new ArrayList<>();
        ostalo.add("Predmet 1");
        ostalo.add("Predmet 2");

        listHash.put(listDataHeader.get(0), oas);
        listHash.put(listDataHeader.get(1), mas);
        listHash.put(listDataHeader.get(2), kat);
        listHash.put(listDataHeader.get(3), ostalo);

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
