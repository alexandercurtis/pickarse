package com.logicmill.kickasa;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 19/12/13
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public class Model
{

  private Controller controller;

  private Gui view;


  private ConcurrentSkipListSet<Image> allImages = new ConcurrentSkipListSet<Image>();



  public void setController( Controller controller )
  {
    this.controller = controller;
  }

  public void setView( Gui view )
  {
    this.view = view;
  }

  /**
   * Called from fv thread
   */
  public synchronized void addImage( Image image )
  {
//    System.out.println( "ADD: " + image.toString() );

    allImages.add( image );

    view.modelChanged();
  }

  /**
   * Runs in fv thread
   * @param currentImage may be null
   */
  public LinkedList<Image> pickDisplayedImages( Image currentImage, int nImages )
  {
    LinkedList<Image> result = new LinkedList<Image>();

    Iterator<Image> i;
    if( currentImage != null )
    {

      // TODO: Is this thread safe? What if stuff gets added during iteration?
      NavigableSet<Image> tail = allImages.tailSet( currentImage );
      i = tail.iterator();
    }
    else
    {
      i = allImages.iterator();
    }

    while( i.hasNext() && nImages-- > 0 )
    {
      Image image = i.next();

      if( image.thumbnailingNotStarted() )
      {
        System.out.println( "new Thumbnailer( " + image + " )" );
        Future<java.awt.Image> f = controller.submitToExecutor( new Thumbnailer( view, image ) );
        image.setThumbnail( f );
      }

      result.add(image);
    }

    return result;
  }

  public Image getFirstImage()
  {
    try
    {
      return allImages.first();
    }
    catch( NoSuchElementException e )
    {
      return null;
    }
  }
  public Image getLastImage()
  {
    try
    {
      return allImages.last();
    }
    catch( NoSuchElementException e )
    {
      return null;
    }
  }

  /**
   * Called from GUI thread
   */
  public Image selectPrevious( Image currentImage )
  {
    Image previousImage = allImages.lower( currentImage );
    return previousImage!=null?previousImage:currentImage;
  }

  /**
   * Called from GUI thread
   */
  public Image selectNext( Image currentImage )
  {
    Image nextImage = allImages.higher( currentImage );
    return nextImage!=null?nextImage:currentImage;
  }




  /**
   * Gets a copy of the displayed images list
   */
/*
  public synchronized java.util.List<Image> getDisplayedImages()
  {
    return new ArrayList<Image>( displayedImages );
  }
*/

}
