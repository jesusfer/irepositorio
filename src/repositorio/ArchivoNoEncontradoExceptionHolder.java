package repositorio;

/**
* repositorio/ArchivoNoEncontradoExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Wednesday, August 30, 2017 4:08:42 PM CEST
*/

public final class ArchivoNoEncontradoExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public repositorio.ArchivoNoEncontradoException value = null;

  public ArchivoNoEncontradoExceptionHolder ()
  {
  }

  public ArchivoNoEncontradoExceptionHolder (repositorio.ArchivoNoEncontradoException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = repositorio.ArchivoNoEncontradoExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    repositorio.ArchivoNoEncontradoExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return repositorio.ArchivoNoEncontradoExceptionHelper.type ();
  }

}
