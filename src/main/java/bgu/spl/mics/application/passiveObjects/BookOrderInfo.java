package bgu.spl.mics.application.passiveObjects;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BookOrderInfo implements Serializable {

    @SerializedName("bookTitle")
    private String bookTitle;
    @SerializedName("tick")
    private int tick;

    public BookOrderInfo(String name, int time) {
        bookTitle = name;
        tick = time;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getTick() {
        return tick;
    }
}