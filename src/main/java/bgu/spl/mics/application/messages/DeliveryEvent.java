package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.Future;


public class DeliveryEvent implements Event<Boolean> {
    private Customer customer;
    private String bookTitle;
    private Future<Boolean> future;

    public DeliveryEvent(Customer c, String name){
        customer = c;
        bookTitle = name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookTitle(){
        return bookTitle;
    }

    public void setFuture(Future f){
        future = f;
    }

    public Future<Boolean> getFuture(){
        return future;
    }
}
