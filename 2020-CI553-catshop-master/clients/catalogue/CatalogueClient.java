package clients.catalogue;

import catalogue.*;
import middle.MiddleFactory;
import middle.Names;
import middle.RemoteMiddleFactory;

import javax.swing.*;



public class CatalogueClient
{
   public static void main (String args[])
   {
     String stockURL = args.length < 1     // URL of stock RW
                     ? Names.STOCK_RW      //  default  location
                     : args[0];            //  supplied location
     String orderURL = args.length < 2     // URL of order
                     ? Names.ORDER         //  default  location
                     : args[1];            //  supplied location
     
    RemoteMiddleFactory mrf = new RemoteMiddleFactory();
    mrf.setStockRWInfo( stockURL );
    mrf.setOrderInfo  ( orderURL );        //
    displayGUI(mrf);                       // Create GUI
  }


  private static void displayGUI(MiddleFactory mf)
  {     
    JFrame  window = new JFrame();
     
    window.setTitle( "Catalogue Client (MVC RMI)");
    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    
    CatalogueModel      model = new CatalogueModel(mf);
    CatalogueView       view  = new CatalogueView( window, mf, 0, 0 );
    CatalogueController cont  = new CatalogueController( model, view );
    view.setController( cont );

    model.addObserver( view );       // Add observer to the model
    window.setVisible(true);         // Display Screen
    model.askForUpdate();
  }
}
