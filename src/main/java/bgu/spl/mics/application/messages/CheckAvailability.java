package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class CheckAvailability implements Event<Integer>{

    private String bookTitle;
    private Future<Integer> future;

    public CheckAvailability(String name){
        bookTitle = name;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setFuture(Future<Integer> future) {
        this.future = future;
    }

    public Future<Integer> getFuture(){
        return future;
    }
}
