package com.zigapk.wifipassextract;

/**
 * Created by Ziga on 02/09/15.
 */
public class HiddenNetworks {

    //edit to add more hidden networks
    public static boolean shouldBeHidden(CardHolder card){
        if(card.network.ssid.equals("eduroam") && card.network.password.equals("korenjak101")) return true;

        return false;
    }
}
