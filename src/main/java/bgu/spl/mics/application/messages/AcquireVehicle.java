package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Customer;


public class AcquireVehicle implements Event<Boolean> {

    private Customer customer;

    private Future<Boolean> future;

    public AcquireVehicle(Customer c){
        customer = c;
    }

    public Customer getCustomer(){
        return customer;
    }

    public Future<Boolean> getFuture() {
        return future;
    }

    public void setFuture(Future<Boolean> future) {
        this.future = future;
    }
}
