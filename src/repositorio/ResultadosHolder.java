package repositorio;


/**
* repositorio/ResultadosHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Tuesday, August 29, 2017 4:56:39 PM CEST
*/

public final class ResultadosHolder implements org.omg.CORBA.portable.Streamable
{
  public repositorio.Coincidencia value[] = null;

  public ResultadosHolder ()
  {
  }

  public ResultadosHolder (repositorio.Coincidencia[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = repositorio.ResultadosHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    repositorio.ResultadosHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return repositorio.ResultadosHelper.type ();
  }

}
