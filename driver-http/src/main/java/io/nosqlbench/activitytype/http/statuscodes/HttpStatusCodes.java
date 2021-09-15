package io.nosqlbench.activitytype.http.statuscodes;

import io.nosqlbench.nb.api.content.Content;
import io.nosqlbench.nb.api.content.NBIO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStreamReader;

public class HttpStatusCodes {

    private static final IetfStatusCode[] codes = loadMap();

    private static IetfStatusCode[] loadMap() {
        Content<?> csv = NBIO.local().name("ietf-http-status-codes").extension("csv").one();
        InputStreamReader isr = new InputStreamReader(csv.getInputStream());
        IetfStatusCode[] codes = new IetfStatusCode[600];

        try {
            CSVParser parser = new CSVParser(isr,CSVFormat.DEFAULT.withFirstRecordAsHeader());
            for (CSVRecord record : parser) {
                String values = record.get("Value");
                String description = record.get("Description");
                String reference = record.get("Reference");

                int min, max=0;
                if (values.contains("-")) {
                    min=Integer.parseInt(values.substring(0,values.indexOf('-')));
                    max=Integer.parseInt(values.substring(values.indexOf('-')));
                } else {
                    min = max = Integer.parseInt(values);
                }
                HttpStatusRanges category = HttpStatusRanges.valueOfCode(min);
                IetfStatusCode code = new IetfStatusCode(values,description,reference,category);
                for (int value = min; value <=max ; value++) {
                    codes[value]=code;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return codes;
    }

    public static IetfStatusCode lookup(int code) {
        if (code<1||code>codes.length-1) {
            return UNKNOWN(code);
        }
        IetfStatusCode found = codes[code];
        if (found!=null) {
            return found;
        } else {
            return UNKNOWN(code);
        }
    }

    private static IetfStatusCode UNKNOWN(int code) {
        return new IetfStatusCode(String.valueOf(code),null, "[check https://www.iana.org/assignments/http-status-codes/http-status-codes.xhtml]", HttpStatusRanges.valueOfCode(code));
    }
}