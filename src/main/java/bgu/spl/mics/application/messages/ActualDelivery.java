package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ActualDelivery implements Event<DeliveryVehicle>{
    private Future<DeliveryVehicle> future;
    private Customer customer;
    private DeliveryVehicle vehicle;

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }

    public Future<DeliveryVehicle> getFuture() {
        return future;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ActualDelivery(Customer c, DeliveryVehicle v){
        customer = c;
        vehicle = v;
    }

    public void setFuture(Future<DeliveryVehicle> future) {
        this.future = future;
    }
}
