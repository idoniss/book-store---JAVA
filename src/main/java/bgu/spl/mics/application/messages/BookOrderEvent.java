package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.BookOrderInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt>{

    private String bookTitle;
    private Customer customer;
    private Future<OrderReceipt> future;
    private long tick;
    private int speed;
    private long start;


    public long getStart() {
        return start;
    }


    public BookOrderEvent(BookOrderInfo orderInfo, Customer orderer, long tick, int speed, long s){
        bookTitle=orderInfo.getBookTitle();
        customer=orderer;
        this.tick = tick;
        this.speed = speed;
        start = s;
    }


    public void setFuture(Future<OrderReceipt> future) {
        this.future = future;
    }

    public Future<OrderReceipt> getFuture(){
        return future;
    }

    public String getBookTitle(){
        return bookTitle;
    }

    public Customer getCustomer(){
        return customer;
    }

    public long getTick(){
        return tick;
    }

    public int getSpeed(){
        return speed;
    }
}