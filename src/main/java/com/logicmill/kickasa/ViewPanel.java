package com.logicmill.kickasa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 21/12/13
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
public class ViewPanel extends JPanel implements MouseListener, MouseMotionListener
{
  private Gui gui;
  private Image selectedImage;


/*
  public void setController( Controller controller )
  {
    this.controller = controller;
  }
*/



  public ViewPanel( Gui gui ) {
    this.gui = gui;
    setBorder(BorderFactory.createLineBorder( Color.black));

    setToolTipText( "View" );
    addMouseListener( this );
    addMouseMotionListener( this );
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(700,700);
  }
/*
  public synchronized void setImages( java.util.List<kickasa.Image> images )
  {
    this.images = images;
  }*/

  /**
   * Called from GUI thread
   */
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    java.util.List<com.logicmill.kickasa.Image> images = gui.getDisplayedImages();

    if( images != null )
    {
      int x = 10;
      int y = 20;
      for( com.logicmill.kickasa.Image image : images )
      {
        java.awt.Image thumbnail = image.getThumbnail();
        if( thumbnail != null )
        {
          g.drawImage( thumbnail, x, y, null );
        }
/*        else
        {
          if( image.thumbnailingNotStarted() )
          {
            System.out.println("No thumb for " + image + " (not started)" );
          }
          else
          {
            System.out.println("No thumb for " + image + " (started...)" );
          }
        }*/
/*        else
        {
          if( image.thumbnailingNotStarted() )
          {
            Future<java.awt.Image> f = controller.submitToExecutor( new Thumbnailer( gui, image ) );
            image.setThumbnail( f );
          }
        }*/
        if( selectedImage == image )
        {
          g.setColor( Color.BLUE );
        }
        else
        {
          g.setColor( Color.BLACK );
        }
        g.drawRect( x, y, 64, 64 );
//          g.drawString( image.toString(), x, y );

        x+=68;
        if( x > 680 )
        {
          x = 10;
          y += 68;
        }
      }
    }
  }


  @Override
  public void mouseClicked( MouseEvent e )
  {
    int x = e.getX();
    int y = e.getY();
    selectedImage = findImageAt( x, y );
    gui.setCurrentImage();
    repaint();
  }

  @Override
  public void mousePressed( MouseEvent e )
  {
    //TODO: This
  }

  @Override
  public void mouseReleased( MouseEvent e )
  {
    //TODO: This
  }

  @Override
  public void mouseEntered( MouseEvent e )
  {
    //TODO: This
  }

  @Override
  public void mouseExited( MouseEvent e )
  {
    //TODO: This
  }

  @Override
  public void mouseDragged( MouseEvent e )
  {
    //TODO: This
  }

  @Override
  public void mouseMoved( MouseEvent e )
  {
    int x = e.getX();
    int y = e.getY();
    Image hoveredImage = findImageAt( x, y );
    if( hoveredImage == null )
    {
      setToolTipText( "null" );
    }
    else
    {
      // TODO: Intercept tooltip popping up and decide then what it should say
      setToolTipText( hoveredImage.toString() );
    }

  }

  private Image findImageAt( int x, int y )
  {
    if( x >= 10 && y >= 20 )
    {
      int ox = x % 68;
      int oy = y % 68;
      if( ox < 64 && oy < 64 )
      {
        int ix = x / 68;
        int iy = y / 68;
        int i = iy * 10 + ix;
        if( i < gui.displayedImages.size() )
        {
          return gui.displayedImages.get( i );
        }
      }
    }
    return null;
  }


  /**
   * Called from fv thread
   */
  /*public synchronized boolean compareAndSetImages( java.util.List<Image> newImages )
  {
    boolean changed = false;
    if( images == null )
    {
      changed = true;
    }
    else
    {
      for( Image newImage: newImages )
      {
        if( !images.contains( newImage ) )
        {
          changed = true;
          break;
        }
      }
    }
    if( changed )
    {
      images = newImages;
    }
    return changed;
  }*/
}
