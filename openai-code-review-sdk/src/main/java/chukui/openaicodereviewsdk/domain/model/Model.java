package chukui.openaicodereviewsdk.domain.model;

public enum Model {

    DEEPSEEK_V4_FLASH("deepseek-v4-flash", "DeepSeek V4 Flash 模型"),
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
