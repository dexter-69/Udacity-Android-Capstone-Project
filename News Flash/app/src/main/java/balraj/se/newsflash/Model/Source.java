package balraj.se.newsflash.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by balra on 19-03-2018.
 */

public class Source implements Parcelable {
    public static final Creator<Source> CREATOR = new Creator<Source>() {
        @Override
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        @Override
        public Source[] newArray(int size) {
            return new Source[size];
        }
    };
    private String name;
    private String id;

    protected Source(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static Creator<Source> getCREATOR() {
        return CREATOR;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
