package repositorio;


/**
* repositorio/Coincidencia.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Tuesday, August 29, 2017 6:17:14 PM CEST
*/

public final class Coincidencia implements org.omg.CORBA.portable.IDLEntity
{
  public String nombreRepositorio = null;
  public repositorio.IRepositorio repositorio = null;
  public String nombre = null;
  public String archivo = null;
  public String palabrasClave[] = null;
  public String comentario = null;

  public Coincidencia ()
  {
  } // ctor

  public Coincidencia (String _nombreRepositorio, repositorio.IRepositorio _repositorio, String _nombre, String _archivo, String[] _palabrasClave, String _comentario)
  {
    nombreRepositorio = _nombreRepositorio;
    repositorio = _repositorio;
    nombre = _nombre;
    archivo = _archivo;
    palabrasClave = _palabrasClave;
    comentario = _comentario;
  } // ctor

} // class Coincidencia
