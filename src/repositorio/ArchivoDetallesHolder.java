package repositorio;

/**
* repositorio/ArchivoDetallesHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Tuesday, August 29, 2017 12:22:07 AM CEST
*/

public final class ArchivoDetallesHolder implements org.omg.CORBA.portable.Streamable
{
  public repositorio.ArchivoDetalles value = null;

  public ArchivoDetallesHolder ()
  {
  }

  public ArchivoDetallesHolder (repositorio.ArchivoDetalles initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = repositorio.ArchivoDetallesHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    repositorio.ArchivoDetallesHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return repositorio.ArchivoDetallesHelper.type ();
  }

}
