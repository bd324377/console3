package com.console.framework.domain;
import com.console.framework.utils.RequestUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class LanguagePack implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String zhCN;            //中文
    private String enUS;            //英文
    private String ptBR;            //葡萄牙语

    public static String getValueByLang(LanguagePack languagePack) {
        String lang = RequestUtils.getLang();
        if (languagePack == null) {
            languagePack = new LanguagePack();
        }
        return switch (lang) {
            case "en" -> languagePack.getEnUS();
            case "pt" -> languagePack.getPtBR();
            default -> languagePack.getZhCN();
        };
    }

    public String langValue() {
        String lang = RequestUtils.getLang();
        return switch (lang) {
            case "en" -> this.getEnUS();
            case "pt" -> this.getPtBR();
            default -> this.getZhCN();
        };
    }

    public String langValue(String lang) {
        return switch (lang) {
            case "en" -> this.getEnUS();
            case "pt", "sp" -> this.getPtBR();
            default -> this.getZhCN();
        };
    }
}
