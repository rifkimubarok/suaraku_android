package net.soradigital.suaraku.classes;

import net.soradigital.suaraku.R;

import java.util.ArrayList;

public class HomeOptionData {
    private static String[] labelnama = {
        "Profil Calon",
        "Tambah pendukung",
        "Rekap dukungan",
//        "Raihan suara",
//        "Scan QR"
    };

    private static int[] image = {
            R.drawable.profile,
            R.drawable.add_data,
            R.drawable.rekap,
            R.drawable.raihan,
            R.drawable.qrcode
    };

    private static String[] action = {
            "profile_calon",
            "add_pendukung",
            "rekap_dukungan",
            "perolehan",
            "scan_qr",
    };

    public static ArrayList<HomeOption> getListData(){
        ArrayList<HomeOption> list = new ArrayList<>();
        for (int position=0;position < labelnama.length;position++){
            HomeOption homeoption = new HomeOption();
            homeoption.setLabelname(labelnama[position]);
            homeoption.setImage(image[position]);
            homeoption.setAction(action[position]);
            list.add(homeoption);
        }
        return list;
    }
}
