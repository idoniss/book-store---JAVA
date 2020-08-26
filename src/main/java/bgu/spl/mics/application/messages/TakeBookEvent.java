package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.Future;

public class TakeBookEvent implements Event {
    private String bookTitle;

    public TakeBookEvent(String name){
        bookTitle = name;
    }
    public void setFuture(Future<OrderResult> future) {
        this.future = future;
    }

    public Future<OrderResult> getFuture() {
        return future;
    }

    private Future<OrderResult> future;

    public String getBookTitle() {
        return bookTitle;
    }
}
