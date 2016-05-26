package com.logicmill.kickasa;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 19/12/13
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public class Thumbnailer implements Callable<java.awt.Image>
{
  private Gui view;
  private com.logicmill.kickasa.Image image;

  public Thumbnailer( Gui view, com.logicmill.kickasa.Image image )
  {
    this.view = view;
    this.image = image;
  }

  /**
   * @return null if IO Exception reading image
   */
  @Override
  public Image call() throws Exception
  {
    System.out.println( "Thumbnailing " + image.toString() );

    BufferedImage img = null;
    try
    {
      System.out.println( "Loading " + image.toString() );
      img = ImageIO.read( image.getPath().toFile() );
      System.out.println( "Loaded " + image.toString() );
    }
    catch( IOException e )
    {
      // Can't read from filesystem
      System.err.println( "IO Exception " + e );
      return null;
    }

    if( img == null )
    {
      // Can't parse image data
      return null;
    }

    System.out.println( "Resizing 1 " + image.toString() );
    System.out.println( "Resizing 1a = " + img );

    // Resize image
    int ow = img.getWidth();
    int oh = img.getHeight();

    System.out.println( "w=" + ow + " h = " + oh );

    int nw, nh, x, y;
    if( ow <=64 && oh <= 64 )
    {
      nw = ow;
      nh = oh;
      x = ( 64 - nw ) / 2;
      y = ( 64 - nh ) / 2;
    }
    else if( ow > oh )
    {
      nw = 64;
      nh = nw * oh / ow;
      x = 0;
      y = ( 64 - nh ) / 2;
    }
    else
    {
      nh = 64;
      nw = nh * ow / oh;
      x = ( 64 - nw ) / 2;
      y = 0;
    }

    System.out.println( "Resizing 2 " + image.toString() );

    BufferedImage resizedImage = new BufferedImage( 64, 64, BufferedImage.TYPE_INT_ARGB );

    System.out.println( "Resizing 3 " + image.toString() );

    Graphics2D g2 = resizedImage.createGraphics();

    System.out.println( "Resizing 4 " + image.toString() );

    g2.drawImage( img, x, y, nw, nh, null );

    System.out.println( "Repainting " + image.toString() );


    view.repaint(); // TODO: What if future isn't updated yet by the time view tries to use it? We might update but show blank!

    System.out.println( "Thumbnailed " + image.toString() );

    return resizedImage;
  }
}
