package com.logicmill.kickasa;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 19/12/13
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 *
 */
public class ImageTester extends SimpleFileVisitor<Path> implements Runnable
{
  private Model model;
  private final static Path PROC = Paths.get( "/proc" );

  private Gui view;

  public void setView( Gui view )
  {
    this.view = view;
  }

  public ImageTester( Model model )
  {
    this.model = model;
  }

  /**
   * Runs in fv thread
   */
  @Override
  public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
  {
    boolean isImage = test( file );
    if( isImage )
    {
      model.addImage( new Image( file ) );
    }
    return FileVisitResult.CONTINUE;
  }

  private boolean test( Path file )
  {
//    System.out.println( "TEST: " + file.toString() );
    // TODO: A better test based on file content
    String name = file.getFileName().toString();
    int index = name.lastIndexOf( '.' );
    if( index >= 0 )
    {
      String extn = name.substring( index ).toLowerCase();
      if( ".jpg".equals( extn ) )
      {
        return true;
      }
      if( ".jpeg".equals( extn ) )
      {
        return true;
      }
      if( ".png".equals( extn ) )
      {
        return true;
      }
      if( ".gif".equals( extn ) )
      {
        return true;
      }
      if( ".bmp".equals( extn ) )
      {
        return true;
      }

    }
    return false;
  }

  /**
   * Runs in fv thread
   */
  @Override
  public FileVisitResult visitFileFailed( Path file, IOException exc ) throws IOException
  {
    System.err.println( "Failed: " + file.toString() + " " + exc.getMessage() );
    return FileVisitResult.CONTINUE;
  }

  /**
   * Runs in fv thread
   */
  @Override
  public void run()
  {
    Path start = Paths.get( "/" );
    try
    {
//      Files.walkFileTree( start, EnumSet.of( FileVisitOption.FOLLOW_LINKS ), Integer.MAX_VALUE, this );
      Files.walkFileTree( start, this );
    }
    catch( IOException e )
    {
      System.err.println( "IOException: " + e.toString() );
      e.printStackTrace(System.err);
    }
  }

  @Override
  public FileVisitResult postVisitDirectory( Path dir, IOException exc ) throws IOException
  {
    Objects.requireNonNull( dir );
    if (exc != null)
    {
      System.err.println( "Failed DIR: " + exc.toString() );
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs ) throws IOException
  {
    if( dir.startsWith( PROC ) )
    {
      return FileVisitResult.SKIP_SUBTREE;
    }
    if( dir.endsWith( "Application Data" ) )
    {
      System.out.println( "Ignoring Application Data system folder: " + dir );
      return FileVisitResult.SKIP_SUBTREE;
    }

/*
    try {
      DosFileAttributes attr =
        Files.readAttributes(dir, DosFileAttributes.class);
*/
/*      if( attr.isSystem() )
      {
        System.out.println( "Ignoring DOS system folder: " + dir );
        return FileVisitResult.SKIP_SUBTREE;
      }*//*

      if( attr.isHidden() )
      {
        System.out.println( "Ignoring DOS hidden folder: " + dir );
        return FileVisitResult.SKIP_SUBTREE;
      }
    } catch (UnsupportedOperationException x) {
    }
*/

/*
    if( dir.toString().indexOf( "Application Data" ) > 0 )
    {

      try {
        DosFileAttributes attr =
          Files.readAttributes(dir, DosFileAttributes.class);
        System.out.println("isReadOnly is " + attr.isReadOnly());
        System.out.println("isHidden is " + attr.isHidden());
        System.out.println("isArchive is " + attr.isArchive());
        System.out.println("isSystem is " + attr.isSystem());
      } catch (UnsupportedOperationException x) {
        System.err.println("DOS file" +
          " attributes not supported:" + x);
      }
      BasicFileAttributes attr = Files.readAttributes(dir, BasicFileAttributes.class);

      System.out.println("creationTime: " + attr.creationTime());
      System.out.println("lastAccessTime: " + attr.lastAccessTime());
      System.out.println("lastModifiedTime: " + attr.lastModifiedTime());

      System.out.println("isDirectory: " + attr.isDirectory());
      System.out.println("isOther: " + attr.isOther());
      System.out.println("isRegularFile: " + attr.isRegularFile());
      System.out.println("isSymbolicLink: " + attr.isSymbolicLink());
      System.out.println("size: " + attr.size());
    }*/

    return FileVisitResult.CONTINUE;
  }
}
