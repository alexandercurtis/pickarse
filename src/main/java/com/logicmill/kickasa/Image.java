package com.logicmill.kickasa;

import net.jcip.annotations.GuardedBy;

import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 19/12/13
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 *
 * Needs to be thread safe
 */
public class Image implements Comparable<Image>
{
  private final Path filename;

  @GuardedBy( "this" )
  private Future<java.awt.Image> thumbnail;

  public Image( Path filename )
  {
    if( filename == null )
    {
      throw new NullPointerException( "filename must not be null" );
    }
    this.filename = filename;
  }

  @Override
  /**
   * @throws NullPointerException if other is null
   */
  public int compareTo( Image other )
  {
    int result;
    //// First compare just the file name. If they match, compare the full paths as well.
    //result = filename.getFileName().compareTo( other.filename.getFileName() );
    //if( result == 0 )
    {
      result = filename.compareTo( other.filename );
    }
    return result;
  }

  @Override
  public boolean equals( Object obj )
  {
    if( obj instanceof Image )
    {
      return filename.equals( ((Image)obj).filename );
    }
    return false;
  }

  public Path getPath()
  {
    return filename;
  }

  /**
   * Called from GUI thread. Blocks on future.
   */
  public synchronized java.awt.Image getThumbnail()
  {
    java.awt.Image result = null;
    if( thumbnail != null && thumbnail.isDone() )
    {
      try
      {
        result = thumbnail.get();
      }
      catch( InterruptedException e )
      {
        // TODO: Show broken thumbnail placeholder image
      }
      catch( ExecutionException e )
      {
        // TODO: Show broken thumbnail placeholder image
      }
      catch( CancellationException e )
      {
        // TODO: Show broken thumbnail placeholder image
      }
    }
    return result;
  }

  @Override
  public String toString()
  {
    return filename.toString();
  }

  /**
   * Called from main thread
   */
  public synchronized void setThumbnail( Future<java.awt.Image> thumbnail )
  {
    this.thumbnail = thumbnail;
  }

  public synchronized boolean thumbnailingNotStarted()
  {
    return thumbnail == null;
  }
}
