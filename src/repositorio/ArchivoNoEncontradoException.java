package repositorio;


/**
* repositorio/ArchivoNoEncontradoException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Wednesday, August 30, 2017 12:59:48 AM CEST
*/

public final class ArchivoNoEncontradoException extends org.omg.CORBA.UserException
{
  public String message = null;

  public ArchivoNoEncontradoException ()
  {
    super(ArchivoNoEncontradoExceptionHelper.id());
  } // ctor

  public ArchivoNoEncontradoException (String _message)
  {
    super(ArchivoNoEncontradoExceptionHelper.id());
    message = _message;
  } // ctor


  public ArchivoNoEncontradoException (String $reason, String _message)
  {
    super(ArchivoNoEncontradoExceptionHelper.id() + "  " + $reason);
    message = _message;
  } // ctor

} // class ArchivoNoEncontradoException