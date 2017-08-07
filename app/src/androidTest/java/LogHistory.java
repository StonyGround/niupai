import android.os.Parcel;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanqian on 27/06/2017.
 */
public class LogHistory {
    public void addEntry(String testString, long testLong) {
    }

    public String describeContents() {
        return "hello";
    }

    public void writeToParcel(Parcel parcel, String aVoid) {
    }

    public List<Pair<String,Long>> getData() {
        List<Pair<String,Long>> list = new ArrayList<>();
        list.add(new Pair<String, Long>("This is a string", 12345678L));
        return list;
    }

    public static class CREATOR {
        public static LogHistory createFromParcel(Parcel parcel) {
            return new LogHistory();
        }
    }
}
