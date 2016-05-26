package com.logicmill.kickasa;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 19/12/13
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public class Controller implements KeyListener
{
  BlockingQueue<Runnable> queue = new BlockingLifoQueue<Runnable>();

  private ExecutorService es = new ThreadPoolExecutor( 4, 8, 1, TimeUnit.SECONDS, queue  );
  private Gui view;
  private Model model;
  final CountDownLatch startGate = new CountDownLatch( 1 );
  private List<com.logicmill.kickasa.Image> displayedImages;



  private AtomicBoolean modelChanged = new AtomicBoolean(true);

  /**
   * Called from fv thread
   */
  public void modelChanged()
  {
    synchronized( modelChanged )
    {
      modelChanged.set( true );
      modelChanged.notifyAll();
    }
  }


  /**
   * Called from main thread
   */
  public Controller( Model model, Gui view )
  {
    this.model = model;
    this.view = view;
  }

  private void dumpImages( List<Image> displayedImages )
  {
    int i = 1;
    for( Image image: displayedImages )
    {
      System.out.println( (i++) + ": " + image.toString() );
    }
  }

/*

  @Override
  public void run()
  {
    // Watch model for changes
    try
    {
      for(;;)
      {
        synchronized( modelChanged )
        {
          while( !modelChanged.getAndSet( false ) )
          {
            modelChanged.wait();
          }
        }
        List<Image> images = model.pickDisplayedImages( getCurrentImage(), 100 );
        boolean imagesChanged = viewPanel.compareAndSetImages( images );
        if( imagesChanged )
        {
          viewPanel.repaint();
        }
      }
    }
    catch( InterruptedException e )
    {
      // Simply let thread exit
    }
  }
*/

  /**
   * Called from GUI thread
   */
  @Override
  public void keyTyped( KeyEvent e )
  {


  }

  @Override
  public void keyPressed( KeyEvent e )
  {
    if( e.getKeyCode() == KeyEvent.VK_UP )
    {
      Image newImage;
      Image currentImage = view.getCurrentImage();
      if( currentImage == null )
      {
        view.setCurrentImage( model.getFirstImage() );
      }
      else
      {
        for( int i = 0; i < 10; ++i )
        {
          newImage = model.selectPrevious( currentImage );
          if( newImage == currentImage )
          {
            break;
          }
          currentImage = newImage;
        }
        view.setCurrentImage( currentImage );
      }
      view.modelChanged();
    }
    else if( e.getKeyCode() == KeyEvent.VK_DOWN )
    {
      Image newImage;
      Image currentImage = view.getCurrentImage();
      if( currentImage == null )
      {
        currentImage = model.getFirstImage();
      }

      for( int i = 0; i < 10; ++i )
      {
        newImage = model.selectNext( currentImage );
        if( newImage == currentImage )
        {
          break;
        }
        currentImage = newImage;
      }
      view.setCurrentImage( currentImage );
      view.modelChanged();
    }
    if( e.getKeyCode() == KeyEvent.VK_PAGE_UP )
    {
      Image newImage;
      Image currentImage = view.getCurrentImage();
      if( currentImage == null )
      {
        view.setCurrentImage( model.getFirstImage() );
      }
      else
      {
        for( int i = 0; i < 90; ++i )
        {
          newImage = model.selectPrevious( currentImage );
          if( newImage == currentImage )
          {
            break;
          }
          currentImage = newImage;
        }
        view.setCurrentImage( currentImage );
      }
      view.modelChanged();
    }
    else if( e.getKeyCode() == KeyEvent.VK_PAGE_DOWN )
    {
      Image newImage;
      Image currentImage = view.getCurrentImage();
      if( currentImage == null )
      {
        currentImage = model.getFirstImage();
      }

      for( int i = 0; i < 90; ++i )
      {
        newImage = model.selectNext( currentImage );
        if( newImage == currentImage )
        {
          break;
        }
        currentImage = newImage;
      }
      view.setCurrentImage( currentImage );
      view.modelChanged();
    }
    else if( e.isControlDown() && e.getKeyCode() == KeyEvent.VK_HOME )
    {
      view.setCurrentImage( model.getFirstImage() );
      view.modelChanged();
    }
    else if( e.isControlDown() && e.getKeyCode() == KeyEvent.VK_END )
    {
      Image newImage;
      Image currentImage = model.getLastImage();
      for( int i = 0; i < 99; ++i )
      {
        newImage = model.selectPrevious( currentImage );
        if( newImage == currentImage )
        {
          break;
        }
        currentImage = newImage;
      }
      view.setCurrentImage( currentImage );
      view.modelChanged();
    }
  }

  @Override
  public void keyReleased( KeyEvent e )
  {
    //TODO: This
  }


  public Future<java.awt.Image> submitToExecutor( Callable<java.awt.Image> task )
  {
    return es.submit( task );
  }

}
