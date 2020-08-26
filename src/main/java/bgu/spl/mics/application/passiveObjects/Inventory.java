package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.BookStoreRunner;
import com.google.gson.annotations.SerializedName;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.HashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private ConcurrentHashMap<String, AtomicReference<BookInventoryInfo>> books = new ConcurrentHashMap<>();

	@SerializedName("booksRemainToPrint")
	private HashMap<String,Integer> booksRemainToPrint;

	/**
	 * Retrieves the single instance of this class.
	 */
	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}

	private Inventory() {
		booksRemainToPrint=new HashMap<>();
	}

	public static Inventory getInstance() {
		return Inventory.InventoryHolder.instance;
	}


	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (BookInventoryInfo[] inventory ) {
		for(int i=0; i<inventory.length; i++){
			books.put(inventory[i].getBookTitle(), new AtomicReference<>(inventory[i]));
		}
	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	public OrderResult take (String book) {
		if(books.containsKey(book) && books.get(book).get().getAmountInInventory().get()>0) {
			int value;
			do {
				value = books.get(book).get().getAmountInInventory().get();
				if (value==0)
					return OrderResult.NOT_IN_STOCK;
			}
			while (!books.get(book).get().getAmountInInventory().compareAndSet(value,value-1));
			return OrderResult.SUCCESSFULLY_TAKEN;
		}
		else
			return OrderResult.NOT_IN_STOCK;
	}

	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabiltyAndGetPrice(String book) {
		if(books.containsKey(book) && books.get(book).get().getAmountInInventory().get()>0) {
			return books.get(book).get().getPrice();
		}
		else
			return -1;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printInventoryToFile(String filename){
		for(AtomicReference<BookInventoryInfo> ref : books.values()){
			booksRemainToPrint.put(ref.get().getBookTitle(), ref.get().getAmountInInventory().get());
		}
		BookStoreRunner.print(filename,booksRemainToPrint);
	}

	public ConcurrentHashMap<String, AtomicReference
			<bgu.spl.mics.application.passiveObjects.BookInventoryInfo>> getBooks(){
		return books;
	}
}