package repositorio;

/**
* repositorio/IRepositorioHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Tuesday, August 29, 2017 6:17:14 PM CEST
*/

public final class IRepositorioHolder implements org.omg.CORBA.portable.Streamable
{
  public repositorio.IRepositorio value = null;

  public IRepositorioHolder ()
  {
  }

  public IRepositorioHolder (repositorio.IRepositorio initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = repositorio.IRepositorioHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    repositorio.IRepositorioHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return repositorio.IRepositorioHelper.type ();
  }

}
