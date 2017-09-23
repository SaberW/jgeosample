package org.potmart.jgeo.createshp;

import org.geotools.styling.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GOT.hodor on 2017/7/20.
 */
public class test {

    public static void main(String[] args) {
        Map<String, String> layer = new HashMap<>();
        layer.put("code", "m98989");

        Boolean b = verify(layer);
        if (b) {
            System.out.println("true");
        }else{
            System.out.println("false");
        }

        /*
        StyleBuilder styleBuilder = new StyleBuilder();
        ExternalGraphic externalGraphic = new ExternalGraphicImpl();
        externalGraphic.setURI("");
        Mark mark = new MarkImpl();
        mark.set
        styleBuilder.createGraphic(externalGraphic, )
        styleBuilder.buildClassifiedStyle();
        */

    }

    private static boolean verify (Map<String, String> layer) {
        if (layer == null) {
            return false;
        }

        //编码不能以数字开头
        String codeReg = "^[0-9].*$";
        Pattern codePattern = Pattern.compile(codeReg);
        Matcher codeMatcher = codePattern.matcher(layer.get("code"));
        if (codeMatcher.matches()) {
            return false;
        }

        //编码只能包含字母数字和下划线
        String codeReg2 = "^[a-z0-9_]+$";
        Pattern codePattern2 = Pattern.compile(codeReg2);
        Matcher codeMatcher2 = codePattern2.matcher(layer.get("code"));
        if (!codeMatcher2.matches()) {
            return false;
        }



        return true;
    }

}
