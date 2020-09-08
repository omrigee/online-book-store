public class InventoryTest {
//
//    private Inventory inv = Inventory.getInstance();
//
//
//    /** Test method for {bgu.spl.mics.application.passiveObjects.Inventory#getInstance()}:
//     */
//    @Test
//    public void getInstance() {
//        assertNotEquals(null,inv);
//    }
//
//
//    @Test
//    public void load() {
//        BookInventoryInfo[] bookArray = new BookInventoryInfo[1];
//        BookInventoryInfo book1  = new BookInventoryInfo("PeterPen",1,10);
//        inv.load(bookArray);
//        OrderResult taken = inv.take("PeterPen");
//        assertEquals("SUCCESSFULLY_TAKEN",taken);
//    }
//
//
//    /** Test method for {bgu.spl.mics.application.passiveObjects.Inventory#take(java.lang.String)}:
//     *
//     *
//     *
//     */
//    @Test
//    public void take() {
//        // book 1 is the available book, book 2 is the unavailable book.
//
//        BookInventoryInfo[] loadAvailable = new BookInventoryInfo[2];
//        BookInventoryInfo book1 = new BookInventoryInfo("available",1,10);
//        int availableAmount = book1.getAmountInInventory();
//        loadAvailable[0] = book1;
//        BookInventoryInfo book2 = new BookInventoryInfo("unavailable",0,10); // amount in inventory is 0 so unavailable.
//        int unavailableAmount = book2.getAmountInInventory();
//        loadAvailable[1] = book2;
//        inv.load(loadAvailable);
//
//        OrderResult TakenAvailableBook = inv.take("available");
//        assertEquals("SUCCESSFULLY_TAKEN",TakenAvailableBook); // Checks the enum of OrderResult of the available book
//        assertEquals(availableAmount-1,book1.getAmountInInventory()); //Checks the the amount of book taken is decreased by 1.
//
//        OrderResult UnavailableBookResult = inv.take("unavailable");
//        assertEquals("NOT_IN_STOCK",UnavailableBookResult); // checks the enum of OrderResult of unavailable book.
//        assertEquals(unavailableAmount,book2.getAmountInInventory()); // checks that the amount of the unavailable book did not decreased by 1.
//
//    }
//
//    @Test
//    public void checkAvailabiltyAndGetPrice() {
//        BookInventoryInfo[] loadAvailable = new BookInventoryInfo[2];
//        BookInventoryInfo book1 = new BookInventoryInfo("available",1,10);
//        int availablePrice = book1.getPrice();
//        loadAvailable[0] = book1;
//        int addedBookPrice = inv.checkAvailabiltyAndGetPrice("available");
//        assertEquals(availablePrice,addedBookPrice);
//
//
//    }
//
//    @Test
//    public void printInventoryToFile() {
//    }
//

}