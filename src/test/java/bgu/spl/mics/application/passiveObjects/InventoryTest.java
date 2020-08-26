package java.bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    private bgu.spl.mics.application.passiveObjects.Inventory i1;
    private bgu.spl.mics.application.passiveObjects.BookInventoryInfo[] booksTest = new BookInventoryInfo[]{new BookInventoryInfo("Tzofen De Vinchi", 1, 99)};

    @Before
    public void setUp() throws Exception {
        i1 = bgu.spl.mics.application.passiveObjects.Inventory.getInstance();
        i1.load(booksTest);
    }

    @After
    public void tearDown() throws Exception {
        i1 = null;
        booksTest=null;
    }

    @Test
    public void getInstance() {
        bgu.spl.mics.application.passiveObjects.Inventory i2 = bgu.spl.mics.application.passiveObjects.Inventory.getInstance();
        assertEquals(i1, i2);
    }

    // @pre - books HashMap is empty.
    // @post - inventory now contains all the BookInventoryInfo
    @Test
    public void load() {
        assertEquals(i1.getBooks().size(), 0);
        assertEquals(i1.getBooks().size(), 1);
        assertEquals(i1.getBooks().get("Tzofen De Vinchi").get().getAmountInInventory().get(), 1);
    }

    // @pre - books hashmap is loaded.
    // @post - the availability number of the book we took is smaller by 1 if it was available,
    // or is 0 if it wasn't.
    @Test
    public void take() {
        OrderResult i1Result = i1.take("Tzofen De Vinchi");
        try {
            assertEquals(i1.getBooks().get("Tzofen De Vinchi").get().getAmountInInventory().get(), 0);
        } catch (Exception e) {
            System.out.println("Availability Amount after take isn't 0 as expected");
        }
        try {
            assertEquals(i1Result, OrderResult.SUCCESSFULLY_TAKEN);
        } catch (Exception e) {
            System.out.println("take method didn't work peroperly");
        }
        try {
            OrderResult i1Result2 = i1.take("Tzofen De Vinchi");
            assertEquals(i1Result2, OrderResult.NOT_IN_STOCK);
            assertEquals(i1.getBooks().get("Tzofen De Vinchi").get().getAmountInInventory().get(), 0);
        } catch (Exception e) {
            System.out.println("unexpected exception");
        }
    }

    // @pre - Inventory is loaded
    // @post - Inventory remains the same.
    @Test
    public void checkAvailabiltyAndGetPrice() {
        try {
            assertEquals(i1.checkAvailabiltyAndGetPrice("Tzofen De Vinchi"), 99);
        } catch (Exception e) {
            System.out.println("returned price wasn't as expected");
        }
        i1.take("Tzofen De Vinchi");
        try {
            assertEquals(i1.checkAvailabiltyAndGetPrice("Tzofen De Vinchi"), -1);
        } catch (Exception e) {
            System.out.println("returned value wasn't as expected");
        }
    }
}