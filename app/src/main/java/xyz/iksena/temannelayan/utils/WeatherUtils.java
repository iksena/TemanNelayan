package xyz.iksena.temannelayan.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import xyz.iksena.temannelayan.R;

public class WeatherUtils {

    public static String getDateFromSeconds(long seconds, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(seconds*1000));
    }

    public static String getPressureRate(double pressure){
        if (pressure >= 1006.0 && pressure <= 1047.0){
            if (pressure >= 1012.0 && pressure <= 1031.0)
                return "Sangat Baik";
            return "Baik";
        } else
            return "Buruk";
    }

    public static int getPressureRateColorRes(double pressure){
        if (pressure >= 1006.0 && pressure <= 1047.0){
            if (pressure >= 1012.0 && pressure <= 1031.0)
                return R.color.green_900;
            return R.color.yellow_900;
        } else
            return R.color.red_900;
    }

    public static String getFormattedWind(Context context, double windSpeed, double degrees) {
        String direction = "Tidak Diketahui";
         if (degrees >= 22.5 && degrees < 67.5) {
            direction = "Timur Laut";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "Timur";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "Tenggara";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "Selatan";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "Barat Daya";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "Barat";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "Barat Laut";
        } else if (degrees >= 337.5 || degrees < 22.5) {
             direction = "Utara";
         }

        return context.getString(R.string.format_wind_kmh, windSpeed, direction);
    }

    public static String getStringForWeatherCondition(Context context, int weatherId) {
        int stringId;
        if (weatherId >= 200 && weatherId <= 232) {
            stringId = R.string.condition_2xx;
        } else if (weatherId >= 300 && weatherId <= 321) {
            stringId = R.string.condition_3xx;
        } else switch (weatherId) {
            case 500:
                stringId = R.string.condition_500;
                break;
            case 501:
                stringId = R.string.condition_501;
                break;
            case 502:
                stringId = R.string.condition_502;
                break;
            case 503:
                stringId = R.string.condition_503;
                break;
            case 504:
                stringId = R.string.condition_504;
                break;
            case 511:
                stringId = R.string.condition_511;
                break;
            case 520:
                stringId = R.string.condition_520;
                break;
            case 531:
                stringId = R.string.condition_531;
                break;
            case 600:
                stringId = R.string.condition_600;
                break;
            case 601:
                stringId = R.string.condition_601;
                break;
            case 602:
                stringId = R.string.condition_602;
                break;
            case 611:
                stringId = R.string.condition_611;
                break;
            case 612:
                stringId = R.string.condition_612;
                break;
            case 615:
                stringId = R.string.condition_615;
                break;
            case 616:
                stringId = R.string.condition_616;
                break;
            case 620:
                stringId = R.string.condition_620;
                break;
            case 621:
                stringId = R.string.condition_621;
                break;
            case 622:
                stringId = R.string.condition_622;
                break;
            case 701:
                stringId = R.string.condition_701;
                break;
            case 711:
                stringId = R.string.condition_711;
                break;
            case 721:
                stringId = R.string.condition_721;
                break;
            case 731:
                stringId = R.string.condition_731;
                break;
            case 741:
                stringId = R.string.condition_741;
                break;
            case 751:
                stringId = R.string.condition_751;
                break;
            case 761:
                stringId = R.string.condition_761;
                break;
            case 762:
                stringId = R.string.condition_762;
                break;
            case 771:
                stringId = R.string.condition_771;
                break;
            case 781:
                stringId = R.string.condition_781;
                break;
            case 800:
                stringId = R.string.condition_800;
                break;
            case 801:
                stringId = R.string.condition_801;
                break;
            case 802:
                stringId = R.string.condition_802;
                break;
            case 803:
                stringId = R.string.condition_803;
                break;
            case 804:
                stringId = R.string.condition_804;
                break;
            case 900:
                stringId = R.string.condition_900;
                break;
            case 901:
                stringId = R.string.condition_901;
                break;
            case 902:
                stringId = R.string.condition_902;
                break;
            case 903:
                stringId = R.string.condition_903;
                break;
            case 904:
                stringId = R.string.condition_904;
                break;
            case 905:
                stringId = R.string.condition_905;
                break;
            case 906:
                stringId = R.string.condition_906;
                break;
            case 951:
                stringId = R.string.condition_951;
                break;
            case 952:
                stringId = R.string.condition_952;
                break;
            case 953:
                stringId = R.string.condition_953;
                break;
            case 954:
                stringId = R.string.condition_954;
                break;
            case 955:
                stringId = R.string.condition_955;
                break;
            case 956:
                stringId = R.string.condition_956;
                break;
            case 957:
                stringId = R.string.condition_957;
                break;
            case 958:
                stringId = R.string.condition_958;
                break;
            case 959:
                stringId = R.string.condition_959;
                break;
            case 960:
                stringId = R.string.condition_960;
                break;
            case 961:
                stringId = R.string.condition_961;
                break;
            case 962:
                stringId = R.string.condition_962;
                break;
            default:
                return context.getString(R.string.condition_unknown, weatherId);
        }

        return context.getString(stringId);
    }
}
