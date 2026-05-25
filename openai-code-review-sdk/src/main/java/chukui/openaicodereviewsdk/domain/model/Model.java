package chukui.openaicodereviewsdk.domain.model;

public enum Model {

    DEEPSEEK_CHAT("deepseek-chat", "DeepSeek V2.5 最新稳定版，综合能力强"),
    ;

    private final String code;
    private final String info;

    Model(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

}
