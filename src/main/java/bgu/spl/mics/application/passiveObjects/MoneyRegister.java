package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import bgu.spl.mics.application.BookStoreRunner;
import com.google.gson.annotations.SerializedName;


/**
 * Passive object representing the store finance management.
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {

	@SerializedName("receipts")
	List<OrderReceipt> receipts;

	private static class MoneyRegisterHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}

	private MoneyRegister() {
		receipts=new LinkedList<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return MoneyRegister.MoneyRegisterHolder.instance;
	}

	/**
	 * Saves an order receipt in the money register.
	 * <p>
	 * @param r		The receipt to save in the money register.
	 */
	public void file (OrderReceipt r) {
		receipts.add(r);
	}

	/**
	 * Retrieves the current total earnings of the store.
	 */
	public int getTotalEarnings() {
		int sum=0;
		for(OrderReceipt o: receipts)
			sum+=o.getPrice();
		return sum;
	}

	/**
	 * Charges the credit card of the customer a certain amount of money.
	 * <p>
	 * @param amount 	amount to charge
	 */
	public void chargeCreditCard(Customer c, int amount) {
		c.chargeCreditByAmount(amount);
	}

	/**
	 * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
	 * currently in the MoneyRegister
	 * This method is called by the main method in order to generate the output..
	 */
	public void printOrderReceipts(String filename) {
		BookStoreRunner.print(filename,receipts);
	}

	public void printRecipts(){
		for(OrderReceipt or: receipts){
			System.out.println(or.getBookTitle() + " "  + or.getPrice());
			System.out.println("issuedTick: " + or.getIssuedTick() + " processTick: " + or.getProcessTick()
			+ " and orderTick: " + or.getOrderTick());
		}
	}
}