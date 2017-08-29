package repositorio;


/**
* repositorio/IRepositorioOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Tuesday, August 29, 2017 12:22:07 AM CEST
*/

public interface IRepositorioOperations 
{

  ///////////////////////////////////
  repositorio.IRepositorio padre ();

  ///////////////////////////////////
  void padre (repositorio.IRepositorio newPadre);
  String[] subordinados ();
  void subordinados (String[] newSubordinados);
  String nombre ();
  void nombre (String newNombre);

  // TODO B�squeda por palabras clave
  repositorio.Coincidencia[] buscar (String palabraClave);

  // TODO Recuperar documentos
  void iniciarDescarga (String nombre);
  byte[] solicitarBloque (String nombre);

  // Registrar repositorios subordinados
  String registrar (repositorio.IRepositorio referencia);
  String registrarConNombre (repositorio.IRepositorio referencia, String nombre);

  // Dar de baja un repositorio
  void baja (String nombre);
} // interface IRepositorioOperations
