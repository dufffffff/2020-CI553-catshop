package clients.catalogue;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.*;

import java.util.Observable;

/**
 * Implements the Model of the cashier client
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */
public class CatalogueModel extends Observable
{


 
  private Product     theProduct = null;            // Current product
  private Basket      theBasket  = null;            // Bought items

  private String      pn = "";                      // Product being processed

  private StockReadWriter theStock     = null;
  private OrderProcessing theOrder     = null;

  /**
   * Construct the model of the Cashier
   * @param mf The factory to create the connection objects
   */

  public CatalogueModel(MiddleFactory mf)
  {
    try                                           // 
    {      
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch ( Exception e )
    {
      DEBUG.error("CatalogueModel.constructor\n%s", e.getMessage() );
    }

  }
  
  /**
   * Get the Basket of products
   * @return basket
   */
  public Basket getBasket()
  {
    return theBasket;
  }

  /**
   * Check if the product is in Stock
   * @param productNum The product number
   */
  public void doCheck(String productName )
  {
	
    String theOutput = "";
 
    pn  = productName.trim();                    // Product name
    int    amount  = 1;                         //  & quantity
    try
    {
      if ( theStock.existsN( pn ) )              // Stock Exists?
      {                                         // T
        Product pr = theStock.getDetailsN(pn);  //  Get details
        
                                               //  T
          theOutput =                           //   Display 
            String.format( "Found Product: %s", //
              pr.getDescription() );              //    quantity     
          theProduct = pr;                      //   Remember prod.
          theProduct.setQuantity( amount );     //    & quantity
        
        
        
      } else {                                  // F Stock exists
        theOutput =                             //  Unknown
          "Unknown product name: " + pn;       //  product no.
      }
    } catch( StockException e )
    {
      DEBUG.error( "%s\n%s", 
            "CashierModel.doCheck", e.getMessage() );
      theOutput = e.getMessage();
    }
    setChanged(); notifyObservers(theOutput);
  }

  /**
   * Buy the product
   */
  public void doDisplay()
  {
    String theAction = "";
                             //  & quantity
    try
    {

        boolean stockFound =                   // Buy
          theStock.buyStock(  //  however
            theProduct.getProductNum(),         //  may fail              
            theProduct.getQuantity() );         //
        if ( stockFound )                      // Stock bought
        {                                       // T
          makeBasketIfReq();                    //  new Basket ?
          theBasket.add( theProduct );          //  Add to bought
          theAction = "Found " +            //    details
                  theProduct.getDescription();  //
        } else {                                // F
          theAction = "!!! Not in stock";       //  Now no stock
        }
      
    } catch( StockException e )
    {
      DEBUG.error( "%s\n%s", 
            "CashierModel.doBuy", e.getMessage() );
      theAction = e.getMessage();
    }

    setChanged(); notifyObservers(theAction);
  }
  
  /**
   
   */
  public void doClear()
  {
    String theAction = "";
  
    try
    {
      if ( theBasket != null &&
           theBasket.size() >= 1 )            // items > 1
      {                                       // T
        theOrder.newOrder( theBasket );       //  Process order
        theBasket = null;                     //  reset
      }                                       //
      theAction = "Next customer";            // New Customer

      theBasket = null;
    } catch( OrderException e )
    {
      DEBUG.error( "%s\n%s", 
            "CashierModel.doCancel", e.getMessage() );
      theAction = e.getMessage();
    }
    theBasket = null;
    setChanged(); notifyObservers(theAction); // Notify
  }

  /**
   * ask for update of view callled at start of day
   * or after system reset
   */
  public void askForUpdate()
  {
    setChanged(); notifyObservers("Welcome");
  }
  
  /**
   * make a Basket when required
   */
  private void makeBasketIfReq()
  {
    if ( theBasket == null )
    {
      try
      {
        int uon   = theOrder.uniqueNumber();     // Unique order num.
        theBasket = makeBasket();                //  basket list
        theBasket.setOrderNum( uon );            // Add an order number
      } catch ( OrderException e )
      {
        DEBUG.error( "Comms failure\n" +
                     "CashierModel.makeBasket()\n%s", e.getMessage() );
      }
    }
  }

  /**
   * return an instance of a new Basket
   * @return an instance of a new Basket
   */
  protected Basket makeBasket()
  {
    return new Basket();
  }
}
  
