package net.soradigital.suaraku.helper;

public class ApiHelper {
    private String base_url = "https://suaraku.sieradigital.com/";
    private String url = "https://suaraku.sieradigital.com/?req=api&key=";
    private String key = "21232f297a57a5a743894a0e4a801fc3";
    public String newBaseUrl = "http://192.168.141.102:8000/";

    public String getUrl() {
        return url+this.key;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBase_url() {
        return base_url;
    }

    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }
}
