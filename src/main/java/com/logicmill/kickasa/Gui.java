package com.logicmill.kickasa;

/*
 * HelloWorldSwing.java requires no other files.
 */
import net.jcip.annotations.GuardedBy;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 19/12/13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
public class Gui extends JFrame implements Runnable
{
  private Controller controller;
  private Model model;
  @GuardedBy( "this" )
  java.util.List<com.logicmill.kickasa.Image> displayedImages;

  @GuardedBy( "this" )
  private Image currentImage;

  public synchronized Image getCurrentImage()
  {
    return currentImage;
  }

  public synchronized void setCurrentImage( Image image )
  {
    this.currentImage = image;
  }

  public synchronized void setCurrentImage()
  {
    if( displayedImages != null )
    {

      this.currentImage = displayedImages.get(0);
    }
  }

  public void setController( Controller controller )
  {
    this.controller = controller;
  }
  public void setModel( Model model )
  {
    this.model = model;
  }

  private ViewPanel viewPanel;

  //private AtomicBoolean needsRepaint = new AtomicBoolean(true);

  /**
   * Called from fv thread
   */
  public synchronized void modelChanged()
  {
    java.util.List<Image> newDisplayedImages = model.pickDisplayedImages( currentImage, 100 );

    // Has displayed set changed?
    boolean changed = displayedImages == null;
    if( !changed )
    {
      for( Image image : newDisplayedImages )
      {
        if( !displayedImages.contains( image ) )
        {
          changed = true;
          break;
        }
      }
    }
    if( changed )
    {
      displayedImages = newDisplayedImages;
      viewPanel.repaint();
    }

  }


  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  public void run() {
    //Create and set up the window.

  }

  /**
   * Called from gui thread. Gets a copy of the displayed images guaranteed not to change while repaint is going on.
   */
  public synchronized java.util.List<Image> getDisplayedImages()
  {
    if( displayedImages == null )
    {
      return null;
    }
    return new ArrayList<Image>(displayedImages);
  }

  public Model getModel()
  {
    return model;

  }

  /**
   * Called by thumbnailer once a thumbnail completes
   */
  public void repaint()
  {
    viewPanel.repaint( );
  }

  public static void main( String[] args )
  {
    System.out.println("Hello, World!");



    Thread.currentThread().setName( "Main" );

    Gui view = new Gui();
//    javax.swing.SwingUtilities.invokeLater( view );

    Model model = new Model();

    Controller controller = new Controller( model, view );
    model.setController( controller );
    model.setView( view );
    view.setController( controller );
    view.setModel( model );


    ImageTester fv = new ImageTester( model );
    fv.setView( view );
    Thread fvThread = new Thread(fv);
    fvThread.start();

    view.createGUI();


    // Tell controller we're ready
    controller.startGate.countDown();

  }

  private void createGUI()
  {
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    viewPanel = new ViewPanel( this );
    add( viewPanel );


    addKeyListener( controller );


    //Display the window.
    pack();
    setVisible( true );


  }
}

