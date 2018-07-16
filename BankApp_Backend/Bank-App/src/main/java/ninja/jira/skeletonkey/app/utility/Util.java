package ninja.jira.skeletonkey.app.utility;

import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import javax.servlet.http.Part;
import java.util.Date;
import java.text.SimpleDateFormat;

public final class Util {

    public static Date convertToDate (String date) throws Exception{
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    public static Date formatNewDate () throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = formatter.format(new Date());
        return formatter.parse(sDate);
    }

    public static String formatNewStringDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date());
    }

    public static Double roundTo2Decimal (Double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    public static byte[] convertImage (Part part) throws Exception{
        InputStream input = part.getInputStream();
        byte[] image = IOUtils.toByteArray(input);
        return image;

    }
}